package com.example.jobify

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import java.text.SimpleDateFormat
import java.util.*

class JobAdapter(private val jobs: List<JobPost>) :
    RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.job_title)
        val position: TextView = itemView.findViewById(R.id.job_position)
        val type: TextView = itemView.findViewById(R.id.job_type)
        val status: TextView = itemView.findViewById(R.id.job_status)
        val createdAt: TextView = itemView.findViewById(R.id.created_at)
        val description: TextView = itemView.findViewById(R.id.job_description)
        val experience: TextView = itemView.findViewById(R.id.job_experience)
        val salary: TextView = itemView.findViewById(R.id.job_salary)
        val requirementsContainer: LinearLayout = itemView.findViewById(R.id.requirements_container)
        val skillsContainer: FlexboxLayout = itemView.findViewById(R.id.skills_container)
        val btnApply: Button = itemView.findViewById(R.id.btn_apply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job_post, parent, false)
        return JobViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]

        // Format date
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(job.createdAt)

        // Bind data
        holder.title.text = job.title
        holder.position.text = job.jobPosition
        holder.type.text = job.type
        holder.status.text = job.status
        holder.createdAt.text = "Posted on: $formattedDate"
        holder.description.text = job.description
        holder.experience.text = job.experience
        holder.salary.text = job.salary

        // Requirements
        holder.requirementsContainer.removeAllViews()
        for (req in job.requirements) {
            val reqText = TextView(holder.itemView.context).apply {
                text = "• $req"
                textSize = 13f
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                setPadding(4, 2, 4, 2)
            }
            holder.requirementsContainer.addView(reqText)
        }

        // Skills
        holder.skillsContainer.removeAllViews()
        for (skill in job.skills) {
            val skillView = TextView(holder.itemView.context).apply {
                text = skill
                setPadding(24, 12, 24, 12)
                background = ContextCompat.getDrawable(context, R.drawable.badge_background)
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
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

        // Apply button
        holder.btnApply.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Applied to ${job.title}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = jobs.size
}
