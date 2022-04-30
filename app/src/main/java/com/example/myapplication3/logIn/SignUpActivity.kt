package com.example.myapplication3.logIn

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.myapplication3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var et_fName: EditText
    private lateinit var et_mName: EditText
    private lateinit var et_lName: EditText
    private lateinit var et_birth_date: EditText
    private lateinit var et_address: EditText
    private lateinit var et_email: EditText
    private lateinit var et_mobile: EditText
    private lateinit var sp_category: Spinner
    private lateinit var et_password: EditText
    private lateinit var et_confirm_password: EditText
    private lateinit var btn_sign_up: Button
    private lateinit var tv_login: TextView

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        et_fName = findViewById(R.id.et_fName)
        et_mName = findViewById(R.id.et_mName)
        et_lName = findViewById(R.id.et_lName)
        et_birth_date = findViewById(R.id.et_birth_date)
        et_address = findViewById(R.id.et_address)
        et_email = findViewById(R.id.et_email)
        et_mobile = findViewById(R.id.et_mobile)
        sp_category = findViewById(R.id.sp_category)
        et_password = findViewById(R.id.et_password)
        et_confirm_password = findViewById(R.id.et_confirm_password)
        btn_sign_up = findViewById(R.id.btn_sign_up)
        tv_login = findViewById(R.id.tv_login)

        auth = Firebase.auth
        database = Firebase.database.reference

        et_birth_date.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH)
            val year = currentDate.get(Calendar.YEAR)
            val picker = DatePickerDialog(
                this, { _, y, m, d ->
                    et_birth_date.setText("$y / ${m + 1} / $d")
                }, year, month, day
            )
            picker.show()
        }

        var categorySpinner = ""

        sp_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                categorySpinner = p0!!.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }


        btn_sign_up.setOnClickListener {
            if (et_fName.text.isEmpty() || et_mName.text.isEmpty() || et_lName.text.isEmpty() || et_birth_date.text.isEmpty() || et_address.text.isEmpty() ||
                et_email.text.isEmpty() || et_mobile.text.isEmpty() || categorySpinner == "Choose your category" || et_password.text.isEmpty() || et_confirm_password.text.isEmpty()
            ) {
                Toast.makeText(this, "Registration is Incomplete", Toast.LENGTH_SHORT).show()
            } else if (et_password.text.toString() != et_confirm_password.text.toString()) {
                Toast.makeText(this, "Password Does Not Match", Toast.LENGTH_SHORT).show()
            } else {
                createNewAccount(
                    et_email.text.toString(),
                    et_password.text.toString(),
                    categorySpinner
                )
            }
        }
        tv_login.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }
    }

    private fun createNewAccount(email: String, pass: String, categorySpinner: String) {
        val id = System.currentTimeMillis()
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                addUser(
                    id.toString(),
                    et_fName.text.toString(),
                    et_mName.text.toString(),
                    et_lName.text.toString(),
                    et_birth_date.text.toString(),
                    et_address.text.toString(),
                    et_email.text.toString(),
                    et_mobile.text.toString(),
                    categorySpinner,
                    et_password.text.toString()
                )
                Toast.makeText(this, "Successfully registered", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LogInActivity::class.java))
            } else {
                Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addUser(
        id: String,
        First_Name: String,
        Middle_Name: String,
        Family_Name: String,
        Birth_Date: String,
        Address: String,
        Email: String,
        Mobile: String,
        Category: String,
        Password: String
    ) {
        val lecturer = hashMapOf(
            "id_Lecturer" to id,
            "First_Name" to First_Name,
            "Middle_Name" to Middle_Name,
            "Family_Name" to Family_Name,
            "Birth_Date" to Birth_Date,
            "Address" to Address,
            "Email" to Email,
            "Mobile" to Mobile,
            "Category" to Category,
            "Password" to Password
        )
        val student = hashMapOf(
            "id_Student" to id,
            "First_Name" to First_Name,
            "Middle_Name" to Middle_Name,
            "Family_Name" to Family_Name,
            "Birth_Date" to Birth_Date,
            "Address" to Address,
            "Email" to Email,
            "Mobile" to Mobile,
            "Category" to Category,
            "Password" to Password
        )

        when (Category) {
            "Lecturer" -> {
                database.child("Lecturer/$id").setValue(lecturer)
            }
            "Student" -> {
                database.child("Student/$id").setValue(student)
            }
        }
    }
}