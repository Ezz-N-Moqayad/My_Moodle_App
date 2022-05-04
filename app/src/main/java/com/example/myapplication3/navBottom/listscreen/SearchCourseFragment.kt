package com.example.myapplication3.navBottom.listscreen

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication3.R
import com.example.myapplication3.modle.Course
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
    lateinit var adapter: FirebaseRecyclerAdapter<Course, ViewHolder.CourseViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search_course, container, false)

        etSearch = view.findViewById(R.id.etSearch)
        rvSearchCourse = view.findViewById(R.id.rvSearchCourse)
        rvSearchCourse.layoutManager = LinearLayoutManager(context)
        var search = ""
        getAllCourses(rvSearchCourse, search)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                search = etSearch.text.toString().trim()
                getAllCourses(rvSearchCourse, search)
            }
        })
        return view
    }

    private fun getAllCourses(rvSearchCourse: RecyclerView, etSearch: String) {
        val query = database.child("Courses").orderByChild("Name_Course").startAt(etSearch)
            .endAt(etSearch + "\uf8ff")
        val options =
            FirebaseRecyclerOptions.Builder<Course>().setQuery(query, Course::class.java).build()
        adapter = object :
            FirebaseRecyclerAdapter<Course, ViewHolder.CourseViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ViewHolder.CourseViewHolder {
                val root = LayoutInflater.from(context)
                    .inflate(R.layout.course_item, parent, false)
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
            }
        }
        adapter.startListening()
        rvSearchCourse.adapter = adapter
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}
