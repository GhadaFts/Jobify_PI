package com.example.jobify

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class PostsActivity : AppCompatActivity() {

    private lateinit var rvPosts: RecyclerView
    private lateinit var btnAddPost: View
    private lateinit var btnMenu: View
    private lateinit var drawerLayout: DrawerLayout

    // Menu items
    private lateinit var menuHomeLayout: View
    private lateinit var menuProfileLayout: View
    private lateinit var menuLogoutLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        // Views
        rvPosts = findViewById(R.id.rvPosts)
        btnAddPost = findViewById(R.id.btnAddPost)
        btnMenu = findViewById(R.id.btnMenu)
        drawerLayout = findViewById(R.id.drawerLayout)

        menuHomeLayout = findViewById(R.id.menuHomeLayout)
        menuProfileLayout = findViewById(R.id.menuProfileLayout)
        menuLogoutLayout = findViewById(R.id.menuLogoutLayout)

        // Mock data
        val posts = listOf(
            Post(
                id = 1,
                title = "Android Developer",
                jobPosition = "Full Time",
                experience = "2 years",
                salary = "2000$",
                description = "Develop Android apps",
                type = "IT",
                createdAt = Date(),
                status = "Active",
                requirements = listOf("Bachelor in CS"),
                skills = listOf("Kotlin", "Java"),
                published = true
            ),
            Post(
                id = 2,
                title = "Backend Developer",
                jobPosition = "Full Time",
                experience = "3 years",
                salary = "2500$",
                description = "Develop APIs",
                type = "IT",
                createdAt = Date(),
                status = "Active",
                requirements = listOf("Bachelor in CS"),
                skills = listOf("Spring Boot", "SQL"),
                published = true
            )
        )

        rvPosts.layoutManager = LinearLayoutManager(this)
        rvPosts.adapter = PostAdapter(posts)

        btnAddPost.setOnClickListener {
            // Open AddPostActivity
            startActivity(Intent(this, AddPostActivity::class.java))
        }

        // Ouvrir le drawer
        btnMenu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        btnAddPost.setOnClickListener {
            val dialog = AddPostDialogFragment()
            dialog.show(supportFragmentManager, "AddPostDialog")
        }

        // Menu clicks
        menuHomeLayout.setOnClickListener {
            startActivity(Intent(this, PostsActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuProfileLayout.setOnClickListener {
            // Open ProfileActivity
            startActivity(Intent(this, RecruiterProfileActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuLogoutLayout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }
}
