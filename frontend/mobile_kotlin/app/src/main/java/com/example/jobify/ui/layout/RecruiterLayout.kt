package com.example.jobify.ui.layout

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.jobify.InterviewsActivity
import com.example.jobify.MainActivity
import com.example.jobify.RecruiterProfileActivity
import com.example.jobify.ui.drawer.RecruiterDrawerContent
import kotlinx.coroutines.launch

/**
 * Wrapper layout for all recruiter pages with navigation drawer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruiterLayout(
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                RecruiterDrawerContent(
                    onPublishJobClick = {
                        scope.launch { drawerState.close() }
                    },
                    onInterviewsClick = {
                        context.startActivity(Intent(context, InterviewsActivity::class.java))
                        scope.launch { drawerState.close() }
                    },
                    onSettingsClick = {
                        context.startActivity(Intent(context, RecruiterProfileActivity::class.java))
                        scope.launch { drawerState.close() }
                    },
                    onLogoutClick = {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFFF0F2F5),
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}

