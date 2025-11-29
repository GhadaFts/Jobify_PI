package com.example.jobify

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.flexbox.FlexboxLayout
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : BaseDrawerActivity() {

    // ... dans la classe ProfileActivity, avec vos autres variables de classe
    private lateinit var scrollViewRoot: ScrollView

    private val interviewNotifications = listOf(
        InterviewNotification(
            id = "1",
            companyName = "TekUp",
            interviewDate = "Nov 21, 2025",
            interviewTime = "1:00 AM",
            location = "Online Meeting",
            additionalNotes = "Please have your portfolio ready. Technical assessment will include live coding.",
            duration = "60 mins",
            interviewType = "Online",
            isCompleted = true,
            meetingLink = "https://meet.google.com/tekup-interview-link" // Lien de réunion simulé
        ),
        InterviewNotification(
            id = "2",
            companyName = "Tech Solutions SARL",
            interviewDate = "Jan 12, 2026",
            interviewTime = "2:30 PM",
            location = "Tech Park, Ariana, Tunisi...",
            additionalNotes = "Bring your ID and previous work samples. Dress code: Business casual.",
            duration = "45 mins",
            interviewType = "Local",
            isCompleted = false
        ),
        InterviewNotification(
            id = "3",
            companyName = "MedCare Hospital",
            interviewDate = "Jan 20, 2024",
            interviewTime = "9:00 AM",
            location = "Online Meeting",
            additionalNotes = "Please bring your CV and diploma for verification.",
            duration = "90 mins",
            interviewType = "Online",
            isCompleted = true,
            meetingLink = "https://meet.google.com/medcare-interview-link" // Lien de réunion simulé
        )
    )

    // ...
    // Données du profil
    private var id: Int = 1
    private var email = "example@email.com"
    private var password = "********"
    private var fullName = "Meriem Bejaoui"
    private var role = "Android Developer"
    private var photoProfil = ""
    private var twitterLink = ""
    private var webLink = ""
    private var githubLink = ""
    private var facebookLink = ""
    private var description = "A motivated android developer seeking new challenges"
    private var phoneNumber = "+216 12 345 678"
    private var nationality = "Tunisia"
    private var title = "Senior Developer"
    private var dateOfBirth = "01/01/1995"
    private var gender = "Female"

    // Listes dynamiques
    private val skills = mutableListOf<String>()
    private val experiences = mutableListOf<Experience>()
    private val educations = mutableListOf<Education>()

    private var isDarkMode = false
    private lateinit var rootLayout: ScrollView

    // UI Elements
    private lateinit var txtName: TextView
    private lateinit var txtCountry: TextView
    private lateinit var txtBio: TextView
    private lateinit var btnNotification: ImageView
    private lateinit var experienceContainer: LinearLayout
    private lateinit var txtNoExperience: TextView
    private lateinit var educationContainer: LinearLayout
    private lateinit var txtNoEducation: TextView
    private lateinit var skillsContainer: FlexboxLayout
    private lateinit var txtNoSkill: TextView
    private lateinit var languagesContainer: FlexboxLayout
    private lateinit var txtNoLanguage: TextView

    data class Experience(
        var jobPosition: String,
        var companyName: String,
        var address: String,
        var startDate: String,
        var endDate: String
    )

    data class Education(
        var university: String,
        var degree: String,
        var field: String,
        var startDate: String,
        var endDate: String
    )

    // Ajoutez cette data class dans ProfileActivity.kt, avant la fonction onCreate
    data class InterviewNotification(
        val id: String,
        val companyName: String,
        val interviewDate: String, // ISO string ou date lisible, nous utiliserons la date lisible pour la démo
        val interviewTime: String,
        val location: String,
        val additionalNotes: String,
        val duration: String,
        val interviewType: String, // "Online" ou "Local"
        val isCompleted: Boolean,
        val meetingLink: String = "" // Pour le bouton "Join"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidate_profile)

        initViews()
        setupListeners()
        loadProfileFromPreferences()
        loadProfileData()
    }

    private fun initViews() {
        scrollViewRoot = findViewById(R.id.scrollViewRoot)
        rootLayout = findViewById(R.id.scrollViewRoot)

        txtName = findViewById(R.id.txtName)
        txtCountry = findViewById(R.id.txtCountry)
        txtBio = findViewById(R.id.txtBio)

        // Experience
        experienceContainer = findViewById(R.id.experienceItemsContainer)
        txtNoExperience = findViewById(R.id.txtNoExperience)

        // Education
        educationContainer = findViewById(R.id.educationItemsContainer)
        txtNoEducation = findViewById(R.id.txtNoEducation)

        // Skills
        skillsContainer = findViewById(R.id.skillsContainer)
        txtNoSkill = findViewById(R.id.txtNoSkill)

        // Languages
        languagesContainer = findViewById(R.id.languagesContainer)
        txtNoLanguage = findViewById(R.id.txtNoLanguage)
        btnNotification = findViewById(R.id.btnNotification)

    }

    private fun loadProfileFromPreferences() {
        val prefs = getSharedPreferences("UserProfile", MODE_PRIVATE)

        // Charger les données depuis SharedPreferences
        fullName = prefs.getString("fullName", fullName) ?: fullName
        title = prefs.getString("title", title) ?: title
        role = prefs.getString("role", role) ?: role
        dateOfBirth = prefs.getString("birthDate", dateOfBirth) ?: dateOfBirth
        gender = prefs.getString("gender", gender) ?: gender
        email = prefs.getString("email", email) ?: email
        phoneNumber = prefs.getString("phone", phoneNumber) ?: phoneNumber
        nationality = prefs.getString("nationality", nationality) ?: nationality
        twitterLink = prefs.getString("twitter", twitterLink) ?: twitterLink
        githubLink = prefs.getString("github", githubLink) ?: githubLink
        facebookLink = prefs.getString("facebook", facebookLink) ?: facebookLink
        webLink = prefs.getString("website", webLink) ?: webLink

        // Afficher dans les TextViews
        findViewById<TextView>(R.id.txtFullName).text = "Full Name: $fullName"
        findViewById<TextView>(R.id.txtTitle).text = "Title: $title"
        findViewById<TextView>(R.id.txtRole).text = "Role: $role"
        findViewById<TextView>(R.id.txtBirthDate).text = "Birth Date: $dateOfBirth"
        findViewById<TextView>(R.id.txtGender).text = "Gender: $gender"
        findViewById<TextView>(R.id.txtEmail).text = "Email: $email"
        findViewById<TextView>(R.id.txtPhone).text = "Phone: $phoneNumber"
        findViewById<TextView>(R.id.txtNationality).text = "Nationality: $nationality"
        findViewById<TextView>(R.id.txtTwitter).text = "Twitter: $twitterLink"
        findViewById<TextView>(R.id.txtGithub).text = "GitHub: $githubLink"
        findViewById<TextView>(R.id.txtFacebook).text = "Facebook: $facebookLink"
        findViewById<TextView>(R.id.txtWebsite).text = "Website: $webLink"
    }

    private fun saveToPreferences() {
        val prefs = getSharedPreferences("UserProfile", MODE_PRIVATE).edit()

        prefs.putString("fullName", fullName)
        prefs.putString("title", title)
        prefs.putString("role", role)
        prefs.putString("birthDate", dateOfBirth)
        prefs.putString("gender", gender)
        prefs.putString("email", email)
        prefs.putString("phone", phoneNumber)
        prefs.putString("nationality", nationality)
        prefs.putString("twitter", twitterLink)
        prefs.putString("github", githubLink)
        prefs.putString("facebook", facebookLink)
        prefs.putString("website", webLink)

        prefs.apply()
    }

    private fun setupListeners() {
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val btnTheme = findViewById<ImageView>(R.id.btnTheme)
        val btnEditName = findViewById<ImageView>(R.id.btnEditName)
        val btnAddExperience = findViewById<ImageView>(R.id.btnAddExperience)
        val btnAddEducation = findViewById<ImageView>(R.id.btnAddEducation)
        val btnEditSkills = findViewById<ImageView>(R.id.btnEditSkills)

        btnMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        btnEditName.setOnClickListener { showEditBasicInfoDialog() }
        btnAddExperience.setOnClickListener { showAddExperienceDialog() }
        btnAddEducation.setOnClickListener { showAddEducationDialog() }
        btnEditSkills.setOnClickListener { showAddSkillDialog() }
        btnNotification.setOnClickListener { showInterviewNotificationsDialog() }
        // Click on elements to edit
        txtName.setOnClickListener { showEditBasicInfoDialog() }
        txtCountry.setOnClickListener { showEditContactDialog() }
        txtBio.setOnClickListener { showEditBioDialog() }

        setupDrawerMenu()
        setupDarkMode(btnTheme)
    }

    /**
     * Affiche un modal (AlertDialog) avec la liste des notifications d'entrevue.
     */
    private fun showInterviewNotificationsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_interview_notifications, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val container = dialogView.findViewById<LinearLayout>(R.id.notificationsContainer)

        interviewNotifications.forEach { notification ->
            val notificationCard = layoutInflater.inflate(R.layout.item_interview_notification, container, false)

            // Définition des champs
            notificationCard.findViewById<TextView>(R.id.tvCompanyName).text = notification.companyName
            notificationCard.findViewById<TextView>(R.id.tvDuration).text = notification.duration
            notificationCard.findViewById<TextView>(R.id.tvType).text = notification.interviewType
            notificationCard.findViewById<TextView>(R.id.tvDate).text = notification.interviewDate
            notificationCard.findViewById<TextView>(R.id.tvTime).text = notification.interviewTime
            notificationCard.findViewById<TextView>(R.id.tvLocation).text = notification.location
            notificationCard.findViewById<TextView>(R.id.tvNotes).text = notification.additionalNotes

            val btnJoin = notificationCard.findViewById<Button>(R.id.btnJoin)
            val tvCompleted = notificationCard.findViewById<TextView>(R.id.tvCompleted)
            val btnDetails = notificationCard.findViewById<Button>(R.id.btnDetails)
            val typeIcon = notificationCard.findViewById<ImageView>(R.id.iconType)

            // Logique 'Completed' vs. 'Join'
            if (notification.isCompleted) {
                btnJoin.visibility = View.GONE
                tvCompleted.visibility = View.VISIBLE
                tvCompleted.text = "Completed"
            } else {
                btnJoin.visibility = View.VISIBLE
                tvCompleted.visibility = View.GONE
            }

            // Logique de l'icône de type (Online/Local)
            if (notification.interviewType == "Online") {
                typeIcon.setImageResource(R.drawable.ic_videocam) // Assurez-vous d'avoir cet icône
            } else {
                typeIcon.setImageResource(R.drawable.ic_location) // Assurez-vous d'avoir cet icône
            }

            // Gérer le bouton 'Join' pour les réunions en ligne
            if (notification.interviewType == "Online" && !notification.isCompleted) {
                btnJoin.setOnClickListener {
                    try {
                        // Ouvre le lien de la réunion (Google Meet, Zoom, etc.)
                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(notification.meetingLink))
                        startActivity(intent)
                        dialog.dismiss() // Fermer le modal après avoir cliqué sur Join
                    } catch (e: Exception) {
                        Toast.makeText(this, "Impossible d'ouvrir le lien: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                // Désactiver le bouton Join si non-en ligne ou complété
                btnJoin.isEnabled = false
                btnJoin.alpha = 0.5f // Optionnel : rendre le bouton semi-transparent
            }

            // Gérer le bouton 'Details' (vous pouvez afficher plus d'infos ou fermer le modal)
            btnDetails.setOnClickListener {
                Toast.makeText(this, "Details for ${notification.companyName}", Toast.LENGTH_SHORT).show()
                // Vous pouvez ajouter ici une autre boite de dialogue pour les détails
            }

            container.addView(notificationCard)
        }

        dialog.show()
    }
    private fun loadProfileData() {
        txtName.text = fullName
        txtCountry.text = nationality
        txtBio.text = description

        refreshExperienceList()
        refreshEducationList()
        refreshSkillsList()
    }

    // ============== EDIT BASIC INFO ==============
    private fun showEditBasicInfoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_basic_info, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val inputFullName = dialogView.findViewById<EditText>(R.id.inputFullName)
        val inputTitle = dialogView.findViewById<EditText>(R.id.inputTitle)
        val inputRole = dialogView.findViewById<EditText>(R.id.inputRole)
        val inputDateOfBirth = dialogView.findViewById<EditText>(R.id.inputDateOfBirth)
        val spinnerGender = dialogView.findViewById<Spinner>(R.id.spinnerGender)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveBasicInfo)

        // Populate fields
        inputFullName.setText(fullName)
        inputTitle.setText(title)
        inputRole.setText(role)
        inputDateOfBirth.setText(dateOfBirth)

        // Gender spinner
        val genderOptions = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter
        spinnerGender.setSelection(genderOptions.indexOf(gender))

        inputDateOfBirth.setOnClickListener { showDatePicker(inputDateOfBirth) }

        btnSave.setOnClickListener {
            fullName = inputFullName.text.toString().trim()
            title = inputTitle.text.toString().trim()
            role = inputRole.text.toString().trim()
            dateOfBirth = inputDateOfBirth.text.toString().trim()
            gender = spinnerGender.selectedItem.toString()

            txtName.text = fullName
            saveToPreferences()
            loadProfileFromPreferences()
            Toast.makeText(this, "Basic info updated", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    // ============== EDIT CONTACT INFO ==============
    private fun showEditContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_contact, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val inputEmail = dialogView.findViewById<EditText>(R.id.inputEmail)
        val inputPhone = dialogView.findViewById<EditText>(R.id.inputPhone)
        val inputNationality = dialogView.findViewById<EditText>(R.id.inputNationality)
        val inputTwitter = dialogView.findViewById<EditText>(R.id.inputTwitter)
        val inputGithub = dialogView.findViewById<EditText>(R.id.inputGithub)
        val inputFacebook = dialogView.findViewById<EditText>(R.id.inputFacebook)
        val inputWebsite = dialogView.findViewById<EditText>(R.id.inputWebsite)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveContact)

        // Populate fields
        inputEmail.setText(email)
        inputPhone.setText(phoneNumber)
        inputNationality.setText(nationality)
        inputTwitter.setText(twitterLink)
        inputGithub.setText(githubLink)
        inputFacebook.setText(facebookLink)
        inputWebsite.setText(webLink)

        btnSave.setOnClickListener {
            email = inputEmail.text.toString().trim()
            phoneNumber = inputPhone.text.toString().trim()
            nationality = inputNationality.text.toString().trim()
            twitterLink = inputTwitter.text.toString().trim()
            githubLink = inputGithub.text.toString().trim()
            facebookLink = inputFacebook.text.toString().trim()
            webLink = inputWebsite.text.toString().trim()

            txtCountry.text = nationality
            saveToPreferences()
            loadProfileFromPreferences()
            Toast.makeText(this, "Contact info updated", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    // ============== EDIT BIO ==============
    private fun showEditBioDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_bio, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val inputBio = dialogView.findViewById<EditText>(R.id.inputBio)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveBio)

        inputBio.setText(description)

        btnSave.setOnClickListener {
            description = inputBio.text.toString().trim()
            txtBio.text = description
            Toast.makeText(this, "Bio updated", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    // ============== EXPERIENCE ==============
    private fun showAddExperienceDialog(existingExp: Experience? = null, index: Int = -1) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_experience, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val jobPosition = dialogView.findViewById<EditText>(R.id.inputJobPosition)
        val companyName = dialogView.findViewById<EditText>(R.id.inputCompanyName)
        val address = dialogView.findViewById<EditText>(R.id.inputAddress)
        val startDate = dialogView.findViewById<EditText>(R.id.inputStartDate)
        val endDate = dialogView.findViewById<EditText>(R.id.inputEndDate)
        val addBtn = dialogView.findViewById<Button>(R.id.btnAddExperience)

        // If editing
        existingExp?.let {
            jobPosition.setText(it.jobPosition)
            companyName.setText(it.companyName)
            address.setText(it.address)
            startDate.setText(it.startDate)
            endDate.setText(it.endDate)
            addBtn.text = "Update"
        }

        startDate.setOnClickListener { showDatePicker(startDate) }
        endDate.setOnClickListener { showDatePicker(endDate) }

        addBtn.setOnClickListener {
            val job = jobPosition.text.toString().trim()
            val company = companyName.text.toString().trim()
            val addr = address.text.toString().trim()
            val start = startDate.text.toString().trim()
            val end = endDate.text.toString().trim()

            if (job.isEmpty() || company.isEmpty() || addr.isEmpty() || start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val experience = Experience(job, company, addr, start, end)

            if (index >= 0) {
                experiences[index] = experience
            } else {
                experiences.add(experience)
            }

            refreshExperienceList()
            Toast.makeText(this, "Experience saved", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun refreshExperienceList() {
        experienceContainer.removeAllViews()

        if (experiences.isEmpty()) {
            txtNoExperience.visibility = View.VISIBLE
        } else {
            txtNoExperience.visibility = View.GONE

            experiences.forEachIndexed { index, exp ->
                val itemView = layoutInflater.inflate(R.layout.item_experience, experienceContainer, false)

                itemView.findViewById<TextView>(R.id.tvJobPosition).text = exp.jobPosition
                itemView.findViewById<TextView>(R.id.tvCompanyName).text = exp.companyName
                itemView.findViewById<TextView>(R.id.tvAddress).text = exp.address
                itemView.findViewById<TextView>(R.id.tvWorkDuration).text = "${exp.startDate} - ${exp.endDate}"

                val btnEdit = itemView.findViewById<ImageView>(R.id.btnEditExperience)
                val btnDelete = itemView.findViewById<Button>(R.id.btnDeleteExperience)

                btnEdit.setOnClickListener {
                    showAddExperienceDialog(exp, index)
                }

                btnDelete.setOnClickListener {
                    experiences.removeAt(index)
                    refreshExperienceList()
                    Toast.makeText(this, "Experience removed", Toast.LENGTH_SHORT).show()
                }

                experienceContainer.addView(itemView)
            }
        }
    }

    // ============== EDUCATION ==============
    private fun showAddEducationDialog(existingEdu: Education? = null, index: Int = -1) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_education, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val university = dialogView.findViewById<EditText>(R.id.inputUniversity)
        val degree = dialogView.findViewById<EditText>(R.id.inputDegree)
        val field = dialogView.findViewById<EditText>(R.id.inputField)
        val start = dialogView.findViewById<EditText>(R.id.inputEduStart)
        val end = dialogView.findViewById<EditText>(R.id.inputEduEnd)
        val addBtn = dialogView.findViewById<Button>(R.id.btnAddEducation)

        existingEdu?.let {
            university.setText(it.university)
            degree.setText(it.degree)
            field.setText(it.field)
            start.setText(it.startDate)
            end.setText(it.endDate)
            addBtn.text = "Update"
        }

        start.setOnClickListener { showDatePicker(start) }
        end.setOnClickListener { showDatePicker(end) }

        addBtn.setOnClickListener {
            val uni = university.text.toString().trim()
            val deg = degree.text.toString().trim()
            val fld = field.text.toString().trim()
            val startDate = start.text.toString().trim()
            val endDate = end.text.toString().trim()

            if (uni.isEmpty() || deg.isEmpty() || fld.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val education = Education(uni, deg, fld, startDate, endDate)

            if (index >= 0) {
                educations[index] = education
            } else {
                educations.add(education)
            }

            refreshEducationList()
            Toast.makeText(this, "Education saved", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun refreshEducationList() {
        educationContainer.removeAllViews()

        if (educations.isEmpty()) {
            txtNoEducation.visibility = View.VISIBLE
        } else {
            txtNoEducation.visibility = View.GONE

            educations.forEachIndexed { index, edu ->
                val itemView = layoutInflater.inflate(R.layout.item_education, educationContainer, false)

                itemView.findViewById<TextView>(R.id.tvDegree).text = edu.degree
                itemView.findViewById<TextView>(R.id.tvField).text = edu.field
                itemView.findViewById<TextView>(R.id.tvUniversity).text = edu.university
                itemView.findViewById<TextView>(R.id.tvGraduationDate).text = "${edu.startDate} - ${edu.endDate}"

                val btnEdit = itemView.findViewById<ImageView>(R.id.btnEditEducation)
                val btnDelete = itemView.findViewById<Button>(R.id.btnRemoveEducation)

                btnEdit.setOnClickListener {
                    showAddEducationDialog(edu, index)
                }

                btnDelete.setOnClickListener {
                    educations.removeAt(index)
                    refreshEducationList()
                    Toast.makeText(this, "Education removed", Toast.LENGTH_SHORT).show()
                }

                educationContainer.addView(itemView)
            }
        }
    }

    // ============== SKILLS ==============
    private fun showAddSkillDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_item, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val title = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val input = dialogView.findViewById<EditText>(R.id.inputItem)
        val addBtn = dialogView.findViewById<Button>(R.id.btnAddItem)
        title.text = "Add Skill"

        addBtn.setOnClickListener {
            val skill = input.text.toString().trim()
            if (skill.isEmpty()) {
                input.error = "Enter skill"
                return@setOnClickListener
            }

            skills.add(skill)
            refreshSkillsList()
            Toast.makeText(this, "Skill added", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun refreshSkillsList() {
        skillsContainer.removeAllViews()

        if (skills.isEmpty()) {
            txtNoSkill.visibility = View.VISIBLE
        } else {
            txtNoSkill.visibility = View.GONE

            skills.forEach { skill ->
                val tv = TextView(this)
                tv.text = skill
                tv.setTextColor(Color.WHITE)
                tv.textSize = 14f
                tv.setPadding(32, 16, 32, 16)
                tv.background = ContextCompat.getDrawable(this, R.drawable.bg_item_profile)

                val randomColor = getRandomColor()
                (tv.background as GradientDrawable).setColor(randomColor)

                val params = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(12, 12, 12, 12)
                tv.layoutParams = params

                tv.setOnLongClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Delete Skill")
                        .setMessage("Remove '$skill'?")
                        .setPositiveButton("Delete") { _, _ ->
                            skills.remove(skill)
                            refreshSkillsList()
                            Toast.makeText(this, "Skill removed", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                    true
                }

                skillsContainer.addView(tv)
            }
        }
    }

    private fun getRandomColor(): Int {
        val colors = listOf(
            Color.parseColor("#FF6B6B"),
            Color.parseColor("#4ECDC4"),
            Color.parseColor("#45B7D1"),
            Color.parseColor("#FFA07A"),
            Color.parseColor("#98D8C8"),
            Color.parseColor("#6C5CE7"),
            Color.parseColor("#A29BFE"),
            Color.parseColor("#FD79A8")
        )
        return colors.random()
    }

    // ============== UTILITIES ==============
    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dpd = DatePickerDialog(this, { _, year, month, day ->
            val selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year)
            editText.setText(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        dpd.show()
    }

    private fun setupDrawerMenu() {
        findViewById<LinearLayout>(R.id.menuHomeLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, JobOpportunitiesActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.menuProfileLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        findViewById<LinearLayout>(R.id.menuLogoutLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                performLogout()
            }, 250)
        }
    }

    private fun setupDarkMode(btnTheme: ImageView) {
        val darkModeLayout = findViewById<LinearLayout>(R.id.menuDarkModeLayout)
        val darkModeIcon = findViewById<ImageView>(R.id.menuDarkModeIcon)

        darkModeLayout.setOnClickListener {
            toggleDarkMode(btnTheme, darkModeIcon)
        }
        btnTheme.setOnClickListener {
            toggleDarkMode(btnTheme, darkModeIcon)
        }
    }

    private fun toggleDarkMode(btnTheme: ImageView, darkModeIcon: ImageView) {
        if (isDarkMode) {
            btnTheme.setImageResource(R.drawable.ic_sun)
            darkModeIcon.setImageResource(R.drawable.ic_dark_mode)
            rootLayout.setBackgroundColor(Color.parseColor("#F5F7FA"))
            txtName.setTextColor(Color.parseColor("#1F4E5F"))
            txtCountry.setTextColor(Color.parseColor("#6E7A8A"))
            txtBio.setTextColor(Color.parseColor("#1F4E5F"))
            isDarkMode = false
        } else {
            btnTheme.setImageResource(R.drawable.ic_moon)
            darkModeIcon.setImageResource(R.drawable.ic_sun)
            rootLayout.setBackgroundColor(Color.parseColor("#1F1F1F"))
            txtName.setTextColor(Color.parseColor("#FFFFFF"))
            txtCountry.setTextColor(Color.parseColor("#BBBBBB"))
            txtBio.setTextColor(Color.parseColor("#FFFFFF"))
            isDarkMode = true
        }
    }

    private fun performLogout() {
        try {
            val sessionManager = SessionManager(this)
            sessionManager.clearSession()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finishAffinity()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Logout error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}