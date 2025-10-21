package com.example.jobify

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AddPostActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etJobPosition: EditText
    private lateinit var etExperience: EditText
    private lateinit var etSalary: EditText
    private lateinit var etDescription: EditText
    private lateinit var etType: EditText
    private lateinit var etRequirements: EditText
    private lateinit var etSkills: EditText
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        etTitle = findViewById(R.id.etTitle)
        etJobPosition = findViewById(R.id.etJobPosition)
        etExperience = findViewById(R.id.etExperience)
        etSalary = findViewById(R.id.etSalary)
        etDescription = findViewById(R.id.etDescription)
        etType = findViewById(R.id.etType)
        etRequirements = findViewById(R.id.etRequirements)
        etSkills = findViewById(R.id.etSkills)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString()
            val jobPosition = etJobPosition.text.toString()
            val experience = etExperience.text.toString()
            val salary = etSalary.text.toString()
            val description = etDescription.text.toString()
            val type = etType.text.toString()
            val requirements = etRequirements.text.toString().split(",").map { it.trim() }
            val skills = etSkills.text.toString().split(",").map { it.trim() }

            if(title.isEmpty() || jobPosition.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            } else {
                // Ici tu peux ajouter le post à ta liste ou appeler ton API backend
                val newPost = Post(
                    id = Random().nextInt(1000),
                    title = title,
                    jobPosition = jobPosition,
                    experience = experience,
                    salary = salary,
                    description = description,
                    type = type,
                    createdAt = Date(),
                    status = "Active",
                    requirements = requirements,
                    skills = skills,
                    published = true
                )

                Toast.makeText(this, "Post Added: ${newPost.title}", Toast.LENGTH_SHORT).show()

                // Optionnel: revenir à PostsActivity
                finish()
            }
        }
    }
}
