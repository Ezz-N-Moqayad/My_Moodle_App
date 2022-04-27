package com.example.myapplication3.navBottom.homeScreen.course

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import com.example.myapplication3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddFile : AppCompatActivity() {

    private lateinit var addNameFile: EditText
    private lateinit var addNumberFile: EditText
    private lateinit var addFile: Button
    private lateinit var backPageCourseFile: ImageView

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_file)

        addNameFile = findViewById(R.id.addNameFile)
        addNumberFile = findViewById(R.id.addNumberFile)
        addFile = findViewById(R.id.addFile)
        backPageCourseFile = findViewById(R.id.backPageCourseFile)

        auth = Firebase.auth
        db = Firebase.firestore
        database = Firebase.database.reference
        val idRT = System.currentTimeMillis()

        addNameFile = findViewById(R.id.addNameFile)
        addNumberFile = findViewById(R.id.addNumberFile)
        addFile = findViewById(R.id.addFile)

        backPageCourseFile.setOnClickListener {
            intent(Intent(this, CoursePage::class.java))
        }

        addFile.setOnClickListener {
            if (addNameFile.text.isEmpty() || addNumberFile.text.isEmpty()) {
                Toast.makeText(this, "File Fields", Toast.LENGTH_SHORT).show()
            } else if (addNumberFile.text.length != 8) {
                Toast.makeText(
                    this, "File number must consist of 8 digits", Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.e("ezz", "e")

                var Name_File = "null"
                var Number_File = "null"
                database.child("File").get().addOnSuccessListener { dataSnapshot ->
                    for (document in dataSnapshot.children) {
                        if (document.child("Name_File").value.toString() == addNameFile.text.toString() &&
                            document.child("Lecturer").value.toString() == intent.getStringExtra("Lecturer")
                                .toString()
                        ) {
                            Name_File = "Name_File"
                            Toast.makeText(
                                this, "This ${addNameFile.text} is already in use, try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        if (document.child("Number_File").value.toString() == addNumberFile.text.toString() &&
                            document.child("Lecturer").value.toString() == intent.getStringExtra("Lecturer")
                                .toString()
                        ) {
                            Number_File = "Number_File"
                            Toast.makeText(
                                this, "This ${addNumberFile.text} is already in use, try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                Handler().postDelayed({
                    when {
                        Name_File == "Name_File" -> {}
                        Number_File == "Number_File" -> {}
                        else -> {
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("Add File")
                            builder.setMessage("Do you want to Add the File?")
                            builder.setPositiveButton("Yes") { _, _ ->
                                addFile(
                                    idRT.toString(),
                                    addNameFile.text.toString(),
                                    addNumberFile.text.toString(),
                                    intent.getStringExtra("Name_Course").toString(),
                                    intent.getStringExtra("Number_Course").toString(),
                                    intent.getStringExtra("Lecturer").toString()
                                )
                                Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT)
                                    .show()
                                addNameFile.text.clear()
                                addNumberFile.text.clear()
                                intent(Intent(this, CoursePage::class.java))
                            }
                            builder.setNegativeButton("No") { d, _ ->
                                d.dismiss()
                                intent(Intent(this, CoursePage::class.java))
                            }
                            builder.create().show()
                        }
                    }
                }, 2500)
            }
        }
    }

    private fun addFile(
        id: String,
        Name_File: String,
        Number_File: String,
        Name_Course: String,
        Number_Course: String,
        Lecturer: String
    ) {
        val file = hashMapOf(
            "id" to id,
            "Name_File" to Name_File,
            "Number_File" to Number_File,
            "Name_Course" to Name_Course,
            "Number_Course" to Number_Course,
            "Lecturer" to Lecturer,
        )
        database.child("File/$id").setValue(file)
    }

    fun intent(Intent_Page: Intent) {
        val i = Intent_Page
        i.putExtra("id_Course", intent.getStringExtra("id_Course").toString())
        i.putExtra("Name_Course", intent.getStringExtra("Name_Course").toString())
        i.putExtra("Number_Course", intent.getStringExtra("Number_Course").toString())
        i.putExtra("Lecturer", intent.getStringExtra("Lecturer").toString())
        startActivity(i)
    }
}