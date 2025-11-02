package com.example.jobify

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.flexbox.FlexboxLayout
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private val itemColors = listOf(
        Color.parseColor("#FF6F61"), // rouge corail
        Color.parseColor("#6B5B95"), // violet doux
        Color.parseColor("#88B04B"), // vert clair
        Color.parseColor("#FFA500"), // orange
        Color.parseColor("#009B77"), // turquoise
        Color.parseColor("#5D9CEC"), // bleu doux
        Color.parseColor("#FFB347")  // jaune orangé
    )


    private var isDarkMode = false
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var rootLayout: ScrollView

    private lateinit var experienceContainer: LinearLayout
    private lateinit var txtNoExperience: TextView
    private lateinit var educationContainer: LinearLayout
    private lateinit var txtNoEducation: TextView

    private lateinit var skillsContainer: FlexboxLayout
    private lateinit var txtNoSkill: TextView
    private lateinit var btnEditSkills: ImageView

    private lateinit var languagesContainer: FlexboxLayout
    private lateinit var txtNoLanguage: TextView
    private lateinit var btnEditLanguages: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidate_profile)

        drawerLayout = findViewById(R.id.drawerLayout)
        rootLayout = findViewById(R.id.scrollViewRoot)

        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val btnTheme = findViewById<ImageView>(R.id.btnTheme)
        val txtName = findViewById<TextView>(R.id.txtName)
        val txtCountry = findViewById<TextView>(R.id.txtCountry)
        val txtBio = findViewById<TextView>(R.id.txtBio)
        val statApplied = findViewById<TextView>(R.id.statApplied)
        val statReviewed = findViewById<TextView>(R.id.statReviewed)
        val statInterview = findViewById<TextView>(R.id.statInterview)

        // Experience
        val btnAddExperience = findViewById<ImageView>(R.id.btnAddExperience)
        experienceContainer = findViewById(R.id.experienceItemsContainer)
        txtNoExperience = findViewById(R.id.txtNoExperience)
        btnAddExperience.setOnClickListener { showAddExperienceDialog() }

        // Education
        val btnAddEducation = findViewById<ImageView>(R.id.btnAddEducation)
        educationContainer = findViewById(R.id.educationItemsContainer)
        txtNoEducation = findViewById(R.id.txtNoEducation)
        btnAddEducation.setOnClickListener { showAddEducationDialog() }

        // Skills
        skillsContainer = findViewById(R.id.skillsContainer)
        txtNoSkill = findViewById(R.id.txtNoSkill)
        btnEditSkills = findViewById(R.id.btnEditSkills)
        btnEditSkills.setOnClickListener { showAddSkillDialog() }

        // Languages
        languagesContainer = findViewById(R.id.languagesContainer)
        txtNoLanguage = findViewById(R.id.txtNoLanguage)
        btnEditLanguages = findViewById(R.id.btnEditLanguages)
        btnEditLanguages.setOnClickListener { showAddLanguageDialog() }

        // Drawer Menu
        btnMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        findViewById<LinearLayout>(R.id.menuHomeLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, JobOpportunitiesActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.menuProfileLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        findViewById<LinearLayout>(R.id.menuLogoutLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, MainActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.menuHelpLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        findViewById<LinearLayout>(R.id.menuCorrectCVLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, CvCorrectionActivity::class.java))

        }


        val darkModeLayout = findViewById<LinearLayout>(R.id.menuDarkModeLayout)
        val darkModeIcon = findViewById<ImageView>(R.id.menuDarkModeIcon)

        darkModeLayout.setOnClickListener {
            toggleDarkMode(btnTheme, darkModeIcon, txtName, txtCountry, txtBio,
                statApplied, statReviewed, statInterview)
        }
        btnTheme.setOnClickListener {
            toggleDarkMode(btnTheme, darkModeIcon, txtName, txtCountry, txtBio,
                statApplied, statReviewed, statInterview)
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                rootLayout.alpha = 1 - slideOffset * 0.5f
                rootLayout.translationX = drawerView.width * slideOffset * 0.3f
            }
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                rootLayout.alpha = 1f
                rootLayout.translationX = 0f
            }
            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    // ---------------- DIALOG EXPERIENCE -----------------
    private fun showAddExperienceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_experience, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val jobPosition = dialogView.findViewById<EditText>(R.id.inputJobPosition)
        val companyName = dialogView.findViewById<EditText>(R.id.inputCompanyName)
        val address = dialogView.findViewById<EditText>(R.id.inputAddress)
        val startDate = dialogView.findViewById<EditText>(R.id.inputStartDate)
        val endDate = dialogView.findViewById<EditText>(R.id.inputEndDate)
        val addBtn = dialogView.findViewById<Button>(R.id.btnAddExperience)

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

            txtNoExperience.visibility = View.GONE
            val item = TextView(this)
            item.text = "$job at $company\n$start - $end"
            item.setTextColor(Color.parseColor("#1F4E5F"))
            item.setPadding(8,8,8,8)
            experienceContainer.addView(item)
            dialog.dismiss()
        }

        dialog.show()
    }

    // ---------------- DIALOG EDUCATION -----------------
    private fun showAddEducationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_education, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val university = dialogView.findViewById<EditText>(R.id.inputUniversity)
        val degree = dialogView.findViewById<EditText>(R.id.inputDegree)
        val field = dialogView.findViewById<EditText>(R.id.inputField)
        val start = dialogView.findViewById<EditText>(R.id.inputEduStart)
        val end = dialogView.findViewById<EditText>(R.id.inputEduEnd)
        val addBtn = dialogView.findViewById<Button>(R.id.btnAddEducation)

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

            txtNoEducation.visibility = View.GONE
            val item = TextView(this)
            item.text = "$deg - $uni\n$fld\n$startDate - $endDate"
            item.setTextColor(Color.parseColor("#1F4E5F"))
            item.setPadding(8,8,8,8)
            educationContainer.addView(item)
            dialog.dismiss()
        }

        dialog.show()
    }

    // ---------------- SKILL -----------------
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

            txtNoSkill.visibility = View.GONE

            val tv = TextView(this)
            tv.text = skill
            tv.setTextColor(Color.WHITE)
            tv.textSize = 14f
            tv.setPadding(32, 16, 32, 16)
            tv.background = ContextCompat.getDrawable(this, R.drawable.bg_item_profile)

            // couleur aléatoire dynamique
            val randomColor = getRandomColor()
            (tv.background as GradientDrawable).setColor(randomColor)

            // marge
            val params = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(12, 12, 12, 12)
            tv.layoutParams = params

            skillsContainer.addView(tv)

            dialog.dismiss()
        }

        dialog.show()
    }

    // Fonction pour générer une couleur aléatoire
    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }


    // ---------------- LANGUAGE -----------------
    private fun showAddLanguageDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_item, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val title = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val input = dialogView.findViewById<EditText>(R.id.inputItem)
        val addBtn = dialogView.findViewById<Button>(R.id.btnAddItem)
        title.text = "Add Language"

        addBtn.setOnClickListener {
            val language = input.text.toString().trim()
            if (language.isEmpty()) {
                input.error = "Enter language"
                return@setOnClickListener
            }

            txtNoLanguage.visibility = View.GONE
            val tv = TextView(this)
            tv.text = language
            tv.setTextColor(Color.WHITE)
            tv.textSize = 14f
            tv.setPadding(32, 16, 32, 16)
            tv.background = ContextCompat.getDrawable(this, R.drawable.bg_item_profile)

            // couleur aléatoire dynamique
            val randomColor = getRandomColor()
            (tv.background as GradientDrawable).setColor(randomColor)

            // marge
            val params = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(12, 12, 12, 12)
            tv.layoutParams = params

            languagesContainer.addView(tv)
            dialog.dismiss()
        }

        dialog.show()
    }

    // ---------------- DATE PICKER -----------------
    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dpd = DatePickerDialog(this, { _, year, month, day ->
            val selectedDate = String.format("%02d/%02d/%04d", day, month+1, year)
            editText.setText(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        dpd.show()
    }

    // ---------------- DARK MODE -----------------
    private fun toggleDarkMode(
        btnTheme: ImageView,
        darkModeIcon: ImageView,
        txtName: TextView,
        txtCountry: TextView,
        txtBio: TextView,
        statApplied: TextView,
        statReviewed: TextView,
        statInterview: TextView
    ) {
        if (isDarkMode) {
            btnTheme.setImageResource(R.drawable.ic_sun)
            darkModeIcon.setImageResource(R.drawable.ic_dark_mode)
            rootLayout.setBackgroundColor(Color.parseColor("#F5F7FA"))
            txtName.setTextColor(Color.parseColor("#1F4E5F"))
            txtCountry.setTextColor(Color.parseColor("#6E7A8A"))
            txtBio.setTextColor(Color.parseColor("#1F4E5F"))
            statApplied.setTextColor(Color.parseColor("#1F4E5F"))
            statReviewed.setTextColor(Color.parseColor("#1F4E5F"))
            statInterview.setTextColor(Color.parseColor("#1F4E5F"))
            isDarkMode = false
        } else {
            btnTheme.setImageResource(R.drawable.ic_moon)
            darkModeIcon.setImageResource(R.drawable.ic_sun)
            rootLayout.setBackgroundColor(Color.parseColor("#1F1F1F"))
            txtName.setTextColor(Color.parseColor("#FFFFFF"))
            txtCountry.setTextColor(Color.parseColor("#BBBBBB"))
            txtBio.setTextColor(Color.parseColor("#FFFFFF"))
            statApplied.setTextColor(Color.parseColor("#FFFFFF"))
            statReviewed.setTextColor(Color.parseColor("#FFFFFF"))
            statInterview.setTextColor(Color.parseColor("#FFFFFF"))
            isDarkMode = true
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
