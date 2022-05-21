package com.example.myapplication3.navBottom.homeScreen.course

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
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
import com.example.myapplication3.navBottom.homeScreen.course.edit.EditAssignment
import com.example.myapplication3.navBottom.homeScreen.course.edit.EditFile
import com.example.myapplication3.navBottom.homeScreen.course.edit.EditVideo
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream

class CoursePageLecturer : AppCompatActivity() {

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

    lateinit var adapterFileLecturer: FirebaseRecyclerAdapter<FileCourse, ViewHolder.FileViewHolderLecturer>
    lateinit var adapterAssLecturer: FirebaseRecyclerAdapter<AssignmentCourse, ViewHolder.AssignmentViewHolderLecturer>
    lateinit var adapterVideoLecturer: FirebaseRecyclerAdapter<VideoCourse, ViewHolder.VideoViewHolderLecturer>

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
        val idLecturer = intent.getStringExtra("id_Lecturer").toString()

        getAllFile(idCourse, idLecturer)
        getAllAssignment(idCourse, idLecturer)
        getAllVideo(idCourse, idLecturer)

        backHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
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

    private fun getAllFile(idCourse: String, idLecturer: String) {
        rvFile = findViewById(R.id.rvFile)
        val query = database.child("Lecturer/$idLecturer/Courses/$idCourse/File")
        val options =
            FirebaseRecyclerOptions.Builder<FileCourse>().setQuery(query, FileCourse::class.java)
                .build()
        adapterFileLecturer =
            object :
                FirebaseRecyclerAdapter<FileCourse, ViewHolder.FileViewHolderLecturer>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ViewHolder.FileViewHolderLecturer {
                    val view = LayoutInflater.from(this@CoursePageLecturer)
                        .inflate(R.layout.file_course_lucturer_item, parent, false)
                    return ViewHolder.FileViewHolderLecturer(view)
                }

                override fun onBindViewHolder(
                    holder: ViewHolder.FileViewHolderLecturer,
                    position: Int,
                    model: FileCourse
                ) {
                    holder.file_name_lecturer.text = model.Name_File
                    holder.file_name_lecturer.setOnClickListener {
                        if (ContextCompat.checkSelfPermission(
                                this@CoursePageLecturer, Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            downloadFile(model.Name_File, model.Uri_File)
                        } else {
                            val requestStoragePermissionLauncher =
                                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                                    if (isGranted) {
                                        downloadFile(model.Name_File, model.Uri_File)
                                    } else {
                                        Toast.makeText(
                                            this@CoursePageLecturer,
                                            "PERMISSION denied",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }
                    holder.edit_file.setOnClickListener {
                        intentFileLecturer(
                            model.id_File,
                            model.Name_File,
                            model.Uri_File
                        )
                    }
                    holder.delete_file.setOnClickListener {
                        deleteFile(
                            idCourse,
                            idLecturer,
                            model.id_File
                        )
                    }
                }
            }
        rvFile.layoutManager = LinearLayoutManager(this)
        rvFile.adapter = adapterFileLecturer
    }

    object Constants {
        const val MAX_BYTES_PDF: Long = 50000000//50MB
    }

    private fun downloadFile(FileName: String, FileUrl: String) {
        showDialog()
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(FileUrl)
        storageReference.getBytes(Constants.MAX_BYTES_PDF).addOnSuccessListener { bytes ->
            saveToDownloadsFolder(bytes, FileName)
        }.addOnFailureListener { e ->
            hideDialog()
            Toast.makeText(this, "Failed to download book due to ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun saveToDownloadsFolder(bytes: ByteArray?, FileName: String) {
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
            Toast.makeText(this, "Failed to save due to ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getAllAssignment(idCourse: String, idLecturer: String) {
        rvAss = findViewById(R.id.rvAss)
        val query = database.child("Lecturer/$idLecturer/Courses/$idCourse/Assignment")
        val options = FirebaseRecyclerOptions.Builder<AssignmentCourse>()
            .setQuery(query, AssignmentCourse::class.java).build()
        adapterAssLecturer = object :
            FirebaseRecyclerAdapter<AssignmentCourse, ViewHolder.AssignmentViewHolderLecturer>(
                options
            ) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ViewHolder.AssignmentViewHolderLecturer {
                val view = LayoutInflater.from(this@CoursePageLecturer)
                    .inflate(R.layout.assignment_course_lucturer_item, parent, false)
                return ViewHolder.AssignmentViewHolderLecturer(view)
            }

            override fun onBindViewHolder(
                holder: ViewHolder.AssignmentViewHolderLecturer,
                position: Int,
                model: AssignmentCourse
            ) {
                holder.assignment_name_lecturer.text = model.Name_Assignment
                holder.assignment_name_lecturer.setOnClickListener {
                    intentAss(
                        model.id_Assignment,
                        model.Name_Assignment,
                        model.Required_Assignment
                    )
                }
                holder.edit_assignment.setOnClickListener {
                    intentAssLecturer(
                        model.id_Assignment,
                        model.Name_Assignment,
                        model.Required_Assignment
                    )
                }
                holder.delete_ass.setOnClickListener {
                    deleteAssignment(
                        idCourse,
                        idLecturer,
                        model.id_Assignment
                    )
                }
            }
        }
        rvAss.layoutManager = LinearLayoutManager(this)
        rvAss.adapter = adapterAssLecturer
    }

    private fun getAllVideo(idCourse: String, idLecturer: String) {
        rvVideo = findViewById(R.id.rvVideo)
        val query = database.child("Lecturer/$idLecturer/Courses/$idCourse/Video")
        val options =
            FirebaseRecyclerOptions.Builder<VideoCourse>()
                .setQuery(query, VideoCourse::class.java)
                .build()
        adapterVideoLecturer = object :
            FirebaseRecyclerAdapter<VideoCourse, ViewHolder.VideoViewHolderLecturer>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ViewHolder.VideoViewHolderLecturer {
                val view = LayoutInflater.from(this@CoursePageLecturer)
                    .inflate(R.layout.video_course_lucturer_item, parent, false)
                return ViewHolder.VideoViewHolderLecturer(view)
            }

            override fun onBindViewHolder(
                holder: ViewHolder.VideoViewHolderLecturer,
                position: Int,
                model: VideoCourse
            ) {
                holder.video_name_lecturer.text = model.Name_Video
                holder.video_name_lecturer.setOnClickListener {
                    intentVideo(
                        model.id_Video,
                        model.Name_Video,
                        model.Uri_Video
                    )
                }
                holder.edit_video.setOnClickListener {
                    intentVideoLecturer(
                        model.id_Video,
                        model.Name_Video,
                        model.Uri_Video
                    )
                }
                holder.delete_video.setOnClickListener {
                    deleteVideo(
                        idCourse,
                        idLecturer,
                        model.id_Video
                    )
                }
            }
        }
        rvVideo.layoutManager = LinearLayoutManager(this)
        rvVideo.adapter = adapterVideoLecturer
    }


    private fun deleteFile(idCourse: String, idLecturer: String, id_File: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete File")
        builder.setMessage("Do you want to Delete the File?")
        builder.setPositiveButton("Yes") { _, _ ->
            database.child("Courses/$idCourse/File/$id_File").removeValue()
            database.child("Lecturer/$idLecturer/Courses/$idCourse/File/$id_File").removeValue()
        }
        builder.setNegativeButton("No") { d, _ ->
            d.dismiss()
        }
        builder.create().show()
    }

    private fun deleteVideo(idCourse: String, idLecturer: String, id_Video: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Video")
        builder.setMessage("Do you want to Delete the Video?")
        builder.setPositiveButton("Yes") { _, _ ->
            database.child("Courses/$idCourse/Video/$id_Video").removeValue()
            database.child("Lecturer/$idLecturer/Courses/$idCourse/Video/$id_Video").removeValue()
        }
        builder.setNegativeButton("No") { d, _ ->
            d.dismiss()
        }
        builder.create().show()
    }

    private fun deleteAssignment(idCourse: String, idLecturer: String, id_Assignment: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Assignment")
        builder.setMessage("Do you want to Delete the Assignment?")
        builder.setPositiveButton("Yes") { _, _ ->
            database.child("Courses/$idCourse/Assignment/$id_Assignment").removeValue()
            database.child("Lecturer/$idLecturer/Courses/$idCourse/Assignment/$id_Assignment")
                .removeValue()
        }
        builder.setNegativeButton("No") { d, _ ->
            d.dismiss()
        }
        builder.create().show()
    }

    fun intentAss(
        id_Assignment: String,
        Name_Assignment: String,
        Required_Assignment: String
    ) {
        val i = Intent(this, AssignmentPage::class.java)
        i.putExtra("id_Assignment", id_Assignment)
        i.putExtra("Name_Assignment", Name_Assignment)
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

    fun intentAssLecturer(
        id_Assignment: String,
        Name_Assignment: String,
        Required_Assignment: String
    ) {
        val i = Intent(this, EditAssignment::class.java)
        i.putExtra("id_Assignment", id_Assignment)
        i.putExtra("Name_Assignment", Name_Assignment)
        i.putExtra("Required_Assignment", Required_Assignment)
        i.putExtra("id_Lecturer", intent.getStringExtra("id_Lecturer").toString())
        i.putExtra("Lecturer", intent.getStringExtra("Lecturer").toString())
        i.putExtra("id_Course", intent.getStringExtra("id_Course").toString())
        i.putExtra("Name_Course", intent.getStringExtra("Name_Course").toString())
        i.putExtra("Number_Course", intent.getStringExtra("Number_Course").toString())
        startActivity(i)
    }

    fun intentFileLecturer(
        id_File: String,
        Name_File: String,
        Uri_File: String
    ) {
        val i = Intent(this, EditFile::class.java)
        i.putExtra("id_File", id_File)
        i.putExtra("Name_File", Name_File)
        i.putExtra("Uri_File", Uri_File)
        i.putExtra("id_Lecturer", intent.getStringExtra("id_Lecturer").toString())
        i.putExtra("Lecturer", intent.getStringExtra("Lecturer").toString())
        i.putExtra("id_Course", intent.getStringExtra("id_Course").toString())
        i.putExtra("Name_Course", intent.getStringExtra("Name_Course").toString())
        i.putExtra("Number_Course", intent.getStringExtra("Number_Course").toString())
        startActivity(i)
    }

    fun intentVideoLecturer(
        id_Video: String,
        Name_Video: String,
        Uri_Video: String
    ) {
        val i = Intent(this, EditVideo::class.java)
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
        adapterFileLecturer.startListening()
        adapterAssLecturer.startListening()
        adapterVideoLecturer.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterFileLecturer.stopListening()
        adapterAssLecturer.stopListening()
        adapterVideoLecturer.stopListening()
    }
}