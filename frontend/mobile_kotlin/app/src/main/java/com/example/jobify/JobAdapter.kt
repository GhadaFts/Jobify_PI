package com.example.jobify

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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job_post, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]

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

    override fun getItemCount() = jobs.size

    fun updateJobs(newJobs: List<JobPost>) {
        jobs.clear()
        jobs.addAll(newJobs)
        notifyDataSetChanged()
    }
}