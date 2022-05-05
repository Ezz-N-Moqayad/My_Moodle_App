package com.example.myapplication3.navBottom.homeScreen.course

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication3.R
import com.example.myapplication3.logIn.MainActivity
import com.example.myapplication3.modle.AssignmentCourse
import com.example.myapplication3.modle.FileCourse
import com.example.myapplication3.modle.VideoCourse
import com.example.myapplication3.navBottom.homeScreen.course.add.AddAssignment
import com.example.myapplication3.navBottom.homeScreen.course.add.AddFile
import com.example.myapplication3.navBottom.homeScreen.course.add.AddVideo
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream

class CoursePage : AppCompatActivity() {

    private lateinit var rvFile: RecyclerView
    private lateinit var rvAss: RecyclerView
    private lateinit var rvVideo: RecyclerView
    private lateinit var nameCourseCou: TextView
    private lateinit var numberCourseCou: TextView
    private lateinit var lecturerCou: TextView
    private lateinit var backHome: ImageView
    private lateinit var btnPopup: ImageView

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    lateinit var adapterFile: FirebaseRecyclerAdapter<FileCourse, ViewHolder.FileViewHolder>
    lateinit var adapterAss: FirebaseRecyclerAdapter<AssignmentCourse, ViewHolder.AssignmentViewHolder>
    lateinit var adapterVideo: FirebaseRecyclerAdapter<VideoCourse, ViewHolder.VideoViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_page)

        nameCourseCou = findViewById(R.id.nameCourseCou)
        numberCourseCou = findViewById(R.id.numberCourseCou)
        lecturerCou = findViewById(R.id.lecturerCou)
        backHome = findViewById(R.id.backHome)
        btnPopup = findViewById(R.id.btnPopup)

        auth = Firebase.auth
        database = Firebase.database.reference

        nameCourseCou.text = intent.getStringExtra("Name_Course").toString()
        numberCourseCou.text = intent.getStringExtra("Number_Course").toString()
        lecturerCou.text = intent.getStringExtra("Lecturer").toString()
        val idCourse = intent.getStringExtra("id_Course").toString()

        getAllFile()
        getAllAssignment()
        getAllVideo()

        backHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        var idLecturer = ""
        database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("id_Lecturer").value.toString() ==
                    intent.getStringExtra("id_Lecturer").toString()
                ) {
                    idLecturer = document.child("id_Lecturer").value.toString()
                }
            }
        }
        var idStudent = ""
        database.child("Student").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                val idStudent2 = document.child("id_Student").value.toString()
                database.child("Student/$idStudent2/Courses").get()
                    .addOnSuccessListener { dataSnapshot2 ->
                        for (document2 in dataSnapshot2.children) {
                            if (document2.child("id_Course").value.toString() ==
                                intent.getStringExtra("id_Course").toString()
                            ) {
                                idStudent = document.child("id_Student").value.toString()
                            }
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
                                R.id.deleteCourse -> deleteCourse(idCourse, idLecturer, idStudent)
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

    private fun deleteCourse(idCourse: String, idLecturer: String, idStudent: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Course")
        builder.setMessage("Do you want to Delete the Course?")
        builder.setPositiveButton("Yes") { _, _ ->
            database.child("Lecturer/$idLecturer/Courses/$idCourse").removeValue()
            database.child("Student/$idStudent/Courses/$idCourse").removeValue()
            database.child("Courses/$idCourse").removeValue()

            startActivity(Intent(this, MainActivity::class.java))
        }
        builder.setNegativeButton("No") { d, _ ->
            d.dismiss()
        }
        builder.create().show()
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
                    holder.file_course_layout.setOnClickListener {
                        if (ContextCompat.checkSelfPermission(
                                this@CoursePage, Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            downloadFile(model.Name_File, model.Uri_File)
                            Log.d("ezz", "onCreate: STORAGE PERMISSION is already granted")
                        } else {
                            Log.d(
                                "ezz",
                                "onCreate: STORAGE PERMISSION was not granted, LETS request it"
                            )
                            val requestStoragePermissionLauncher =
                                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                                    if (isGranted) {
                                        downloadFile(model.Name_File, model.Uri_File)
                                        Log.d(
                                            "ezz",
                                            "onCreate: STORAGE PERMISSION is already granted"
                                        )
                                    } else {
                                        Log.d("ezz", "onCreate: STORAGE PERMISSION is denied")
                                        Toast.makeText(
                                            this@CoursePage,
                                            "PERMISSION denied",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }
                }
            }
        rvFile.layoutManager = LinearLayoutManager(this)
        rvFile.adapter = adapterFile
    }

    object Constants {
        const val MAX_BYTES_PDF: Long = 50000000//50MB
    }

    private fun downloadFile(FileName: String, FileUrl: String) {
        showDialog()
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(FileUrl)
        storageReference.getBytes(Constants.MAX_BYTES_PDF).addOnSuccessListener { bytes ->
            Log.d("ezz", " downloadFile: File downloaded. ..")
            saveToDownloadsFolder(bytes, FileName)
        }.addOnFailureListener { e ->
            hideDialog()
            Log.d("ezz", "downloadFile: Failed to download File due to ${e.message}")
            Toast.makeText(this, "Failed to download book due to ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun saveToDownloadsFolder(bytes: ByteArray?, FileName: String) {
        Log.d("ezz", "saveToDownloadsFolder: saving download file")
        try {
            val downloadsFolder =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsFolder.mkdirs()
            val filePath = downloadsFolder.path + "/" + "$FileName.pdf"

            val out = FileOutputStream(filePath)
            out.write(bytes)
            out.close()
            Toast.makeText(this, "Saved to Downloads Folder", Toast.LENGTH_SHORT).show()
            hideDialog()
        } catch (e: Exception) {
            hideDialog()
            Log.d("ezz", "saveToDownloadsFolder: failed to save due to ${e.message}")
            Toast.makeText(this, "Failed to save due to ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getAllAssignment() {
        rvAss = findViewById(R.id.rvAss)
        val idCourse = intent.getStringExtra("id_Course").toString()
        val idLecturer = intent.getStringExtra("id_Lecturer").toString()
        val query = database.child("Lecturer/$idLecturer/Courses/$idCourse/Assignment")
        val options = FirebaseRecyclerOptions.Builder<AssignmentCourse>()
            .setQuery(query, AssignmentCourse::class.java).build()
        adapterAss = object :
            FirebaseRecyclerAdapter<AssignmentCourse, ViewHolder.AssignmentViewHolder>(
                options
            ) {
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
                holder.assignment_course_number.text = model.Number_Assignment
                holder.assignment_course_layout.setOnClickListener {
                    intentAss(
                        model.id_Assignment,
                        model.Name_Assignment,
                        model.Number_Assignment,
                        model.Required_Assignment
                    )
                }
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
            FirebaseRecyclerOptions.Builder<VideoCourse>()
                .setQuery(query, VideoCourse::class.java)
                .build()
        adapterVideo = object :
            FirebaseRecyclerAdapter<VideoCourse, ViewHolder.VideoViewHolder>(options) {
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
                holder.video_course_layout.setOnClickListener {
                    intentVideo(
                        model.id_Video,
                        model.Name_Video,
                        model.Uri_Video
                    )
                }
            }
        }
        rvVideo.layoutManager = LinearLayoutManager(this)
        rvVideo.adapter = adapterVideo
    }

    fun intentAss(
        id_Assignment: String,
        Name_Assignment: String,
        Number_Assignment: String,
        Required_Assignment: String
    ) {
        val i = Intent(this, AssignmentPage::class.java)
        i.putExtra("id_Assignment", id_Assignment)
        i.putExtra("Name_Assignment", Name_Assignment)
        i.putExtra("Number_Assignment", Number_Assignment)
        i.putExtra("Required_Assignment", Required_Assignment)
        i.putExtra("id_Lecturer", intent.getStringExtra("id_Lecturer").toString())
        i.putExtra("Lecturer", intent.getStringExtra("Lecturer").toString())
        i.putExtra("id_Course", intent.getStringExtra("id_Course").toString())
        i.putExtra("Name_Course", intent.getStringExtra("Name_Course").toString())
        i.putExtra("Number_Course", intent.getStringExtra("Number_Course").toString())
        startActivity(i)
    }

    fun intentVideo(
        id_Video: String,
        Name_Video: String,
        Uri_Video: String
    ) {
        val i = Intent(this, VideoActivity::class.java)
        i.putExtra("id_Video", id_Video)
        i.putExtra("Name_Video", Name_Video)
        i.putExtra("Uri_Video", Uri_Video)
        i.putExtra("id_Lecturer", intent.getStringExtra("id_Lecturer").toString())
        i.putExtra("Lecturer", intent.getStringExtra("Lecturer").toString())
        i.putExtra("id_Course", intent.getStringExtra("id_Course").toString())
        i.putExtra("Name_Course", intent.getStringExtra("Name_Course").toString())
        i.putExtra("Number_Course", intent.getStringExtra("Number_Course").toString())
        startActivity(i)
    }

    fun intent(Intent_Page: Intent) {
        Intent_Page.putExtra("id_Course", intent.getStringExtra("id_Course").toString())
        Intent_Page.putExtra("Lecturer", intent.getStringExtra("Lecturer").toString())
        Intent_Page.putExtra("id_Lecturer", intent.getStringExtra("id_Lecturer").toString())
        Intent_Page.putExtra("Name_Course", intent.getStringExtra("Name_Course").toString())
        Intent_Page.putExtra("Number_Course", intent.getStringExtra("Number_Course").toString())
        startActivity(Intent_Page)
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Downloading File...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideDialog() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }

    override fun onStart() {
        super.onStart()
        adapterFile.startListening()
        adapterAss.startListening()
        adapterVideo.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterFile.stopListening()
        adapterAss.stopListening()
        adapterVideo.stopListening()
    }
}