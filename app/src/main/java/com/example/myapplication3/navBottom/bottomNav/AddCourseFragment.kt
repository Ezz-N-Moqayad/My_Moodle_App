package com.example.myapplication3.navBottom.bottomNav

import android.app.AlertDialog
import android.content.Intent
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
import com.example.myapplication3.logIn.LogInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddCourseFragment : Fragment() {

    private lateinit var addNameCourse: EditText
    private lateinit var addNumberCourse: EditText
    private lateinit var addCourse: Button

    lateinit var database: DatabaseReference
    var db: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_course, container, false)

        addNameCourse = view.findViewById(R.id.addNameCourse)
        addNumberCourse = view.findViewById(R.id.addNumberCourse)
        addCourse = view.findViewById(R.id.addCourse)

        database = Firebase.database.reference
        db = Firebase.firestore
        auth = Firebase.auth

        var lec = ""
        db!!.collection("Lecturer").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    if (document.get("Email") == auth.currentUser!!.email) {
                        lec = "${document.get("First_Name").toString()} ${
                            document.get("Middle_Name").toString()
                        } ${document.get("Family_Name").toString()}"
                    }
                }
            }

        addCourse.setOnClickListener {
            if (addNameCourse.text.isEmpty() || addNumberCourse.text.isEmpty()) {
                Toast.makeText(context, "Fill Fields", Toast.LENGTH_SHORT).show()
            } else {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Add Course")
                builder.setMessage("Do you want to Add the Course?")
                builder.setPositiveButton("Yes") { _, _ ->
                    addCourse(
                        id.toString(),
                        addNameCourse.text.toString(),
                        addNumberCourse.text.toString(),
                        lec
                    )
                    Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show()
                    addNameCourse.text.clear()
                    addNumberCourse.text.clear()


                    findNavController().navigate(R.id.action_add_to_home)


                }
                builder.setNegativeButton("No") { d, _ ->
                    d.dismiss()
                }
                builder.create().show()
            }
        }
        return view
    }

    private fun addCourse(
        id: String,
        nameBook: String,
        nameAuthor: String,
        launchYear: String
    ) {
        val course = hashMapOf(
            "id" to id,
            "Name_Course" to nameBook,
            "Number_Course" to nameAuthor,
            "Lecturer" to launchYear,
        )
        database.child("Courses/$id").setValue(course)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.addCourse_toolbar)
        NavigationUI.setupWithNavController(toolbar, findNavController())
    }
}
