package com.example.jobify

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.jobify.ui.layout.RecruiterLayout
import com.example.jobify.ui.posts.PostsScreen
import com.example.jobify.ui.theme.AppTheme

class PostsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                PostsApp()
            }
        }
    }
}

@Composable
fun PostsApp() {
    RecruiterLayout(
        title = "Job Postings"
    ) { paddingValues ->
        PostsScreen(modifier = Modifier.padding(paddingValues))
    }
}

