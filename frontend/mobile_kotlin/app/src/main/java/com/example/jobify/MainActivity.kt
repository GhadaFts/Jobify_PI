package com.example.jobify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.ScaleAnimation
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

        // Recruiter → Open Recruiter posts
        cardRecruiter.setOnClickListener {
            val intent = Intent(this, PostsActivity::class.java)
            startActivity(intent)
        }


        fun addHoverEffect(card: CardView) {
            card.setOnTouchListener { v, event ->
                val scaleUp = ScaleAnimation(
                    1f, 1.05f, 1f, 1.05f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f
                ).apply {
                    duration = 150
                    fillAfter = true
                }
                val scaleDown = ScaleAnimation(
                    1.05f, 1f, 1.05f, 1f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f
                ).apply {
                    duration = 150
                    fillAfter = true
                }
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> v.startAnimation(scaleUp)
                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> v.startAnimation(scaleDown)
                }
                false
            }
        }

        addHoverEffect(cardJobSeeker)
        addHoverEffect(cardRecruiter)


    }
}
