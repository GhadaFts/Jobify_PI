package com.example.jobify

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JobAdapter(
    private var jobs: MutableList<JobPost>,
    private val onApplyClick: (JobPost) -> Unit = {}
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    companion object {
        private const val TAG = "JobAdapter"
    }

    inner class JobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.job_title)
        val positionText: TextView = view.findViewById(R.id.job_position)
        val experienceText: TextView = view.findViewById(R.id.job_experience)
        val salaryText: TextView = view.findViewById(R.id.job_salary)
        val descriptionText: TextView = view.findViewById(R.id.job_description)
        val typeText: TextView = view.findViewById(R.id.job_type)
        val btnApply: Button = view.findViewById(R.id.btn_apply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        Log.d(TAG, "onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job_post, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder called for position $position")

        if (position >= jobs.size) {
            Log.e(TAG, "ERROR: Position $position is out of bounds! jobs.size = ${jobs.size}")
            return
        }

        val job = jobs[position]
        Log.d(TAG, "Binding job: ${job.title} (ID: ${job.id})")

        holder.titleText.text = job.title
        holder.positionText.text = job.jobPosition
        holder.experienceText.text = "Experience: ${job.experience}"
        holder.salaryText.text = job.salary
        holder.descriptionText.text = job.description
        holder.typeText.text = job.type

        // Change button text and style based on application status
        if (job.status == "Applied") {
            holder.btnApply.text = "Applied ✓"
            holder.btnApply.isEnabled = false
            holder.btnApply.alpha = 0.6f
        } else {
            holder.btnApply.text = "Apply Now"
            holder.btnApply.isEnabled = true
            holder.btnApply.alpha = 1.0f

            holder.btnApply.setOnClickListener {
                onApplyClick(job)
            }
        }
    }

    override fun getItemCount(): Int {
        val count = jobs.size
        Log.d(TAG, "getItemCount() returning: $count")
        return count
    }

    fun updateJobs(newJobs: List<JobPost>) {
        Log.d(TAG, "updateJobs called - Before: ${jobs.size} jobs, After: ${newJobs.size} jobs")

        jobs.clear()
        jobs.addAll(newJobs)

        Log.d(TAG, "Jobs list after update:")
        jobs.forEachIndexed { index, job ->
            Log.d(TAG, "  [$index] ${job.title} (ID: ${job.id})")
        }

        notifyDataSetChanged()

        Log.d(TAG, "notifyDataSetChanged() called, itemCount = ${itemCount}")
    }
}