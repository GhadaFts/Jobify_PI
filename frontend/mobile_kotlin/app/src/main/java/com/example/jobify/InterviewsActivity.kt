package com.example.jobify

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.jobify.data.InterviewManager
import com.example.jobify.model.ScheduledInterview
import com.example.jobify.ui.interviews.InterviewsScreen
import com.example.jobify.ui.layout.RecruiterLayout
import com.example.jobify.ui.theme.AppTheme

class InterviewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("InterviewsActivity", "Created - Initializing InterviewManager")
        InterviewManager.init(this)

        setContent {
            AppTheme {
                InterviewsApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("InterviewsActivity", "Resumed - Refreshing interviews from backend")
    }
}

@Composable
fun InterviewsApp() {
    Log.d("InterviewsApp", ">>> InterviewsApp COMPOSING <<<")
    
    // State to hold interviews list
    var interviewsList by remember { mutableStateOf<List<ScheduledInterview>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch interviews on first composition
    LaunchedEffect(Unit) {
        Log.d("InterviewsApp", "LaunchedEffect - Fetching interviews from backend")
        isLoading = true
        InterviewManager.refresh { success, error ->
            isLoading = false
            if (success) {
                interviewsList = InterviewManager.getAllInterviews()
                Log.d("InterviewsApp", "✅ Successfully loaded ${interviewsList.size} interviews")
            } else {
                errorMessage = error
                Log.e("InterviewsApp", "❌ Failed to load interviews: $error")
            }
        }
    }

    RecruiterLayout(
        title = "Interviews"
    ) { paddingValues ->
        InterviewsScreen(
            modifier = Modifier.padding(paddingValues),
            scheduledInterviews = interviewsList,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onRefresh = {
                Log.d("InterviewsScreen", "onRefresh called - Fetching from backend")
                isLoading = true
                errorMessage = null
                InterviewManager.refresh { success, error ->
                    isLoading = false
                    if (success) {
                        interviewsList = InterviewManager.getAllInterviews()
                        Log.d("InterviewsScreen", "Refreshed: ${interviewsList.size} interviews")
                    } else {
                        errorMessage = error
                        Log.e("InterviewsScreen", "Refresh failed: $error")
                    }
                }
            },
            onCancelInterview = { interviewId ->
                Log.d("InterviewsScreen", "onCancelInterview called for ID: $interviewId")
                InterviewManager.cancelInterview(interviewId.toLong()) { success, error ->
                    if (success) {
                        Log.d("InterviewsScreen", "Interview cancelled successfully")
                        // Refresh the list after cancellation
                        InterviewManager.refresh { refreshSuccess, refreshError ->
                            if (refreshSuccess) {
                                interviewsList = InterviewManager.getAllInterviews()
                            }
                        }
                    } else {
                        Log.e("InterviewsScreen", "Failed to cancel interview: $error")
                        errorMessage = error
                    }
                }
            }
        )
    }
}

