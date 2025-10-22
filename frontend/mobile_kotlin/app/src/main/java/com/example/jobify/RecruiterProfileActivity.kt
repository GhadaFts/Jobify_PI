package com.example.jobify

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.util.*
import android.content.Intent
import android.widget.LinearLayout
import android.widget.TextView



class RecruiterProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnMenu: ImageView

    private lateinit var menuProfile: LinearLayout
    private lateinit var menuLogout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recruiter_profile)

        // --- Drawer + menu ---
        drawerLayout = findViewById(R.id.drawerLayout)
        btnMenu = findViewById(R.id.btnMenu)
        menuProfile = findViewById(R.id.menuProfileLayout)
        menuLogout = findViewById(R.id.menuLogoutLayout)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, RecruiterProfileActivity::class.java)
            startActivity(intent)
        }


        menuLogout.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val menuHomeLayout = findViewById<LinearLayout>(R.id.menuHomeLayout)
        menuHomeLayout.setOnClickListener {
            startActivity(Intent(this, PostsActivity::class.java))
        }


        // --- Services Section ---
        val btnAddService = findViewById<ImageView>(R.id.btnAddService)
        val servicesContainer = findViewById<LinearLayout>(R.id.servicesContainer)
        val txtNoService = findViewById<TextView>(R.id.txtNoService)

        btnAddService.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null)
            val editText = dialogView.findViewById<EditText>(R.id.inputItem)
            val btnSave = dialogView.findViewById<Button>(R.id.btnAddItem)
            val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)

            dialogTitle.text = "Add Service"
            btnSave.text = "Add Service"
            btnSave.setBackgroundColor(Color.parseColor("#E46A3C"))

            val dialog = AlertDialog.Builder(this).setView(dialogView).create()

            btnSave.setOnClickListener {
                val serviceName = editText.text.toString().trim()
                if (serviceName.isNotEmpty()) {
                    txtNoService.visibility = View.GONE
                    val inflater = LayoutInflater.from(this)
                    val serviceItem = inflater.inflate(R.layout.item_service, servicesContainer, false)
                    serviceItem.findViewById<TextView>(R.id.serviceName).text = serviceName

                    val btnDelete = serviceItem.findViewById<ImageView>(R.id.btnDeleteService)
                    btnDelete.setOnClickListener {
                        servicesContainer.removeView(serviceItem)
                        if (servicesContainer.childCount == 0) txtNoService.visibility = View.VISIBLE
                    }

                    servicesContainer.addView(serviceItem)
                    dialog.dismiss()
                } else editText.error = "Please enter a service name"
            }
            dialog.show()
        }

        // --- Specializations Section ---
        val btnAddSpecialization = findViewById<ImageView>(R.id.btnAddSpecialization)
        val specializationsContainer = findViewById<LinearLayout>(R.id.specializationsContainer)
        val txtNoSpecialization = findViewById<TextView>(R.id.txtNoSpecialization)

        btnAddSpecialization.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null)
            val editText = dialogView.findViewById<EditText>(R.id.inputItem)
            val btnSave = dialogView.findViewById<Button>(R.id.btnAddItem)
            val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)

            dialogTitle.text = "Add Specialization"
            btnSave.text = "Add Specialization"
            btnSave.setBackgroundColor(Color.parseColor("#E46A3C"))

            val dialog = AlertDialog.Builder(this).setView(dialogView).create()

            btnSave.setOnClickListener {
                val specializationName = editText.text.toString().trim()
                if (specializationName.isNotEmpty()) {
                    txtNoSpecialization.visibility = View.GONE
                    val inflater = LayoutInflater.from(this)
                    val specializationItem = inflater.inflate(R.layout.item_specialization, specializationsContainer, false)
                    specializationItem.findViewById<TextView>(R.id.specializationName).text = specializationName

                    val btnDelete = specializationItem.findViewById<ImageView>(R.id.btnDeleteSpecialization)
                    btnDelete.setOnClickListener {
                        specializationsContainer.removeView(specializationItem)
                        if (specializationsContainer.childCount == 0) txtNoSpecialization.visibility = View.VISIBLE
                    }

                    specializationsContainer.addView(specializationItem)
                    dialog.dismiss()
                } else editText.error = "Please enter a specialization"
            }
            dialog.show()
        }

        // --- Posts Section ---
        val btnAddPost = findViewById<ImageView>(R.id.btnAddPost)
        val txtNoPost = findViewById<TextView>(R.id.txtNoPost)
        val postsGrid = findViewById<GridLayout>(R.id.postsGrid)

        btnAddPost.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_post, null)
            val inputPostName = dialogView.findViewById<EditText>(R.id.inputPostName)
            val inputDescription = dialogView.findViewById<EditText>(R.id.inputDescription)
            val inputLocation = dialogView.findViewById<EditText>(R.id.inputLocation)
            val inputContractType = dialogView.findViewById<EditText>(R.id.inputContractType)
            val inputDeadline = dialogView.findViewById<EditText>(R.id.inputDeadline)
            val btnAddPostDialog = dialogView.findViewById<Button>(R.id.btnAddPost)

            // Optional: DatePicker for deadline
            inputDeadline.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(this, { _, y, m, d ->
                    inputDeadline.setText("$d/${m+1}/$y")
                }, year, month, day)
                dpd.show()
            }

            val dialog = AlertDialog.Builder(this).setView(dialogView).create()

            btnAddPostDialog.setOnClickListener {
                val postName = inputPostName.text.toString().trim()
                val description = inputDescription.text.toString().trim()
                val location = inputLocation.text.toString().trim()
                val contractType = inputContractType.text.toString().trim()
                val deadline = inputDeadline.text.toString().trim()

                if(postName.isEmpty()){
                    inputPostName.error = "Please enter a post title"
                    return@setOnClickListener
                }
                if(description.isEmpty()){
                    inputDescription.error = "Please enter a post description"
                    return@setOnClickListener
                }
                if(location.isEmpty()){
                    inputLocation.error = "Please enter a location"
                    return@setOnClickListener
                }
                if(contractType.isEmpty()){
                    inputContractType.error = "Please enter contract type"
                    return@setOnClickListener
                }
                if(deadline.isEmpty()){
                    inputDeadline.error = "Please enter a deadline"
                    return@setOnClickListener
                }

                txtNoPost.visibility = View.GONE
                val inflater = LayoutInflater.from(this)
                val postItem = inflater.inflate(R.layout.item_post, postsGrid, false)

                postItem.findViewById<TextView>(R.id.tvTitle).text = postName
                postItem.findViewById<TextView>(R.id.tvPosition).text = "" // placeholder
                postItem.findViewById<TextView>(R.id.tvExperience).text = "" // placeholder
                postItem.findViewById<TextView>(R.id.tvSalary).text = "" // placeholder
                postItem.findViewById<TextView>(R.id.tvDescription).text = description

                postsGrid.addView(postItem)
                dialog.dismiss()
            }

            dialog.show()

        }

    }
}
