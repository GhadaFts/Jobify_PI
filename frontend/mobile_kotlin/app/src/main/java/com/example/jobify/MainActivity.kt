package com.example.jobify

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val splashDuration = 5000L // 5 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start animations
        startAnimations()

        // Navigate after 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, splashDuration)
    }

    private fun startAnimations() {
        // Get views
        val ivTopLeft = findViewById<View>(R.id.ivTopLeft)
        val ivTopRight = findViewById<View>(R.id.ivTopRight)
        val ivBottomLeft = findViewById<View>(R.id.ivBottomLeft)
        val ivBottomRight = findViewById<View>(R.id.ivBottomRight)
        val cvLogo = findViewById<View>(R.id.cvLogo)
        val ivLogo = findViewById<View>(R.id.ivLogo)
        val llAppName = findViewById<View>(R.id.llAppName)
        val tvWelcome = findViewById<View>(R.id.tvWelcome)

        // Animate decorations - fade in and gentle rotation
        animateDecoration(ivTopLeft, 800, 0, -15f, 5f)
        animateDecoration(ivTopRight, 900, 100, 20f, -10f)
        animateDecoration(ivBottomLeft, 850, 200, 10f, -5f)
        animateDecoration(ivBottomRight, 900, 150, -12f, 8f)

        // Logo container - scale up and fade in
        cvLogo?.apply {
            alpha = 0f
            scaleX = 0.3f
            scaleY = 0.3f

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1200)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        // Logo icon - rotate while scaling
        ivLogo?.apply {
            rotation = -180f
            ObjectAnimator.ofFloat(this, View.ROTATION, -180f, 0f).apply {
                duration = 1200
                interpolator = DecelerateInterpolator()
                start()
            }
        }

        // App name - slide from left with fade
        llAppName?.apply {
            alpha = 0f
            translationX = -200f
            animate()
                .alpha(1f)
                .translationX(0f)
                .setStartDelay(600)
                .setDuration(1000)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        // Welcome text - fade in
        tvWelcome?.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setStartDelay(1000)
                .setDuration(800)
                .start()
        }
    }

    private fun animateDecoration(
        view: View?,
        duration: Long,
        startDelay: Long,
        fromRotation: Float,
        toRotation: Float
    ) {
        view?.apply {
            alpha = 0f
            rotation = fromRotation

            // Fade in
            animate()
                .alpha(view.alpha.coerceAtLeast(0.11f))
                .setStartDelay(startDelay)
                .setDuration(duration)
                .start()

            // Gentle rotation animation (continuous)
            ObjectAnimator.ofFloat(this, View.ROTATION, fromRotation, toRotation).apply {
                this.duration = 3000
                this.startDelay = startDelay
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }
    }

    private fun navigateToNextScreen() {
        // Check if user is already logged in
        val sessionManager = SessionManager(this)

        // Get root view for fade out
        val rootView = findViewById<View>(android.R.id.content)

        // Fade out animation
        rootView.animate()
            .alpha(0f)
            .setDuration(600)
            .withEndAction {
                val intent = if (sessionManager.isLoggedIn()) {
                    // Navigate based on saved role
                    when (sessionManager.getUserRole()) {
                        "jobseeker" -> Intent(this, JobOpportunitiesActivity::class.java)
                        "recruiter" -> Intent(this, PostsActivity::class.java)
                        else -> Intent(this, LoginActivity::class.java)
                    }
                } else {
                    // No active session, go to login
                    Intent(this, LoginActivity::class.java)
                }

                startActivity(intent)
                finish()
                // Smooth transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            .start()
    }


}
