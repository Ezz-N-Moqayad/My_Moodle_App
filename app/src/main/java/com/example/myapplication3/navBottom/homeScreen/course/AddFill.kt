package com.example.myapplication3.navBottom.homeScreen.course

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.myapplication3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddFill : AppCompatActivity() {

    private lateinit var addNameFill: EditText
    private lateinit var addNumberFill: EditText
    private lateinit var addFill: Button
    private lateinit var backPageCourseFill: ImageView

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_fill)

        addNameFill = findViewById(R.id.addNameFill)
        addNumberFill = findViewById(R.id.addNumberFill)
        addFill = findViewById(R.id.addFill)
        backPageCourseFill = findViewById(R.id.backPageCourseFill)

        auth = Firebase.auth
        db = Firebase.firestore

        backPageCourseFill.setOnClickListener {
            intent(Intent(this, CoursePage::class.java))
        }


//        addNameFill = findViewById(R.id.addNameFill)
//        addNumberFill = findViewById(R.id.addNumberFill)
//        addFill = findViewById(R.id.addFill)
//


//        addFill.setOnClickListener {
//            if (addNameFill.text.isEmpty() || addNumberFill.text.isEmpty()) {
//                Toast.makeText(this, "Fill Fields", Toast.LENGTH_SHORT).show()
//            } else if (addNumberCourse.text.length != 5) {
//                Toast.makeText(
//                    this, "Course number must consist of 5 digits", Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                var Name_Course = "null"
//                var Number_Course = "null"
//                db!!.collection("Courses").get()
//                    .addOnSuccessListener { querySnapshot ->
//                        for (document in querySnapshot) {
//                            if (document.get("Name_Course") == addNameFill.text.toString()) {
//                                Name_Course = "Name_Course"
//                                Toast.makeText(
//                                    this,
//                                    "This ${addNameFill.text} is already in use, try again",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                            if (document.get("Number_Course") == addNumberCourse.text.toString()) {
//                                Number_Course = "Number_Course"
//                                Toast.makeText(
//                                    this,
//                                    "This ${addNumberCourse.text} is already in use, try again",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    }
//                Handler().postDelayed({
//                    when {
//                        Name_Course == "Name_Course" -> {}
//                        Number_Course == "Number_Course" -> {}
//                        else -> {
//                            val builder = AlertDialog.Builder(this)
//                            builder.setTitle("Add Course")
//                            builder.setMessage("Do you want to Add the Course?")
//                            builder.setPositiveButton("Yes") { _, _ ->
//                                addCourse(
//                                    id.toString(),
//                                    addNameFill.text.toString(),
//                                    addNumberCourse.text.toString(),
//                                    lec
//                                )
//                                Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT)
//                                    .show()
//                                addNameFill.text.clear()
//                                addNumberCourse.text.clear()
//                                findNavController().navigate(R.id.action_add_to_home)
//                            }
//                            builder.setNegativeButton("No") { d, _ ->
//                                d.dismiss()
//                                findNavController().navigate(R.id.action_add_to_home)
//                            }
//                            builder.create().show()
//                        }
//                    }
//                }, 1500)
//            }
//        }


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