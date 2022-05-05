package com.example.myapplication3.navBottom.bottomNav

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.myapplication3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddCourseFragment : Fragment() {

    private lateinit var addNameCourse: EditText
    private lateinit var addNumberCourse: EditText
    private lateinit var addCourse: Button

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_course, container, false)

        addNameCourse = view.findViewById(R.id.addNameCourse)
        addNumberCourse = view.findViewById(R.id.addNumberCourse)
        addCourse = view.findViewById(R.id.addCourse)

        auth = Firebase.auth
        database = Firebase.database.reference
        val idCourse = System.currentTimeMillis()
        var lec = ""
        var idLecturer = ""

        database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    lec = "${document.child("First_Name").value.toString()} ${
                        document.child("Middle_Name").value.toString()
                    } ${document.child("Family_Name").value.toString()}"
                    idLecturer = document.child("id_Lecturer").value.toString()
                }
            }
        }
        addCourse.setOnClickListener {
            if (addNameCourse.text.isEmpty() || addNumberCourse.text.isEmpty()) {
                Toast.makeText(context, "Fill Fields", Toast.LENGTH_SHORT).show()
            } else if (addNumberCourse.text.length != 5) {
                Toast.makeText(
                    context, "Course number must consist of 5 digits", Toast.LENGTH_SHORT
                ).show()
            } else {
                var Name_Course = "null"
                var Number_Course = "null"
                database.child("Courses").get()
                    .addOnSuccessListener { dataSnapshot ->
                        for (document in dataSnapshot.children) {
                            if (document.child("Name_Course").value.toString() == addNameCourse.text.toString()) {
                                Name_Course = "Name_Course"
                                Toast.makeText(
                                    context,
                                    "This ${addNameCourse.text} is already in use, try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (document.child("Number_Course").value.toString() == addNumberCourse.text.toString()) {
                                Number_Course = "Number_Course"
                                Toast.makeText(
                                    context,
                                    "This ${addNumberCourse.text} is already in use, try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                Handler().postDelayed({
                    when {
                        Name_Course == "Name_Course" -> {}
                        Number_Course == "Number_Course" -> {}
                        else -> {
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Add Course")
                            builder.setMessage("Do you want to Add the Course?")
                            builder.setPositiveButton("Yes") { _, _ ->
                                addCourse(
                                    idCourse.toString(),
                                    addNameCourse.text.toString(),
                                    addNumberCourse.text.toString(),
                                    lec,
                                    idLecturer
                                )
                                Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT)
                                    .show()
                                findNavController().navigate(R.id.action_add_to_home)
                            }
                            builder.setNegativeButton("No") { d, _ ->
                                d.dismiss()
                                Toast.makeText(context, "Add Failed", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_add_to_home)
                            }
                            builder.create().show()
                        }
                    }
                }, 500)
            }
        }
        return view
    }

    private fun addCourse(
        id_Course: String,
        Name_Course: String,
        Number_Course: String,
        Lecturer: String,
        id_Lecturer: String
    ) {
        val course = hashMapOf(
            "id_Course" to id_Course,
            "Name_Course" to Name_Course,
            "Number_Course" to Number_Course,
            "Lecturer" to Lecturer,
            "id_Lecturer" to id_Lecturer
        )
        database.child("Lecturer/$id_Lecturer/Courses/$id_Course").setValue(course)
        database.child("Courses/$id_Course").setValue(course)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.addCourse_toolbar)
        NavigationUI.setupWithNavController(toolbar, findNavController())
    }
}