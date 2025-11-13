package com.example.jobify

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.jobify.data.InterviewManager
import com.example.jobify.ui.interviews.InterviewsScreen
import com.example.jobify.ui.layout.RecruiterLayout
import com.example.jobify.ui.theme.AppTheme

class InterviewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("InterviewsActivity", "Created - Initializing InterviewManager")
        InterviewManager.init(this)
        InterviewManager.refresh()

        setContent {
            AppTheme {
                InterviewsApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("InterviewsActivity", "Resumed - Refreshing interviews")
        InterviewManager.refresh()
    }
}

@Composable
fun InterviewsApp() {
    Log.d("InterviewsApp", ">>> InterviewsApp COMPOSING <<<")
    Log.d("InterviewsApp", "Getting interviews from InterviewManager...")
    val allInterviews = InterviewManager.getAllInterviews()
    Log.d("InterviewsApp", "InterviewManager returned ${allInterviews.size} interviews")
    allInterviews.forEachIndexed { index, intv ->
        Log.d("InterviewsApp", "  [$index] ${intv.candidateName} - ${intv.date} - ${intv.time}")
    }

    // Use MutableList from InterviewManager directly to get live updates
    val interviewsList = remember { InterviewManager.getAllInterviews().toMutableList() }

    Log.d("InterviewsApp", "Composing with ${interviewsList.size} interviews")

    RecruiterLayout(
        title = "Interviews"
    ) { paddingValues ->
        Log.d("InterviewsApp", "Rendering InterviewsScreen with ${interviewsList.size} interviews")
        interviewsList.forEachIndexed { index, intv ->
            Log.d("InterviewsApp", "  Passing [$index] ${intv.candidateName}")
        }
        InterviewsScreen(
            modifier = Modifier.padding(paddingValues),
            scheduledInterviews = interviewsList,
            onRefresh = {
                Log.d("InterviewsScreen", "onRefresh called")
                interviewsList.clear()
                interviewsList.addAll(InterviewManager.getAllInterviews())
                Log.d("InterviewsScreen", "Refreshed: ${interviewsList.size} interviews")
            }
        )
    }
}

