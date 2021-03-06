package com.example.myapplication3.navBottom.homeScreen.course.add

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

class AddAssignment : AppCompatActivity() {

    private lateinit var addNameAss: EditText
    private lateinit var addRequiredAss: EditText
    private lateinit var addAss: Button
    private lateinit var backPageCourseAss: ImageView

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_assignment)

        addNameAss = findViewById(R.id.addNameAss)
        addRequiredAss = findViewById(R.id.addRequiredAss)
        addAss = findViewById(R.id.addAss)
        backPageCourseAss = findViewById(R.id.backPageCourseAss)

        auth = Firebase.auth
        database = Firebase.database.reference
        val idAssignment = System.currentTimeMillis()
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

        addAss.setOnClickListener {
            val idCourse = intent.getStringExtra("id_Course").toString()
            if (addNameAss.text.isEmpty() || addRequiredAss.text.isEmpty()) {
                Toast.makeText(this, "File Fields", Toast.LENGTH_SHORT).show()
            } else {
                var nameAssignment = "null"
                database.child("Lecturer/$idLecturer/Courses/$idCourse/Assignment").get()
                    .addOnSuccessListener { dataSnapshot ->
                        for (document in dataSnapshot.children) {
                            if (document.child("Name_Assignment").value.toString() == addNameAss.text.toString()) {
                                nameAssignment = "nameAssignment"
                                Toast.makeText(
                                    this, "This ${addNameAss.text} is already in use, try again",
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
                            builder.setTitle("Add Assignment")
                            builder.setMessage("Do you want to Add the Assignment?")
                            builder.setPositiveButton("Yes") { _, _ ->
                                addAssignment(
                                    idAssignment.toString(),
                                    addNameAss.text.toString(),
                                    addRequiredAss.text.toString(),
                                    intent.getStringExtra("Number_Course").toString(),
                                    idLecturer
                                )
                                Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT)
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

    private fun addAssignment(
        id_Assignment: String,
        Name_Assignment: String,
        Required_Assignment: String,
        Number_Course: String,
        id_Lecturer: String
    ) {
        val assignment = hashMapOf(
            "id_Assignment" to id_Assignment,
            "Name_Assignment" to Name_Assignment,
            "Required_Assignment" to Required_Assignment,
            "Number_Course" to Number_Course,
            "id_Lecturer" to id_Lecturer
        )
        val idCourse = intent.getStringExtra("id_Course").toString()
        database.child("Courses/$idCourse/Assignment/$id_Assignment").setValue(assignment)
        database.child("Lecturer/$id_Lecturer/Courses/$idCourse/Assignment/$id_Assignment")
            .setValue(assignment)
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