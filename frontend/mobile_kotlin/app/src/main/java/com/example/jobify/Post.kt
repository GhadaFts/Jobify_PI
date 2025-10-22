package com.example.jobify

import java.util.Date

data class Post(
    val id: Int,
    val title: String,
    val jobPosition: String,
    val experience: String,
    val salary: String,
    val description: String,
    val type: String,
    val createdAt: Date,
    val status: String,
    val requirements: List<String>,
    val skills: List<String>,
    val published: Boolean
)
