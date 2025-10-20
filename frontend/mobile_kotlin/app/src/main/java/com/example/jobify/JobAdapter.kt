package com.example.jobify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout

class JobAdapter(private val jobs: List<JobPost>) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.job_title)
        val company = itemView.findViewById<TextView>(R.id.company)
        val location = itemView.findViewById<TextView>(R.id.location)
        val type = itemView.findViewById<TextView>(R.id.type)
        val description = itemView.findViewById<TextView>(R.id.description)
        val experience = itemView.findViewById<TextView>(R.id.experience)
        val salary = itemView.findViewById<TextView>(R.id.salary)
        val applicants = itemView.findViewById<TextView>(R.id.applicants)
        val btnApply = itemView.findViewById<Button>(R.id.btn_apply)
        val skillsContainer = itemView.findViewById<FlexboxLayout>(R.id.skills_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job_post, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]

        holder.title.text = job.title
        holder.company.text = job.company
        holder.location.text = job.location
        holder.type.text = job.type
        holder.description.text = job.description
        holder.experience.text = job.experience
        holder.salary.text = job.salary
        holder.applicants.text = job.applicants

        // Clear previous skills
        holder.skillsContainer.removeAllViews()

        // Add skills dynamically
        for (skill in job.skills) {
            val skillView = TextView(holder.itemView.context).apply {
                text = skill
                setPadding(24, 12, 24, 12)
                background = context.getDrawable(R.drawable.badge_background)
                setTextColor(context.resources.getColor(android.R.color.black, null))
                textSize = 12f

                val params = FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(12, 8, 12, 8)
                layoutParams = params
            }

            holder.skillsContainer.addView(skillView)
        }

        holder.btnApply.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Applied to ${job.title}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = jobs.size
}
