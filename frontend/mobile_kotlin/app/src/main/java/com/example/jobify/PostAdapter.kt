package com.example.jobify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val posts: List<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvPosition: TextView = view.findViewById(R.id.tvPosition)
        val tvExperience: TextView = view.findViewById(R.id.tvExperience)
        val tvSalary: TextView = view.findViewById(R.id.tvSalary)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.tvTitle.text = post.title
        holder.tvPosition.text = post.jobPosition
        holder.tvExperience.text = post.experience
        holder.tvSalary.text = post.salary
        holder.tvDescription.text = post.description
    }

    override fun getItemCount(): Int = posts.size
}
