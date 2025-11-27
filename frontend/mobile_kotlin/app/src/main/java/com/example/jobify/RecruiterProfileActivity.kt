package com.example.jobify

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import java.util.*

class RecruiterProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnMenu: ImageView
    private lateinit var btnEdit: ImageView
    private lateinit var btnEditPhoto: ImageView
    private lateinit var menuProfile: LinearLayout
    private lateinit var menuLogout: LinearLayout
    private lateinit var btnSave: Button

    // Views
    private lateinit var companyImage: ImageView
    private lateinit var txtRecruiterName: TextView
    private lateinit var txtCountry: TextView
    private lateinit var txtPostsCount: TextView
    private lateinit var txtEmployeesCount: TextView
    private lateinit var txtBio: TextView
    private lateinit var txtPhone: TextView
    private lateinit var txtDomaine: TextView
    private lateinit var txtAddress: TextView
    private lateinit var phoneLayout: LinearLayout
    private lateinit var domaineLayout: LinearLayout
    private lateinit var addressLayout: LinearLayout
    private lateinit var servicesContainer: FlexboxLayout
    private lateinit var txtNoService: TextView
    private lateinit var btnAddService: ImageView

    // Social links
    private lateinit var socialLinksView: LinearLayout
    private lateinit var btnTwitter: ImageView
    private lateinit var btnWeb: ImageView
    private lateinit var btnGithub: ImageView
    private lateinit var btnFacebook: ImageView

    // Edit mode flag
    private var isEditMode = false

    // Data
    private var fullName: String = ""
    private var photo_profil: String = ""
    private var twitter_link: String = ""
    private var web_link: String = ""
    private var github_link: String = ""
    private var facebook_link: String = ""
    private var description: String = ""
    private var phone_number: String = ""
    private var nationality: String = ""
    private var companyAddress: String = ""
    private var domaine: String = ""
    private var employees_number: Int = 0
    private var services: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recruiter_profile)

        initializeViews()
        loadRecruiterData()
        setupDrawerMenu()
        setupEditMode()
    }

    private fun initializeViews() {
        // Drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        btnMenu = findViewById(R.id.btnMenu)
        btnEdit = findViewById(R.id.btnEdit)
        btnEditPhoto = findViewById(R.id.btnEditPhoto)
        menuProfile = findViewById(R.id.menuProfileLayout)
        menuLogout = findViewById(R.id.menuLogoutLayout)
        btnSave = findViewById(R.id.btnSave)

        // Profile views
        companyImage = findViewById(R.id.companyImage)
        txtRecruiterName = findViewById(R.id.txtRecruiterName)
        txtCountry = findViewById(R.id.txtCountry)
        txtPostsCount = findViewById(R.id.txtPostsCount)
        txtEmployeesCount = findViewById(R.id.txtEmployeesCount)
        txtBio = findViewById(R.id.txtBio)
        txtPhone = findViewById(R.id.txtPhone)
        txtDomaine = findViewById(R.id.txtDomaine)
        txtAddress = findViewById(R.id.txtAddress)
        phoneLayout = findViewById(R.id.phoneLayout)
        domaineLayout = findViewById(R.id.domaineLayout)
        addressLayout = findViewById(R.id.addressLayout)
        servicesContainer = findViewById(R.id.servicesContainer)
        txtNoService = findViewById(R.id.txtNoService)
        btnAddService = findViewById(R.id.btnAddService)

        // Social links
        socialLinksView = findViewById(R.id.socialLinksView)
        btnTwitter = findViewById(R.id.btnTwitter)
        btnWeb = findViewById(R.id.btnWeb)
        btnGithub = findViewById(R.id.btnGithub)
        btnFacebook = findViewById(R.id.btnFacebook)
    }

    private fun setupDrawerMenu() {
        btnMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuLogout.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                performLogout()
            }, 250)
        }

        findViewById<LinearLayout>(R.id.menuHomeLayout).setOnClickListener {
            startActivity(Intent(this, JobOpportunitiesActivity::class.java))
        }
    }

    private fun setupEditMode() {
        btnEdit.setOnClickListener {
            toggleEditMode()
        }

        btnSave.setOnClickListener {
            saveChanges()
        }

        btnEditPhoto.setOnClickListener {
            // TODO: Implement photo picker
            Toast.makeText(this, "Photo selection coming soon", Toast.LENGTH_SHORT).show()
        }

        btnAddService.setOnClickListener {
            showAddServiceDialog()
        }

        // Social links clicks
        btnTwitter.setOnClickListener { openLink(twitter_link) }
        btnWeb.setOnClickListener { openLink(web_link) }
        btnGithub.setOnClickListener { openLink(github_link) }
        btnFacebook.setOnClickListener { openLink(facebook_link) }
    }

    private fun loadRecruiterData() {
        // TODO: Load from API or database
        // For now, using mock data
        fullName = "Tech Solutions Inc."
        nationality = "Tunisia"
        employees_number = 254
        description = "Leading technology company specializing in software development and IT consulting services."
        phone_number = "+216 12 345 678"
        domaine = "Information Technology"
        companyAddress = "Tunis, Tunisia"
        twitter_link = "https://twitter.com/techsolutions"
        web_link = "https://techsolutions.com"
        github_link = "https://github.com/techsolutions"
        facebook_link = "https://facebook.com/techsolutions"
        services = mutableListOf("Web Development", "Mobile Apps", "Cloud Services", "Consulting")

        displayRecruiterData()
    }

    private fun displayRecruiterData() {
        txtRecruiterName.text = fullName
        txtCountry.text = nationality
        txtEmployeesCount.text = employees_number.toString()
        txtBio.text = description

        // Phone
        if (phone_number.isNotEmpty()) {
            txtPhone.text = phone_number
            phoneLayout.visibility = View.VISIBLE
        } else {
            phoneLayout.visibility = View.GONE
        }

        // Domaine
        if (domaine.isNotEmpty()) {
            txtDomaine.text = domaine
            domaineLayout.visibility = View.VISIBLE
        } else {
            domaineLayout.visibility = View.GONE
        }

        // Address
        if (companyAddress.isNotEmpty()) {
            txtAddress.text = companyAddress
            addressLayout.visibility = View.VISIBLE
        } else {
            addressLayout.visibility = View.GONE
        }

        // Social links - Always show the container if at least one link exists
        val hasLinks = twitter_link.isNotEmpty() || web_link.isNotEmpty() ||
                github_link.isNotEmpty() || facebook_link.isNotEmpty()

        socialLinksView.visibility = if (hasLinks) View.VISIBLE else View.GONE

        btnTwitter.visibility = if (twitter_link.isNotEmpty()) View.VISIBLE else View.GONE
        btnWeb.visibility = if (web_link.isNotEmpty()) View.VISIBLE else View.GONE
        btnGithub.visibility = if (github_link.isNotEmpty()) View.VISIBLE else View.GONE
        btnFacebook.visibility = if (facebook_link.isNotEmpty()) View.VISIBLE else View.GONE

        // Services
        displayServices()
    }

    private fun displayServices() {
        servicesContainer.removeAllViews()

        if (services.isEmpty()) {
            txtNoService.visibility = View.VISIBLE
            servicesContainer.visibility = View.GONE
        } else {
            txtNoService.visibility = View.GONE
            servicesContainer.visibility = View.VISIBLE

            services.forEach { service ->
                val chip = Chip(this).apply {
                    text = service
                    isClickable = false
                    isCheckable = false
                    chipBackgroundColor = android.content.res.ColorStateList.valueOf(Color.parseColor("#1F4E5F"))
                    setTextColor(Color.WHITE)

                    if (isEditMode) {
                        isCloseIconVisible = true
                        setOnCloseIconClickListener {
                            removeService(service)
                        }
                    } else {
                        isCloseIconVisible = false
                    }
                }
                servicesContainer.addView(chip)
            }
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode

        if (isEditMode) {
            // Enter edit mode
            btnEdit.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            btnEditPhoto.visibility = View.VISIBLE
            btnAddService.visibility = View.VISIBLE
            btnSave.visibility = View.VISIBLE

            // Make fields editable
            showEditDialog()
        } else {
            // Exit edit mode without saving
            btnEdit.setImageResource(R.drawable.ic_edit)
            btnEditPhoto.visibility = View.GONE
            btnAddService.visibility = View.GONE
            btnSave.visibility = View.GONE
            displayServices()
        }
    }

    private fun showEditDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)

        val edtName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtFullName)
        val edtPhone = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtPhone)
        val edtNationality = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtNationality)
        val edtAddress = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtAddress)
        val edtDomaine = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtDomaine)
        val edtEmployees = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtEmployees)
        val edtDescription = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtDescription)
        val edtTwitter = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtTwitter)
        val edtWeb = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtWeb)
        val edtGithub = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtGithub)
        val edtFacebook = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtFacebook)

        // Pre-fill current data
        edtName.setText(fullName)
        edtPhone.setText(phone_number)
        edtNationality.setText(nationality)
        edtAddress.setText(companyAddress)
        edtDomaine.setText(domaine)
        edtEmployees.setText(employees_number.toString())
        edtDescription.setText(description)
        edtTwitter.setText(twitter_link)
        edtWeb.setText(web_link)
        edtGithub.setText(github_link)
        edtFacebook.setText(facebook_link)

        AlertDialog.Builder(this)
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                fullName = edtName.text.toString()
                phone_number = edtPhone.text.toString()
                nationality = edtNationality.text.toString()
                companyAddress = edtAddress.text.toString()
                domaine = edtDomaine.text.toString()
                employees_number = edtEmployees.text.toString().toIntOrNull() ?: 0
                description = edtDescription.text.toString()
                twitter_link = edtTwitter.text.toString()
                web_link = edtWeb.text.toString()
                github_link = edtGithub.text.toString()
                facebook_link = edtFacebook.text.toString()

                displayRecruiterData()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                isEditMode = false
                btnEdit.setImageResource(R.drawable.ic_edit)
                btnEditPhoto.visibility = View.GONE
                btnAddService.visibility = View.GONE
                btnSave.visibility = View.GONE
            }
            .show()
    }

    private fun showAddServiceDialog() {
        val input = EditText(this).apply {
            hint = "Service name"
        }

        AlertDialog.Builder(this)
            .setTitle("Add Service")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val serviceName = input.text.toString().trim()
                if (serviceName.isNotEmpty() && !services.contains(serviceName)) {
                    services.add(serviceName)
                    displayServices()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun removeService(service: String) {
        services.remove(service)
        displayServices()
    }

    private fun saveChanges() {
        // TODO: Save to API or database
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()

        isEditMode = false
        btnEdit.setImageResource(R.drawable.ic_edit)
        btnEditPhoto.visibility = View.GONE
        btnAddService.visibility = View.GONE
        btnSave.visibility = View.GONE
        displayServices()
    }

    private fun openLink(url: String) {
        if (url.isNotEmpty()) {
            // TODO: Open URL in browser
            Toast.makeText(this, "Opening: $url", Toast.LENGTH_SHORT).show()
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