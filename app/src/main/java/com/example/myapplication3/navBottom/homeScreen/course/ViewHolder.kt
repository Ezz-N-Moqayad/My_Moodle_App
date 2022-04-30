package com.example.myapplication3.navBottom.homeScreen.course

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication3.R

class ViewHolder {

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var course_name = view.findViewById<TextView>(R.id.course_name)!!
        var course_lecturer = view.findViewById<TextView>(R.id.course_lecturer)!!
        var course_number = view.findViewById<TextView>(R.id.course_number)!!
        var course_layout = view.findViewById<LinearLayout>(R.id.course_layout)!!
    }

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var file_course_name = view.findViewById<TextView>(R.id.file_course_name)!!
        var file_course_number = view.findViewById<TextView>(R.id.file_course_number)!!
    }

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var video_course_name = view.findViewById<TextView>(R.id.video_course_name)!!
    }

    class AssignmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var assignment_course_name = view.findViewById<TextView>(R.id.assignment_course_name)!!
    }
}