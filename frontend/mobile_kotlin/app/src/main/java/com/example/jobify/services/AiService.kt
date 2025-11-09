package com.example.jobify.services

import android.util.Log
import com.example.jobify.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

private const val TAG = "AiService"

// Public exception type you can catch from the UI
class AiServiceException(message: String) : Exception(message)

object AiService {
    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Use the model ID only (no leading "models/"). Example values you saw in the 'models' list:
     *   - gemini-flash-latest
     *   - gemini-2.5-flash
     *   - gemini-2.5-pro
     *
     * If you want to use "models/gemini-2.5-flash" literally, set MODEL_ID to "models/gemini-2.5-flash"
     * and change the URL construction accordingly — but using an ID string is simpler and safer.
     */
    private const val MODEL_ID = "gemini-flash-latest"

    // Full default prompt (schema + instructions). Keep the JSON structure in the prompt.
    private const val defaultPrompt = """You are a professional CV analyzer. 
You will receive the raw text extracted from a user's CV.

Your goal is to analyze it and return a JSON object (NO explanations, NO markdown, NO text outside JSON).
The JSON must strictly follow this structure:

{
  "cvScore": number, // overall score out of 100
  "cvSuggestions": [
    {
      "id": string, // unique id like "weak_1" or "missing_3"
      "type": "success" | "warning" | "info" | "missing",
      "title": string,
      "message": string
    }
  ],
  "improvedSummary": {
    "overallAssessment": string,
    "strengths": string[],
    "improvements": string[]
  },
  "profile": {
    "name": string,
    "title": string,
    "email": string,
    "phone": string,
    "nationality": string,
    "summary": string,
    "skills": string[],
    "experience": [
      {
        "position": string,
        "company": string,
        "startDate": string,
        "endDate": string,
        "description": string
      }
    ],
    "education": [
      {
        "degree": string,
        "field": string,
        "school": string,
        "graduationDate": string
      }
    ]
  }
}

Guidelines for the analysis:
- Identify STRONG sections (good content) → type = "success"
- Identify WEAK or unclear sections → type = "warning"
- Identify INFO or general improvement tips → type = "info"
- Identify MISSING sections (e.g., missing contact info, summary, education) → type = "missing"
- Score between 0–100 based on completeness, clarity, and structure.

Return only JSON.
Now analyze this CV:
"""

    /**
     * Analyze CV text using the Generative Language REST endpoint.
     * Returns the generated text (or throws AiServiceException on error).
     */
    suspend fun analyzeCv(cvText: String): String = withContext(Dispatchers.IO) {
        val fullPrompt = "$defaultPrompt\n\n$cvText"

        // Build request JSON: { "contents": [ { "parts": [ { "text": "..." } ] } ] }
        val partsArray = JSONArray().apply { put(JSONObject().put("text", fullPrompt)) }
        val contentObj = JSONObject().put("parts", partsArray)
        val contentsArray = JSONArray().apply { put(contentObj) }
        val bodyJson = JSONObject().put("contents", contentsArray).toString()

        // CORRECT URL: don't duplicate 'models/' — the model id alone goes after /models/
        val url =
            "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_ID:generateContent?key=${BuildConfig.GEMINI_API_KEY}"

        val requestBody = bodyJson.toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val code = response.code
                val respBody = response.body?.string()
                val headers = response.headers

                Log.d(TAG, "Generative API response code=$code")
                Log.d(TAG, "Generative API response headers: ${headers.toMultimap()}")
                Log.d(TAG, "Generative API response body: $respBody")

                // Helpful quick checks for common auth / permission issues
                if (!response.isSuccessful) {
                    val serverMsg = extractErrorMessage(respBody)
                    when (code) {
                        401 -> throw AiServiceException("Unauthorized (401). API key invalid or not provided. Details: $serverMsg")
                        403 -> throw AiServiceException("Forbidden (403). API key not allowed to use this model or API not enabled. Details: $serverMsg")
                        404 -> throw AiServiceException("Not found (404). Model or endpoint not found. Check MODEL_ID and that the Generative Language API is enabled. Details: $serverMsg")
                        429 -> throw AiServiceException("Rate limit (429). You may have exceeded quota. Details: $serverMsg")
                        400 -> throw AiServiceException("Bad Request (400). The request body may be invalid. Details: $serverMsg")
                        else -> throw AiServiceException("AI service error: HTTP $code. Details: $serverMsg")
                    }
                }

                if (respBody.isNullOrEmpty()) {
                    throw AiServiceException("AI service returned an empty response.")
                }

                // Try a few strategies to find the generated text
                try {
                    val json = JSONObject(respBody)

                    // Strategy A: candidates -> content -> parts -> text
                    if (json.has("candidates")) {
                        val candidates = json.optJSONArray("candidates")
                        if (candidates != null && candidates.length() > 0) {
                            val candidate = candidates.optJSONObject(0)
                            val content = candidate?.optJSONObject("content")
                            val parts = content?.optJSONArray("parts")
                            if (parts != null && parts.length() > 0) {
                                val firstPart = parts.optJSONObject(0)
                                val text = firstPart?.optString("text")
                                if (!text.isNullOrEmpty()) return@withContext text
                            }
                        }
                    }

                    // Strategy B: output / outputText fields
                    if (json.has("output")) {
                        val out = json.opt("output")
                        if (out is JSONObject) {
                            val t = out.optString("text", out.optString("outputText", null))
                            if (!t.isNullOrEmpty()) return@withContext t
                        } else if (out is String && out.isNotEmpty()) {
                            return@withContext out
                        }
                    }

                    // Strategy C: depth-first search for first "text" field
                    val found = findFirstText(json)
                    if (!found.isNullOrEmpty()) return@withContext found

                    // Fallback: return entire response so caller can inspect
                    return@withContext respBody
                } catch (parseEx: Exception) {
                    Log.e(TAG, "Error parsing response JSON", parseEx)
                    throw AiServiceException("Failed to parse AI response: ${parseEx.message}. Raw: $respBody")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error calling Generative API", e)
            throw AiServiceException("Network error calling AI service: ${e.message}")
        }
    }

    // Depth-first search in JSON for a "text" value
    private fun findFirstText(node: JSONObject): String? {
        val keys = node.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = node.opt(key)
            when (value) {
                is JSONObject -> {
                    val maybe = value.optString("text", null)
                    if (!maybe.isNullOrEmpty()) return maybe
                    val deeper = findFirstText(value)
                    if (!deeper.isNullOrEmpty()) return deeper
                }
                is JSONArray -> {
                    for (i in 0 until value.length()) {
                        val item = value.opt(i)
                        if (item is JSONObject) {
                            val got = findFirstText(item)
                            if (!got.isNullOrEmpty()) return got
                        }
                    }
                }
                is String -> {
                    // ignore bare strings
                }
            }
        }
        return null
    }

    // Extract message from Google's error JSON or return raw body
    private fun extractErrorMessage(body: String?): String {
        if (body.isNullOrEmpty()) return "No details from server."
        return try {
            val json = JSONObject(body)
            if (json.has("error")) {
                val err = json.get("error")
                if (err is JSONObject) {
                    val message = err.optString("message", null)
                    if (!message.isNullOrEmpty()) return message
                    return err.toString()
                }
            }
            // sometimes servers return {"message":"..."} at top-level
            if (json.has("message")) return json.optString("message")
            body
        } catch (e: Exception) {
            // Not JSON — return raw string
            body
        }
    }
}
