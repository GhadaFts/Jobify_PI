package com.example.jobify

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.jobify.model.Applicant
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import android.content.Intent
import android.net.Uri

class ApplicantDetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_APPLICANT = "extra_applicant"
    }

    private var applicant: Applicant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicant = intent.getParcelableExtra(EXTRA_APPLICANT)
        setContent {
            MaterialTheme {
                ApplicantDetailsScreen(applicant = applicant, onOpenLink = { url ->
                    try {
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(i)
                    } catch (_: Exception) {}
                })
            }
        }
    }
}

@Composable
fun ApplicantDetailsScreen(applicant: Applicant?, onOpenLink: (String) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile header with image and name/title
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (!applicant?.profileImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = applicant!!.profileImageUrl,
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 12.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = applicant?.name ?: "Unknown", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = applicant?.title ?: "", color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = applicant?.title ?: "", color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Contact Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        val contact = listOfNotNull(applicant?.email, applicant?.phone).joinToString(" • ")
        Text(text = if (contact.isNotBlank()) contact else "-")
        Text(text = "Nationality: ${applicant?.nationality ?: ""}")
        Text(text = "Date of Birth: ${applicant?.dateOfBirth ?: ""}")
        Text(text = "Gender: ${applicant?.gender ?: ""}")

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Application Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        // Format applied date if it's a timestamp
        val appliedDateText = try {
            val t = applicant?.appliedDate ?: 0L
            if (t <= 0L) "-" else java.time.Instant.ofEpochMilli(t).atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString()
        } catch (_: Exception) { "-" }
        Text(text = "Application Date: $appliedDateText")

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Skills", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = if (applicant?.skills?.isNotEmpty() == true) applicant!!.skills.joinToString(", ") else "-")

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Curriculum Vitae", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        if (!applicant?.cvLink.isNullOrEmpty()) {
            Button(onClick = { onOpenLink(applicant!!.cvLink!!) }) { Text(text = "Download") }
        } else {
            Text(text = "No CV provided")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Motivation Letter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = applicant?.motivation ?: "No motivation letter provided.")

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Professional Experience", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        if (applicant?.experienceList?.isNotEmpty() == true) {
            applicant.experienceList.forEach { exp ->
                Text(text = "• $exp")
                Spacer(modifier = Modifier.height(6.dp))
            }
        } else {
            Text(text = "No experience details")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Education", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        if (applicant?.educationList?.isNotEmpty() == true) {
            applicant.educationList.forEach { edu ->
                Text(text = "• $edu")
                Spacer(modifier = Modifier.height(6.dp))
            }
        } else {
            Text(text = "No education details")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Socials", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        if (applicant?.socials?.isNotEmpty() == true) {
            applicant.socials.forEach { (k, v) ->
                Text(text = "$k: $v", modifier = Modifier.clickable { onOpenLink(v) })
                Spacer(modifier = Modifier.height(6.dp))
            }
        } else {
            Text(text = "No social links")
        }
    }
}
