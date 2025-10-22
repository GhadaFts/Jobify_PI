package com.example.jobify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.*

class AddPostDialogFragment : DialogFragment() {

    private lateinit var etTitle: EditText
    private lateinit var etJobPosition: EditText
    private lateinit var etExperience: EditText
    private lateinit var etSalary: EditText
    private lateinit var etDescription: EditText
    private lateinit var etType: EditText
    private lateinit var etRequirements: EditText
    private lateinit var etSkills: EditText
    private lateinit var btnSubmit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_add_post, container, false)

        etTitle = view.findViewById(R.id.etTitle)
        etJobPosition = view.findViewById(R.id.etJobPosition)
        etExperience = view.findViewById(R.id.etExperience)
        etSalary = view.findViewById(R.id.etSalary)
        etDescription = view.findViewById(R.id.etDescription)
        etType = view.findViewById(R.id.etType)
        etRequirements = view.findViewById(R.id.etRequirements)
        etSkills = view.findViewById(R.id.etSkills)
        btnSubmit = view.findViewById(R.id.btnSubmit)

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
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            } else {
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

                // Tu peux envoyer ce post au RecyclerView via un listener ou ViewModel
                Toast.makeText(requireContext(), "Post Added: ${newPost.title}", Toast.LENGTH_SHORT).show()

                dismiss() // Ferme le modal
            }
        }

        return view
    }
}
