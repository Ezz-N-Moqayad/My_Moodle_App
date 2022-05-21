package com.example.myapplication3.navBottom.homeScreen.course.edit

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.myapplication3.R
import com.example.myapplication3.navBottom.homeScreen.course.CoursePageLecturer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditAssignment : AppCompatActivity() {

    private lateinit var editNameAss: EditText
    private lateinit var editRequiredAss: EditText
    private lateinit var editAss: Button
    private lateinit var backPageCourseAss: ImageView

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_assignment)

        editNameAss = findViewById(R.id.editNameAss)
        editRequiredAss = findViewById(R.id.editRequiredAss)
        editAss = findViewById(R.id.editAss)
        backPageCourseAss = findViewById(R.id.backPageCourseAss)

        auth = Firebase.auth
        database = Firebase.database.reference

        editNameAss.setText(intent.getStringExtra("Name_Assignment").toString())
        editRequiredAss.setText(intent.getStringExtra("Required_Assignment").toString())

        var idLecturer = ""

        database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    idLecturer = document.child("id_Lecturer").value.toString()
                }
            }
        }
        backPageCourseAss.setOnClickListener {
            intent(Intent(this, CoursePageLecturer::class.java))
        }

        editAss.setOnClickListener {
            val idCourse = intent.getStringExtra("id_Course").toString()
            if (editNameAss.text.isEmpty() || editRequiredAss.text.isEmpty()) {
                Toast.makeText(this, "File Fields", Toast.LENGTH_SHORT).show()
            } else {
                var nameAssignment = "null"
                database.child("Lecturer/$idLecturer/Courses/$idCourse/Assignment").get()
                    .addOnSuccessListener { dataSnapshot ->
                        for (document in dataSnapshot.children) {
                            if (document.child("Name_Assignment").value.toString() == editNameAss.text.toString()) {
                                nameAssignment = "nameAssignment"
                                Toast.makeText(
                                    this, "This ${editNameAss.text} is already in use, try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                Handler().postDelayed({
                    when (nameAssignment) {
                        "nameAssignment" -> {}
                        else -> {
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("Edit Assignment")
                            builder.setMessage("Do you want to Edit the Assignment?")
                            builder.setPositiveButton("Yes") { _, _ ->
                                editAssignment(
                                    editNameAss.text.toString(),
                                    editRequiredAss.text.toString(),
                                    idLecturer
                                )
                                Toast.makeText(this, "Edit Successfully", Toast.LENGTH_SHORT)
                                    .show()
                                intent(Intent(this, CoursePageLecturer::class.java))
                            }
                            builder.setNegativeButton("No") { d, _ ->
                                d.dismiss()
                                intent(Intent(this, CoursePageLecturer::class.java))
                            }
                            builder.create().show()
                        }
                    }
                }, 500)
            }
        }
    }

    private fun editAssignment(
        Name_Assignment: String,
        Required_Assignment: String,
        id_Lecturer: String
    ) {
        val assignment = mapOf(
            "Name_Assignment" to Name_Assignment,
            "Required_Assignment" to Required_Assignment
        )
        val idCourse = intent.getStringExtra("id_Course").toString()
        val idAssignment = intent.getStringExtra("id_Assignment").toString()
        database.child("Courses/$idCourse/Assignment/$idAssignment").updateChildren(assignment)
        database.child("Lecturer/$id_Lecturer/Courses/$idCourse/Assignment/$idAssignment")
            .updateChildren(assignment)
    }

    fun intent(Intent_Page: Intent) {
        Intent_Page.putExtra("id_Course", intent.getStringExtra("id_Course").toString())
        Intent_Page.putExtra("id_Lecturer", intent.getStringExtra("id_Lecturer").toString())
        Intent_Page.putExtra("Name_Course", intent.getStringExtra("Name_Course").toString())
        Intent_Page.putExtra("Number_Course", intent.getStringExtra("Number_Course").toString())
        Intent_Page.putExtra("Lecturer", intent.getStringExtra("Lecturer").toString())
        startActivity(Intent_Page)
    }
}