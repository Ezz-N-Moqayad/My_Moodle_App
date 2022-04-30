package com.example.myapplication3.navBottom.homeScreen.course

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication3.R
import com.example.myapplication3.logIn.MainActivity
import com.example.myapplication3.modle.AssignmentCourse
import com.example.myapplication3.modle.FileCourse
import com.example.myapplication3.modle.VideoCourse
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CoursePage : AppCompatActivity() {

    private lateinit var rvFile: RecyclerView
    private lateinit var rvAss: RecyclerView
    private lateinit var rvVideo: RecyclerView
    private lateinit var textViewCou: TextView
    private lateinit var textView2Cou: TextView
    private lateinit var textView3Cou: TextView
    private lateinit var backHome: ImageView
    private lateinit var btnPopup: ImageView

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference;
    private var adapterFile: FirebaseRecyclerAdapter<FileCourse, ViewHolder.FileViewHolder>? = null
    private var adapterAss: FirebaseRecyclerAdapter<AssignmentCourse, ViewHolder.AssignmentViewHolder>? =
        null
    private var adapterVideo: FirebaseRecyclerAdapter<VideoCourse, ViewHolder.VideoViewHolder>? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_page)

        textViewCou = findViewById(R.id.textViewCou)
        textView2Cou = findViewById(R.id.textView2Cou)
        textView3Cou = findViewById(R.id.textView3Cou)
        backHome = findViewById(R.id.backHome)
        btnPopup = findViewById(R.id.btnPopup)

        auth = Firebase.auth
        database = Firebase.database.reference

        textView3Cou.text = intent.getStringExtra("Name_Course").toString()
        textView2Cou.text = intent.getStringExtra("Number_Course").toString()
        textViewCou.text = intent.getStringExtra("Lecturer").toString()

        getAllFile()
        getAllAssignment()
        getAllVideo()

        backHome.setOnClickListener {
            database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
                for (document in dataSnapshot.children) {
                    if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(this, "Lecturer", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            database.child("Student").get().addOnSuccessListener { dataSnapshot ->
                for (document in dataSnapshot.children) {
                    if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(this, "Student", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnPopup.setOnClickListener {
            database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
                for (document in dataSnapshot.children) {
                    if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                        val popup = PopupMenu(this, btnPopup)
                        popup.menuInflater.inflate(R.menu.toolbar_coures, popup.menu)
                        popup.setOnMenuItemClickListener { x ->
                            when (x.itemId) {
                                R.id.addsFile -> intent(Intent(this, AddFile::class.java))
                                R.id.addsAssignment -> intent(
                                    Intent(this, AddAssignment::class.java)
                                )
                                R.id.addsVideo -> intent(Intent(this, AddVideo::class.java))
                            }
                            true
                        }
                        popup.show()
                    }
                }
            }
            database.child("Student").get().addOnSuccessListener { dataSnapshot ->
                for (document in dataSnapshot.children) {
                    if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                        Toast.makeText(
                            this, "It's not for you, only for Lecturer",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun getAllFile() {
        rvFile = findViewById(R.id.rvFile)

        val idCourse = intent.getStringExtra("id_Course").toString()
        val idLecturer = intent.getStringExtra("id_Lecturer").toString()

        val query = database.child("Lecturer/$idLecturer/Courses/$idCourse/File")

        val options =
            FirebaseRecyclerOptions.Builder<FileCourse>().setQuery(query, FileCourse::class.java)
                .build()

        adapterFile =
            object : FirebaseRecyclerAdapter<FileCourse, ViewHolder.FileViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ViewHolder.FileViewHolder {
                    val view = LayoutInflater.from(this@CoursePage)
                        .inflate(R.layout.file_course_item, parent, false)
                    return ViewHolder.FileViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: ViewHolder.FileViewHolder,
                    position: Int,
                    model: FileCourse
                ) {
                    holder.file_course_name.text = model.Name_File
                    holder.file_course_number.text = model.Number_File

                }
            }
        rvFile.layoutManager = LinearLayoutManager(this)
        rvFile.adapter = adapterFile
    }

    private fun getAllAssignment() {
        rvAss = findViewById(R.id.rvAss)

        val idCourse = intent.getStringExtra("id_Course").toString()
        val idLecturer = intent.getStringExtra("id_Lecturer").toString()

        val query = database.child("Lecturer/$idLecturer/Courses/$idCourse/Assignment")

        val options =
            FirebaseRecyclerOptions.Builder<AssignmentCourse>()
                .setQuery(query, AssignmentCourse::class.java)
                .build()

        adapterAss =
            object :
                FirebaseRecyclerAdapter<AssignmentCourse, ViewHolder.AssignmentViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ViewHolder.AssignmentViewHolder {
                    val view = LayoutInflater.from(this@CoursePage)
                        .inflate(R.layout.assignment_course_item, parent, false)
                    return ViewHolder.AssignmentViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: ViewHolder.AssignmentViewHolder,
                    position: Int,
                    model: AssignmentCourse
                ) {
                    holder.assignment_course_name.text = model.Name_Assignment
                }
            }
        rvAss.layoutManager = LinearLayoutManager(this)
        rvAss.adapter = adapterAss
    }

    private fun getAllVideo() {
        rvVideo = findViewById(R.id.rvVideo)
        val idCourse = intent.getStringExtra("id_Course").toString()
        val idLecturer = intent.getStringExtra("id_Lecturer").toString()

        val query = database.child("Lecturer/$idLecturer/Courses/$idCourse/Video")

        val options =
            FirebaseRecyclerOptions.Builder<VideoCourse>().setQuery(query, VideoCourse::class.java)
                .build()

        adapterVideo =
            object : FirebaseRecyclerAdapter<VideoCourse, ViewHolder.VideoViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ViewHolder.VideoViewHolder {
                    val view = LayoutInflater.from(this@CoursePage)
                        .inflate(R.layout.video_course_item, parent, false)
                    return ViewHolder.VideoViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: ViewHolder.VideoViewHolder,
                    position: Int,
                    model: VideoCourse
                ) {
                    holder.video_course_name.text = model.Name_Video

                }
            }
        rvVideo.layoutManager = LinearLayoutManager(this)
        rvVideo.adapter = adapterVideo
    }

    fun intent(Intent_Page: Intent) {
        Intent_Page.putExtra("id_Course", intent.getStringExtra("id_Course").toString())
        Intent_Page.putExtra("id_Lecturer", intent.getStringExtra("id_Lecturer").toString())
        Intent_Page.putExtra("Name_Course", intent.getStringExtra("Name_Course").toString())
        Intent_Page.putExtra("Number_Course", intent.getStringExtra("Number_Course").toString())
        Intent_Page.putExtra("Lecturer", intent.getStringExtra("Lecturer").toString())
        startActivity(Intent_Page)
    }

    override fun onStart() {
        super.onStart()
        adapterFile!!.startListening()
        adapterAss!!.startListening()
        adapterVideo!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterFile!!.stopListening()
        adapterAss!!.stopListening()
        adapterVideo!!.stopListening()
    }
}