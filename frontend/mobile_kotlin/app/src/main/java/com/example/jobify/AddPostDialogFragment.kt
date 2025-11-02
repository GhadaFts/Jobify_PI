package com.example.jobify

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class AddPostDialogFragment : DialogFragment() {

    interface OnPostAddedListener {
        fun onPostAdded(post: JobPost)
    }

    private var listener: OnPostAddedListener? = null

    private lateinit var etTitle: EditText
    private lateinit var etJobPosition: EditText
    private lateinit var etExperience: EditText
    private lateinit var etSalary: EditText
    private lateinit var etDescription: EditText
    private lateinit var etType: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnCancel: Button

    private lateinit var btnAddRequirement: Button
    private lateinit var btnAddSkill: Button
    private lateinit var requirementsContainer: LinearLayout
    private lateinit var skillsContainer: LinearLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPostAddedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnPostAddedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_add_post, container, false)

        // 🔹 Initialisation des vues
        etTitle = view.findViewById(R.id.etTitle)
        etJobPosition = view.findViewById(R.id.etJobPosition)
        etExperience = view.findViewById(R.id.etExperience)
        etSalary = view.findViewById(R.id.etSalary)
        etDescription = view.findViewById(R.id.etDescription)
        etType = view.findViewById(R.id.etType)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnCancel = view.findViewById(R.id.btnCancel)

        btnAddRequirement = view.findViewById(R.id.btnAddRequirement)
        btnAddSkill = view.findViewById(R.id.btnAddSkill)
        requirementsContainer = view.findViewById(R.id.requirementsContainer)
        skillsContainer = view.findViewById(R.id.skillsContainer)

        // 🔹 Champs par défaut
        addRequirementField()
        addSkillField()

        // 🔹 Actions
        btnAddRequirement.setOnClickListener { addRequirementField() }
        btnAddSkill.setOnClickListener { addSkillField() }

        // 🔹 Bouton Cancel
        btnCancel.setOnClickListener {
            dismiss() // ferme la boîte de dialogue
        }

        // 🔹 Bouton Submit
        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString()
            val jobPosition = etJobPosition.text.toString()
            val experience = etExperience.text.toString()
            val salary = etSalary.text.toString()
            val description = etDescription.text.toString()
            val type = etType.text.toString()

            val requirements = mutableListOf<String>()
            for (i in 0 until requirementsContainer.childCount) {
                val editText = requirementsContainer.getChildAt(i) as EditText
                val text = editText.text.toString().trim()
                if (text.isNotEmpty()) requirements.add(text)
            }

            val skills = mutableListOf<String>()
            for (i in 0 until skillsContainer.childCount) {
                val editText = skillsContainer.getChildAt(i) as EditText
                val text = editText.text.toString().trim()
                if (text.isNotEmpty()) skills.add(text)
            }

            if (title.isEmpty() || jobPosition.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            } else {
                val newPost = JobPost(
                    id = Random().nextInt(10000),
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

                listener?.onPostAdded(newPost)
                savePostToPreferences(newPost)
                dismiss()
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(), // 💡 90% de la largeur de l’écran
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun addRequirementField() {
        val editText = EditText(requireContext())
        editText.hint = "Requirement"
        requirementsContainer.addView(editText)
    }

    private fun addSkillField() {
        val editText = EditText(requireContext())
        editText.hint = "Skill"
        skillsContainer.addView(editText)
    }

    private fun savePostToPreferences(post: JobPost) {
        val sharedPref = requireContext().getSharedPreferences("job_posts", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val postJson = JSONObject().apply {
            put("id", post.id)
            put("title", post.title)
            put("jobPosition", post.jobPosition)
            put("experience", post.experience)
            put("salary", post.salary)
            put("description", post.description)
            put("type", post.type)
            put("createdAt", post.createdAt.time)
            put("status", post.status)
            put("requirements", JSONArray(post.requirements))
            put("skills", JSONArray(post.skills))
            put("published", post.published)
        }

        editor.putString(post.id.toString(), postJson.toString())
        editor.apply()
    }
}
