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

class PostAdapter(private val posts: MutableList<JobPost>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        val btnDelete: Button = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recruiter_post, parent, false)
        return PostViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        // Format date
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(post.createdAt)

        // Bind data
        holder.title.text = post.title
        holder.position.text = post.jobPosition
        holder.type.text = post.type
        holder.status.text = post.status
        holder.createdAt.text = "Posted on: $formattedDate"
        holder.description.text = post.description
        holder.experience.text = post.experience
        holder.salary.text = post.salary

        // Requirements
        holder.requirementsContainer.removeAllViews()
        for (req in post.requirements) {
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
        for (skill in post.skills) {
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

        // Delete post button
        holder.btnDelete.setOnClickListener {
            val removedTitle = post.title
            posts.removeAt(position)
            notifyItemRemoved(position)

            Toast.makeText(
                holder.itemView.context,
                "Deleted post: $removedTitle",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = posts.size
}
