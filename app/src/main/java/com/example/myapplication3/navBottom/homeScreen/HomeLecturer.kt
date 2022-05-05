package com.example.myapplication3.navBottom.homeScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication3.R
import com.example.myapplication3.modle.Course
import com.example.myapplication3.navBottom.homeScreen.course.CoursePage
import com.example.myapplication3.navBottom.homeScreen.course.ViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeLecturer : Fragment() {

    private lateinit var rvCourseLecturer: RecyclerView

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var adapter: FirebaseRecyclerAdapter<Course, ViewHolder.CourseViewHolder>
    private val delayMillis: Long = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_lecturer, container, false)

        rvCourseLecturer = view.findViewById(R.id.rvCourseLecturer)

        auth = Firebase.auth
        database = Firebase.database.reference

        var idLecturer = ""
        database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    idLecturer = document.child("id_Lecturer").value.toString()
                }
            }
        }
        var idStudent = ""
        database.child("Student").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    idStudent = document.child("id_Student").value.toString()
                }
            }
        }
        Handler().postDelayed({
            getAllCourses(rvCourseLecturer, idLecturer, idStudent)
        }, delayMillis)
        return view
    }

    private fun getAllCourses(
        rvCourseLecturer: RecyclerView,
        idLecturer: String,
        idStudent: String
    ) {
        val options: FirebaseRecyclerOptions<Course> = if (idLecturer.isEmpty()) {
            val query = database.child("Student/$idStudent/Courses")
            FirebaseRecyclerOptions.Builder<Course>().setQuery(query, Course::class.java).build()
        } else {
            val query = database.child("Lecturer/$idLecturer/Courses")
            FirebaseRecyclerOptions.Builder<Course>().setQuery(query, Course::class.java).build()
        }
        adapter = object :
            FirebaseRecyclerAdapter<Course, ViewHolder.CourseViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ViewHolder.CourseViewHolder {
                val root = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false)
                return ViewHolder.CourseViewHolder(root)
            }

            override fun onBindViewHolder(
                holder: ViewHolder.CourseViewHolder,
                position: Int,
                model: Course
            ) {
                holder.course_name.text = model.Name_Course
                holder.course_lecturer.text = model.Lecturer
                holder.course_number.text = model.Number_Course
                holder.course_layout.setOnClickListener {
                    val i = Intent(context, CoursePage::class.java)
                    i.putExtra("id_Lecturer", model.id_Lecturer)
                    i.putExtra("id_Student", idStudent)
                    i.putExtra("Lecturer", model.Lecturer)
                    i.putExtra("id_Course", model.id_Course)
                    i.putExtra("Name_Course", model.Name_Course)
                    i.putExtra("Number_Course", model.Number_Course)
                    startActivity(i)
                }
            }
        }
        rvCourseLecturer.layoutManager = LinearLayoutManager(context)
        rvCourseLecturer.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            adapter.startListening()
        }, delayMillis)
    }

    override fun onStop() {
        super.onStop()
        Handler().postDelayed({
            adapter.stopListening()
        }, delayMillis)
    }
}
