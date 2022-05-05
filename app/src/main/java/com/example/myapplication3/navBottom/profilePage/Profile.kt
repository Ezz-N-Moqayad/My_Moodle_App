package com.example.myapplication3.navBottom.profilePage

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myapplication3.R
import com.example.myapplication3.logIn.LogInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class Profile : Fragment() {

    private lateinit var et_fNamePro: EditText
    private lateinit var et_mNamePro: EditText
    private lateinit var et_lNamePro: EditText
    private lateinit var et_birth_datePro: EditText
    private lateinit var et_addressPro: EditText
    private lateinit var et_emailPro: EditText
    private lateinit var et_mobilePro: EditText
    private lateinit var editPro: TextView
    private lateinit var btn_loginOut: ImageButton
    private var edo = 0

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        et_fNamePro = view.findViewById(R.id.et_fNamePro)
        et_mNamePro = view.findViewById(R.id.et_mNamePro)
        et_lNamePro = view.findViewById(R.id.et_lNamePro)
        et_birth_datePro = view.findViewById(R.id.et_birth_datePro)
        et_addressPro = view.findViewById(R.id.et_addressPro)
        et_emailPro = view.findViewById(R.id.et_emailPro)
        et_mobilePro = view.findViewById(R.id.et_mobilePro)
        editPro = view.findViewById(R.id.editPro)
        btn_loginOut = view.findViewById(R.id.btn_loginOut)

        database = Firebase.database.reference
        auth = Firebase.auth

        getProfileData()

        et_birth_datePro.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH)
            val year = currentDate.get(Calendar.YEAR)
            val picker = context?.let { it1 ->
                DatePickerDialog(
                    it1, { _, y, m, d ->
                        et_birth_datePro.setText("$y / ${m + 1} / $d")
                    }, year, month, day
                )
            }
            picker!!.show()
        }
        btn_loginOut.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Logout")
            builder.setMessage("Do you want to Logout?")
            builder.setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(context, LogInActivity::class.java))
            }
            builder.setNegativeButton("No") { d, _ ->
                d.dismiss()
            }
            builder.create().show()
        }

        var lec = ""
        database.child("Lecturer").get().addOnSuccessListener { dataSnapshot2 ->
            for (document2 in dataSnapshot2.children) {
                if (document2.child("Email").value.toString() == auth.currentUser!!.email) {
                    lec = "${document2.child("First_Name").value.toString()} ${
                        document2.child("Middle_Name").value.toString()
                    } ${document2.child("Family_Name").value.toString()}"
                }
            }
        }

        disableEdit()
        editPro.setOnClickListener {
            if (editPro.text.toString() == " Edit Profile ") {
                editPro.text = "Save"
                editPro.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_check,
                    0,
                    0,
                    0
                )
                enableEdit()
            } else {
                if (et_fNamePro.text.isEmpty() || et_mNamePro.text.isEmpty() || et_lNamePro.text.isEmpty() ||
                    et_birth_datePro.text.isEmpty() || et_addressPro.text.isEmpty() ||
                    et_emailPro.text.isEmpty() || et_mobilePro.text.isEmpty()
                ) {
                    Toast.makeText(
                        context, "The modification process must be completed", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    editPro.text = " Edit Profile "
                    editPro.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_save,
                        0,
                        0,
                        0
                    )
                    disableEdit()
                    editUser(lec)
                }
            }
        }
        return view
    }

    private fun getProfileData() {
        database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    et_fNamePro.setText(document.child("First_Name").value.toString())
                    et_mNamePro.setText(document.child("Middle_Name").value.toString())
                    et_lNamePro.setText(document.child("Family_Name").value.toString())
                    et_emailPro.setText(document.child("Email").value.toString())
                    et_birth_datePro.setText(document.child("Birth_Date").value.toString())
                    et_addressPro.setText(document.child("Address").value.toString())
                    et_mobilePro.setText(document.child("Mobile").value.toString())
                }
            }
        }
        database.child("Student").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    et_fNamePro.setText(document.child("First_Name").value.toString())
                    et_mNamePro.setText(document.child("Middle_Name").value.toString())
                    et_lNamePro.setText(document.child("Family_Name").value.toString())
                    et_emailPro.setText(document.child("Email").value.toString())
                    et_birth_datePro.setText(document.child("Birth_Date").value.toString())
                    et_addressPro.setText(document.child("Address").value.toString())
                    et_mobilePro.setText(document.child("Mobile").value.toString())
                }
            }
        }
    }

    private fun editUser(lec: String) {
        database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    val lecturer = mapOf(
                        "First_Name" to et_fNamePro.text.toString(),
                        "Middle_Name" to et_mNamePro.text.toString(),
                        "Family_Name" to et_lNamePro.text.toString(),
                        "Birth_Date" to et_birth_datePro.text.toString(),
                        "Address" to et_addressPro.text.toString(),
                        "Mobile" to et_mobilePro.text.toString()
                    )
                    val idLecturer = document.child("id_Lecturer").value.toString()
                    database.child("Lecturer/$idLecturer").updateChildren(lecturer)

                    val lecturerName = "${et_fNamePro.text} ${et_mNamePro.text} ${et_lNamePro.text}"

                    database.child("Courses").get().addOnSuccessListener { dataSnapshot2 ->
                        for (document2 in dataSnapshot2.children) {
                            if (document2.child("Lecturer").value.toString() == lec) {
                                val idCourse = document2.child("id_Course").value.toString()
                                val course = mapOf(
                                    "Lecturer" to lecturerName
                                )
                                database.child("Courses/$idCourse").updateChildren(course)
                            }
                        }
                    }
                    database.child("Lecturer/$idLecturer/Courses").get()
                        .addOnSuccessListener { dataSnapshot3 ->
                            for (document3 in dataSnapshot3.children) {
                                if (document3.child("Lecturer").value.toString() == lec) {
                                    val idCourse = document3.child("id_Course").value.toString()
                                    val course = mapOf(
                                        "Lecturer" to lecturerName,
                                    )
                                    database.child("Lecturer/$idLecturer/Courses/$idCourse")
                                        .updateChildren(course)
                                }
                            }
                        }
                }
            }
        }
        database.child("Student").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    val student = mapOf(
                        "First_Name" to et_fNamePro.text.toString(),
                        "Middle_Name" to et_mNamePro.text.toString(),
                        "Family_Name" to et_lNamePro.text.toString(),
                        "Birth_Date" to et_birth_datePro.text.toString(),
                        "Address" to et_addressPro.text.toString(),
                        "Mobile" to et_mobilePro.text.toString()
                    )
                    val idStudent = document.child("id_Student").value.toString()
                    database.child("Student").child(idStudent).updateChildren(student)
                }
                val lecturerName = "${et_fNamePro.text} ${et_mNamePro.text} ${et_lNamePro.text}"

                val idStudent = document.child("id_Student").value.toString()
                Handler().postDelayed({
                    database.child("Student/$idStudent/Courses").get()
                        .addOnSuccessListener { dataSnapshot2 ->
                            for (document2 in dataSnapshot2.children) {
                                if (document2.child("Lecturer").value.toString() == lec) {
                                    val idCourse = document2.child("id_Course").value.toString()
                                    val course = mapOf(
                                        "Lecturer" to lecturerName,
                                    )
                                    database.child("Student/$idStudent/Courses/$idCourse")
                                        .updateChildren(course)
                                }
                            }
                        }
                }, 1000)

            }
        }
    }

    private fun disableEdit() {
        et_fNamePro.isEnabled = false
        et_mNamePro.isEnabled = false
        et_lNamePro.isEnabled = false
        et_emailPro.isEnabled = false
        et_birth_datePro.isEnabled = false
        et_addressPro.isEnabled = false
        et_mobilePro.isEnabled = false
    }

    private fun enableEdit() {
        et_fNamePro.isEnabled = true
        et_mNamePro.isEnabled = true
        et_lNamePro.isEnabled = true
        et_emailPro.isEnabled = true
        et_birth_datePro.isEnabled = true
        et_addressPro.isEnabled = true
        et_mobilePro.isEnabled = true
    }
}
