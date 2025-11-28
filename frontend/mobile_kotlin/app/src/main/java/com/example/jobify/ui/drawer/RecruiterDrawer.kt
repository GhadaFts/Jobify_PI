package com.example.jobify.ui.drawer

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobify.LoginActivity
import com.example.jobify.R
import com.example.jobify.SessionManager
import com.example.jobify.repository.AuthRepository

@Composable
fun RecruiterDrawerContent(
    onPublishJobClick: () -> Unit,
    onInterviewsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // Get user name from session
    val userName = sessionManager.getUserName() ?: "Sarah Johnson"

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.75f)
            .background(Color.White)
    ) {

        // ======= HEADER (LOGO REMOVED) =======
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(46.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Jobify",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF1F2937)
            )

            Text(
                "Recruiter Portal",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ======= MENU ITEMS =======
        DrawerMenuItem(
            icon = Icons.Default.Edit,
            label = "Publish Job",
            onClick = onPublishJobClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        DrawerMenuItem(
            icon = Icons.AutoMirrored.Filled.EventNote,
            label = "Interviews",
            onClick = onInterviewsClick
        )

        Spacer(modifier = Modifier.weight(1f))

        // Divider
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = Color(0xFFE5E7EB),
            thickness = 1.dp
        )

        DrawerMenuItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            onClick = onSettingsClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        DrawerMenuItem(
            icon = Icons.AutoMirrored.Filled.Logout,
            label = "Logout",
            onClick = {
                performLogout(context, sessionManager)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Profile
        RecruiterProfileSection(userName = userName)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(8.dp)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                label,
                fontSize = 16.sp,
                color = Color(0xFF374151),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun RecruiterProfileSection(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFE5E7EB),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Profile Image
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                color = Color(0xFFE5E7EB)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.applicant),
                    contentDescription = "Recruiter Profile",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1F2937)
                )

                Text(
                    "Recruitment\nManager",
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF),
                    lineHeight = 13.sp
                )
            }
        }
    }
}

// Logout function
private fun performLogout(context: Context, sessionManager: SessionManager) {
    val authRepository = AuthRepository()
    val accessToken = sessionManager.getAccessToken()
    val refreshToken = sessionManager.getRefreshToken()

    if (accessToken != null && refreshToken != null) {
        authRepository.logout(
            accessToken = accessToken,
            refreshToken = refreshToken,
            onSuccess = {
                sessionManager.clearSession()
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                navigateToLogin(context)
            },
            onError = {
                sessionManager.clearSession()
                Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                navigateToLogin(context)
            }
        )
    } else {
        sessionManager.clearSession()
        navigateToLogin(context)
    }
}

private fun navigateToLogin(context: Context) {
    val intent = Intent(context, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)

    if (context is android.app.Activity) {
        context.finish()
    }
}