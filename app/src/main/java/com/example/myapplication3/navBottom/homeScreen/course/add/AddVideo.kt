package com.example.myapplication3.navBottom.homeScreen.course.add

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication3.R
import com.example.myapplication3.navBottom.homeScreen.course.CoursePage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AddVideo : AppCompatActivity() {

    private lateinit var addNameVideo: EditText
    private lateinit var uploadVideoBtn: Button
    private lateinit var pickVideoFab: FloatingActionButton
    private lateinit var backPageCourseVideo: ImageView

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    private var videoUri: Uri? = null
    private lateinit var cameraPermissions: Array<String>
    private val VIDEO_PICK_GALLERY_CODE = 100
    private val VIDEO_PICK_CAMERA_CODE = 101
    private val CAMERA_REQUEST_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)

        addNameVideo = findViewById(R.id.addNameVideo)
        uploadVideoBtn = findViewById(R.id.uploadVideoBtn)
        pickVideoFab = findViewById(R.id.pickVideoFab)
        backPageCourseVideo = findViewById(R.id.backPageCourseVideo)

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

        backPageCourseVideo.setOnClickListener {
            intent(Intent(this, CoursePage::class.java))
        }

        cameraPermissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        uploadVideoBtn.setOnClickListener {
            val idCourse = intent.getStringExtra("id_Course").toString()
            when {
                addNameVideo.text.isEmpty() -> {
                    Toast.makeText(this, "Video Name is required", Toast.LENGTH_SHORT).show()
                }
                videoUri == null -> {
                    Toast.makeText(this, "Pick the Video First", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    var nameVideo = "null"
                    database.child("Lecturer/$idLecturer/Courses/$idCourse/Video").get()
                        .addOnSuccessListener { dataSnapshot ->
                            for (document in dataSnapshot.children) {
                                if (document.child("Name_Video").value.toString() == addNameVideo.text.toString()) {
                                    nameVideo = "nameVideo"
                                    Toast.makeText(
                                        this,
                                        "This ${addNameVideo.text} is already in use, try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    Handler().postDelayed({
                        when (nameVideo) {
                            "nameVideo" -> {}
                            else -> {
                                val builder = android.app.AlertDialog.Builder(this)
                                builder.setTitle("Add Video")
                                builder.setMessage("Do you want to Add the Video?")
                                builder.setPositiveButton("Yes") { _, _ ->
                                    uploadVideoFirebase(idLecturer)
                                }
                                builder.setNegativeButton("No") { d, _ ->
                                    d.dismiss()
                                    intent(Intent(this, CoursePage::class.java))
                                }
                                builder.create().show()
                            }
                        }
                    }, 1000)
                }
            }
        }
        pickVideoFab.setOnClickListener {
            videoPickDialog()
        }
    }

    private fun uploadVideoFirebase(idLecturer: String) {
        showDialog()
        val idVideo = System.currentTimeMillis()
        val filePathAndName = "Videos/video_$idVideo"
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(videoUri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isSuccessful);
            val downloadUri = uriTask.result
            if (uriTask.isSuccessful) {
                addVideo(
                    idVideo.toString(),
                    addNameVideo.text.toString(),
                    downloadUri.toString(),
                    intent.getStringExtra("Number_Course").toString(),
                    idLecturer
                )
                hideDialog()
                intent(Intent(this, CoursePage::class.java))
                Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show()
            } else {
                intent(Intent(this, CoursePage::class.java))
                Toast.makeText(this, "Add Failed", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            hideDialog()
            intent(Intent(this, CoursePage::class.java))
            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addVideo(
        id_Video: String,
        Name_Video: String,
        Uri_Video: String,
        Number_Course: String,
        idLecturer: String
    ) {
        val video = hashMapOf(
            "id_Video" to id_Video,
            "Name_Video" to Name_Video,
            "Uri_Video" to Uri_Video,
            "Number_Course" to Number_Course,
            "idLecturer" to idLecturer
        )
        val idCourse = intent.getStringExtra("id_Course").toString()
        database.child("Lecturer/$idLecturer/Courses/$idCourse/Video/$id_Video").setValue(video)
    }

    private fun videoPickDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Pick Video From")
            .setItems(options) { _, i ->
                if (i == 0) {
                    if (!checkCameraPermissions()) {
                        requestCameraPermissions()
                    } else {
                        videoPickCamera()
                    }
                } else {
                    videoPickGallery()
                }
            }
            .show()
    }

    private fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(
            this,
            cameraPermissions,
            CAMERA_REQUEST_CODE
        )
    }

    private fun checkCameraPermissions(): Boolean {
        val result1 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val result2 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return result1 && result2
    }

    private fun videoPickGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "Choose video"),
            VIDEO_PICK_GALLERY_CODE
        )
    }

    private fun videoPickCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST_CODE ->
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted =
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted =
                        grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {
                        videoPickCamera()
                    } else {
                        Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == VIDEO_PICK_CAMERA_CODE) {
                videoUri = data!!.data
            } else if (requestCode == VIDEO_PICK_GALLERY_CODE) {
                videoUri = data!!.data
            }
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Uploading Video...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideDialog() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }

    fun intent(Intent_Page: Intent) {
        Intent_Page.putExtra("id_Course", intent.getStringExtra("id_Course").toString())
        Intent_Page.putExtra("id_Lecturer", intent.getStringExtra("id_Lecturer").toString())
        Intent_Page.putExtra("Name_Course", intent.getStringExtra("Name_Course").toString())
        Intent_Page.putExtra("Number_Course", intent.getStringExtra("Number_Course").toString())
        Intent_Page.putExtra("Lecturer", intent.getStringExtra("Lecturer").toString())
        startActivity(Intent_Page)
    }
}