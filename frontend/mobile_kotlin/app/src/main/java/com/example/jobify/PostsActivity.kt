package com.example.jobify

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.jobify.ui.posts.PostsScreen
import com.example.jobify.ui.theme.AppTheme
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerContent {
                    // Navigation logic
                    when (it) {
                        "Find Job" -> scope.launch { drawerState.close() }
                        "Profile" -> context.startActivity(Intent(context, RecruiterProfileActivity::class.java))
                        "Correct CV" -> context.startActivity(Intent(context, CvCorrectionActivity::class.java))
                        "Log Out" -> {
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFFF0F2F5), // Light grey background
            topBar = {
                TopAppBar(
                    title = { Text("Job Postings") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            PostsScreen(modifier = Modifier.padding(paddingValues))
        }
    }
}

@Composable
private fun NavigationDrawerContent(onItemClick: (String) -> Unit) {
    Column {
        NavigationDrawerItem(
            label = { Text("Find Job") },
            selected = true,
            onClick = { onItemClick("Find Job") }
        )
        NavigationDrawerItem(
            label = { Text("Profile") },
            selected = false,
            onClick = { onItemClick("Profile") }
        )
        NavigationDrawerItem(
            label = { Text("Correct CV") },
            selected = false,
            onClick = { onItemClick("Correct CV") }
        )
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Log Out") },
            selected = false,
            onClick = { onItemClick("Log Out") }
        )
    }
}
