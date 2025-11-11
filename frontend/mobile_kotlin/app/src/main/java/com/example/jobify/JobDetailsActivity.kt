package com.example.jobify

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.jobify.model.Job
import com.example.jobify.ui.theme.AppTheme

class JobDetailsActivity : ComponentActivity() {

    private val job: Job? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(JOB_EXTRA, Job::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(JOB_EXTRA)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                if (job != null) {
                    JobDetailsScreen(job = job!!, onNavigateUp = { finish() })
                } else {
                    // Handle error case where job data is missing
                    finish()
                }
            }
        }
    }

    companion object {
        private const val JOB_EXTRA = "job_extra"

        fun newIntent(context: Context, job: Job): Intent {
            return Intent(context, JobDetailsActivity::class.java).apply {
                putExtra(JOB_EXTRA, job)
            }
        }
    }
}
