package com.example.jobify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardJobSeeker = findViewById<CardView>(R.id.cardJobSeeker)
        val cardRecruiter = findViewById<CardView>(R.id.cardRecruiter)

        // Job Seeker → Open Job Opportunities
        cardJobSeeker.setOnClickListener {
            val intent = Intent(this, JobOpportunitiesActivity::class.java)
            startActivity(intent)
        }

        // Recruiter → Open Recruiter Profile
        cardRecruiter.setOnClickListener {
            val intent = Intent(this, RecruiterProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
