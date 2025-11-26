package com.example.jobify.ui.drawer

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobify.R

@Composable
fun RecruiterDrawerContent(
    onPublishJobClick: () -> Unit,
    onInterviewsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
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
            onClick = onLogoutClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Profile
        RecruiterProfileSection()

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
private fun RecruiterProfileSection() {
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
                    "Sarah Johnson",
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
