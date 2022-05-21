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

    class CourseViewHolderStudent(view: View) : RecyclerView.ViewHolder(view) {
        var course_name = view.findViewById<TextView>(R.id.course_student_name)!!
        var course_lecturer = view.findViewById<TextView>(R.id.course_student_lecturer)!!
        var course_number = view.findViewById<TextView>(R.id.course_student_number)!!
        var btnAddCourse = view.findViewById<ImageButton>(R.id.btnAddCourse)!!
    }

    class CourseViewHolderLecturer(view: View) : RecyclerView.ViewHolder(view) {
        var course_name = view.findViewById<TextView>(R.id.course_name)!!
        var course_lecturer = view.findViewById<TextView>(R.id.course_lecturer)!!
        var course_number = view.findViewById<TextView>(R.id.course_number)!!
    }

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var file_name = view.findViewById<TextView>(R.id.file_name)!!
    }

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var video_name = view.findViewById<TextView>(R.id.video_name)!!
    }

    class AssignmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var assignment_name = view.findViewById<TextView>(R.id.assignment_name)!!
    }

    class FileViewHolderLecturer(view: View) : RecyclerView.ViewHolder(view) {
        var file_name_lecturer = view.findViewById<TextView>(R.id.file_name_lecturer)!!
        var edit_file = view.findViewById<ImageButton>(R.id.edit_file)!!
        var delete_file = view.findViewById<ImageButton>(R.id.delete_file)!!
    }

    class VideoViewHolderLecturer(view: View) : RecyclerView.ViewHolder(view) {
        var video_name_lecturer = view.findViewById<TextView>(R.id.video_name_lecturer)!!
        var edit_video = view.findViewById<ImageButton>(R.id.edit_video)!!
        var delete_video = view.findViewById<ImageButton>(R.id.delete_video)!!
    }

    class AssignmentViewHolderLecturer(view: View) : RecyclerView.ViewHolder(view) {
        var assignment_name_lecturer = view.findViewById<TextView>(R.id.assignment_name_lecturer)!!
        var edit_assignment = view.findViewById<ImageButton>(R.id.edit_assignment)!!
        var delete_ass = view.findViewById<ImageButton>(R.id.delete_ass)!!
    }
}