package com.example.jobify

data class JobPost(
    val title: String,
    val company: String,
    val location: String,
    val type: String,
    val description: String,
    val experience: String,
    val salary: String,
    val applicants: String,
    val skills: List<String>
)
