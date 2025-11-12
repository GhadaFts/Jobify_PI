package com.example.jobify

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.jobify.ui.interviews.InterviewsScreen
import com.example.jobify.ui.layout.RecruiterLayout
import com.example.jobify.ui.theme.AppTheme

class InterviewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                InterviewsApp()
            }
        }
    }
}

@Composable
fun InterviewsApp() {
    RecruiterLayout(
        title = "Interviews"
    ) { paddingValues ->
        InterviewsScreen(modifier = Modifier.padding(paddingValues))
    }
}

