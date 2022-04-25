package com.example.myapplication3.navBottom.homeScreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication3.R
import com.example.myapplication3.modle.Course
import com.example.myapplication3.navBottom.homeScreen.course.CoursePage
import com.example.myapplication3.navBottom.listscreen.MyAdapter
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeLecturer : Fragment() {

    private lateinit var rvCourseLecturer: RecyclerView

    lateinit var auth: FirebaseAuth
    private var db: FirebaseFirestore? = null
    private var adapter: FirestoreRecyclerAdapter<Course, CourseViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_lecturer, container, false)

        rvCourseLecturer = view.findViewById(R.id.rvCourseLecturer)

        auth = Firebase.auth
        db = Firebase.firestore

        val query = db!!.collection("Courses")
        val options =
            FirestoreRecyclerOptions.Builder<Course>().setQuery(query, Course::class.java).build()

        adapter = object : FirestoreRecyclerAdapter<Course, CourseViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
                val root = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false)
                return CourseViewHolder(root)
            }

            override fun onBindViewHolder(holder: CourseViewHolder, position: Int, model: Course) {
                holder.course_name.text = model.Name_Course
                holder.course_lecturer.text = model.Lecturer
                holder.course_number.text = model.Number_Course

                holder.course_layout.setOnClickListener {
                    val i = Intent(context, CoursePage::class.java)
                    i.putExtra("id", model.id)
                    i.putExtra("Name_Course", model.Name_Course)
                    i.putExtra("Number_Course", model.Number_Course)
                    i.putExtra("Lecturer", model.Lecturer)
                    startActivity(i)

//                    findNavController().navigate(R.id.action_title_to_about)
                }
            }
        }
        rvCourseLecturer.layoutManager = LinearLayoutManager(context)
        rvCourseLecturer.adapter = adapter

        return view
    }

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var course_name = view.findViewById<TextView>(R.id.course_name)!!
        var course_lecturer = view.findViewById<TextView>(R.id.course_lecturer)!!
        var course_number = view.findViewById<TextView>(R.id.course_number)!!
        var course_layout = view.findViewById<LinearLayout>(R.id.course_layout)!!
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }
}