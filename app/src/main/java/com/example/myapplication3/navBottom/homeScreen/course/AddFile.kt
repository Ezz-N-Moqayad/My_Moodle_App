package com.example.myapplication3.navBottom.homeScreen.course

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.*
import com.example.myapplication3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddFile : AppCompatActivity() {

    private lateinit var addNameFile: EditText
    private lateinit var addNumberFile: EditText
    private lateinit var addFile: Button
    private lateinit var backPageCourseFile: ImageView

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_file)

        addNameFile = findViewById(R.id.addNameFile)
        addNumberFile = findViewById(R.id.addNumberFile)
        addFile = findViewById(R.id.addFile)
        backPageCourseFile = findViewById(R.id.backPageCourseFile)

        auth = Firebase.auth
        database = Firebase.database.reference
        val idFile = System.currentTimeMillis()
        var idLecturer = ""

        database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    idLecturer = document.child("id_Lecturer").value.toString()
                }
            }
        }
        backPageCourseFile.setOnClickListener {
            intent(Intent(this, CoursePage::class.java))
        }

        addFile.setOnClickListener {
            val idCourse = intent.getStringExtra("id_Course").toString()
            if (addNameFile.text.isEmpty() || addNumberFile.text.isEmpty()) {
                Toast.makeText(this, "File Fields", Toast.LENGTH_SHORT).show()
            } else if (addNumberFile.text.length != 6) {
                Toast.makeText(
                    this, "File number must consist of 6 digits", Toast.LENGTH_SHORT
                ).show()
            } else {
                var nameFile = "null"
                var numberFile = "null"
                database.child("Lecturer/$idLecturer/Courses/$idCourse/File").get()
                    .addOnSuccessListener { dataSnapshot ->
                        for (document in dataSnapshot.children) {
                            if (document.child("Name_File").value.toString() == addNameFile.text.toString()) {
                                nameFile = "nameFile"
                                Toast.makeText(
                                    this, "This ${addNameFile.text} is already in use, try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (document.child("Number_File").value.toString() == addNumberFile.text.toString()) {
                                numberFile = "numberFile"
                                Toast.makeText(
                                    this, "This ${addNumberFile.text} is already in use, try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                Handler().postDelayed({
                    when {
                        nameFile == "nameFile" -> {}
                        numberFile == "numberFile" -> {}
                        else -> {
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("Add File")
                            builder.setMessage("Do you want to Add the File?")
                            builder.setPositiveButton("Yes") { _, _ ->
                                addFile(
                                    idFile.toString(),
                                    addNameFile.text.toString(),
                                    addNumberFile.text.toString(),
                                    intent.getStringExtra("Name_Course").toString(),
                                    intent.getStringExtra("Number_Course").toString(),
                                    intent.getStringExtra("Lecturer").toString(),
                                    idLecturer
                                )
                                Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT)
                                    .show()
                                intent(Intent(this, CoursePage::class.java))
                            }
                            builder.setNegativeButton("No") { d, _ ->
                                d.dismiss()
                                intent(Intent(this, CoursePage::class.java))
                            }
                            builder.create().show()
                        }
                    }
                }, 1000)
            }
        }
    }

    private fun addFile(
        id_File: String,
        Name_File: String,
        Number_File: String,
        Name_Course: String,
        Number_Course: String,
        Lecturer: String,
        idLecturer: String
    ) {
        val file = hashMapOf(
            "id_File" to id_File,
            "Name_File" to Name_File,
            "Number_File" to Number_File,
            "Name_Course" to Name_Course,
            "Number_Course" to Number_Course,
            "Lecturer" to Lecturer,
        )
        val idCourse = intent.getStringExtra("id_Course").toString()
        database.child("Lecturer/$idLecturer/Courses/$idCourse/File/$id_File").setValue(file)
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