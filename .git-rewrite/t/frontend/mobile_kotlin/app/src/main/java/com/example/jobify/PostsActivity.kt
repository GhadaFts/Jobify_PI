package com.example.jobify

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
class PostsActivity : AppCompatActivity(), AddPostDialogFragment.OnPostAddedListener {

    private lateinit var rvPosts: RecyclerView
    private lateinit var btnAddPost: View
    private lateinit var btnMenu: View
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var menuHomeLayout: View
    private lateinit var menuProfileLayout: View
    private lateinit var menuLogoutLayout: View

    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<JobPost>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        rvPosts = findViewById(R.id.rvPosts)
        btnAddPost = findViewById(R.id.btnAddPost)
        btnMenu = findViewById(R.id.btnMenu)
        drawerLayout = findViewById(R.id.drawerLayout)

        menuHomeLayout = findViewById(R.id.menuHomeLayout)
        menuProfileLayout = findViewById(R.id.menuProfileLayout)
        menuLogoutLayout = findViewById(R.id.menuLogoutLayout)

        // Mock data
        posts.addAll(
            listOf(
                JobPost(
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
                JobPost(
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
        )

        rvPosts.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(posts)
        rvPosts.adapter = postAdapter

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

        menuHomeLayout.setOnClickListener {
            startActivity(Intent(this, PostsActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuProfileLayout.setOnClickListener {
            startActivity(Intent(this, RecruiterProfileActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuLogoutLayout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onPostAdded(post: JobPost) {
        posts.add(post)
        postAdapter.notifyItemInserted(posts.size - 1)
        rvPosts.scrollToPosition(posts.size - 1)
    }
}
