package com.example.myapplication3.navBottom.searchScreen

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication3.R
import com.example.myapplication3.modle.Course
import com.example.myapplication3.modle.Lecturer
import com.example.myapplication3.modle.Student
import com.example.myapplication3.navBottom.homeScreen.course.CoursePage
import com.example.myapplication3.navBottom.homeScreen.course.ViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchCourseFragment : Fragment() {

    private lateinit var rvSearchCourse: RecyclerView
    private lateinit var etSearch: EditText

    var auth: FirebaseAuth = Firebase.auth
    var database: DatabaseReference = Firebase.database.reference
    lateinit var adapterLecturer: FirebaseRecyclerAdapter<Course, ViewHolder.CourseViewHolderLecturer>
    lateinit var adapterStudent: FirebaseRecyclerAdapter<Course, ViewHolder.CourseViewHolderStudent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search_course, container, false)

        etSearch = view.findViewById(R.id.etSearch)
        rvSearchCourse = view.findViewById(R.id.rvSearchCourse)
        rvSearchCourse.layoutManager = LinearLayoutManager(context)

        var idStudent = ""
        database.child("Student").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    idStudent = document.child("id_Student").value.toString()
                }
            }
        }
        var search = ""
        Handler().postDelayed({
            getAllCourses(rvSearchCourse, search, idStudent)
        }, 1500)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                search = etSearch.text.toString().trim()
                getAllCourses(rvSearchCourse, search, idStudent)
            }
        })
        return view
    }

    private fun getAllCourses(rvSearchCourse: RecyclerView, etSearch: String, idStudent: String) {
        if (idStudent.isEmpty()) {
            val query = database.child("Courses").orderByChild("Name_Course").startAt(etSearch)
                .endAt(etSearch + "\uf8ff")
            val options =
                FirebaseRecyclerOptions.Builder<Course>().setQuery(query, Course::class.java)
                    .build()
            adapterLecturer = object :
                FirebaseRecyclerAdapter<Course, ViewHolder.CourseViewHolderLecturer>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ViewHolder.CourseViewHolderLecturer {
                    val root =
                        LayoutInflater.from(context).inflate(R.layout.course_item, parent, false)
                    return ViewHolder.CourseViewHolderLecturer(root)
                }

                override fun onBindViewHolder(
                    holder: ViewHolder.CourseViewHolderLecturer,
                    position: Int,
                    model: Course
                ) {
                    holder.course_name.text = model.Name_Course
                    holder.course_lecturer.text = model.Lecturer
                    holder.course_number.text = model.Number_Course
                }
            }
            adapterLecturer.startListening()
            rvSearchCourse.adapter = adapterLecturer
        } else {
            val query = database.child("Courses").orderByChild("Name_Course").startAt(etSearch)
                .endAt(etSearch + "\uf8ff")
            val options =
                FirebaseRecyclerOptions.Builder<Course>().setQuery(query, Course::class.java)
                    .build()
            adapterStudent = object :
                FirebaseRecyclerAdapter<Course, ViewHolder.CourseViewHolderStudent>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ViewHolder.CourseViewHolderStudent {
                    val root = LayoutInflater.from(context)
                        .inflate(R.layout.course_student_item, parent, false)
                    return ViewHolder.CourseViewHolderStudent(root)
                }

                override fun onBindViewHolder(
                    holder: ViewHolder.CourseViewHolderStudent,
                    position: Int,
                    model: Course
                ) {
                    holder.course_name.text = model.Name_Course
                    holder.course_lecturer.text = model.Lecturer
                    holder.course_number.text = model.Number_Course
                    holder.btnAddCourse.setOnClickListener {
                        var countCourse = 0
                        var chooseCourse = false
                        database.child("Student/$idStudent/Courses").get()
                            .addOnSuccessListener { dataSnapshot ->
                                for (document in dataSnapshot.children) {
                                    if (model.Number_Course == document.child("Number_Course").value.toString()) {
                                        chooseCourse = true
                                    }
                                    countCourse++
                                }
                            }
                        Handler().postDelayed({
                            when {
                                countCourse == 5 -> {
                                    Toast.makeText(
                                        context,
                                        "You have reached your registration limit",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                chooseCourse -> {
                                    Toast.makeText(
                                        context,
                                        "The course is already registered",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {
                                    val builder = AlertDialog.Builder(context)
                                    builder.setTitle("Add Course")
                                    builder.setMessage("Do you want to Add the Course?")
                                    builder.setPositiveButton("Yes") { _, _ ->
                                        addCourseStudent(
                                            model.id_Course,
                                            model.Name_Course,
                                            model.Number_Course,
                                            model.Lecturer,
                                            model.id_Lecturer,
                                            idStudent
                                        )
                                        Toast.makeText(
                                            context,
                                            "Added Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    builder.setNegativeButton("No") { d, _ ->
                                        d.dismiss()
                                        Toast.makeText(context, "Add Failed", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    builder.create().show()
                                }
                            }
                        }, 1000)
                    }
                }
            }
            adapterStudent.startListening()
            rvSearchCourse.adapter = adapterStudent
        }
    }

    private fun addCourseStudent(
        id_Course: String,
        Name_Course: String,
        Number_Course: String,
        Lecturer: String,
        id_Lecturer: String,
        idStudent: String
    ) {
        val course = hashMapOf(
            "id_Course" to id_Course,
            "Name_Course" to Name_Course,
            "Number_Course" to Number_Course,
            "Lecturer" to Lecturer,
            "id_Lecturer" to id_Lecturer
        )
        database.child("Student/$idStudent/Courses/$id_Course").setValue(course)
    }
}
