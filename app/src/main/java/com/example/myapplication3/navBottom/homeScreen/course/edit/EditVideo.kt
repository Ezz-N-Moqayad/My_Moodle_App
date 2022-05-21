package com.example.myapplication3.navBottom.homeScreen.course.edit

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
import androidx.core.net.toUri
import com.example.myapplication3.R
import com.example.myapplication3.navBottom.homeScreen.course.CoursePageLecturer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class EditVideo : AppCompatActivity() {

    private lateinit var editNameVideo: EditText
    private lateinit var editVideoBtn: Button
    private lateinit var editVideoFab: FloatingActionButton
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
        setContentView(R.layout.activity_edit_video)

        editNameVideo = findViewById(R.id.editNameVideo)
        editVideoBtn = findViewById(R.id.editVideoBtn)
        editVideoFab = findViewById(R.id.editVideoFab)
        backPageCourseVideo = findViewById(R.id.backPageCourseVideo)

        auth = Firebase.auth
        database = Firebase.database.reference
        var idLecturer = ""

        editNameVideo.setText(intent.getStringExtra("Name_Video").toString())
        videoUri = intent.getStringExtra("Uri_Video")!!.toUri()

        database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    idLecturer = document.child("id_Lecturer").value.toString()
                }
            }
        }

        backPageCourseVideo.setOnClickListener {
            intent(Intent(this, CoursePageLecturer::class.java))
        }

        cameraPermissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        editVideoBtn.setOnClickListener {
            val idCourse = intent.getStringExtra("id_Course").toString()
            when {
                editNameVideo.text.isEmpty() -> {
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
                                if (document.child("Name_Video").value.toString() == editNameVideo.text.toString()) {
                                    nameVideo = "nameVideo"
                                    Toast.makeText(
                                        this,
                                        "This ${editNameVideo.text} is already in use, try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    Handler().postDelayed({
                        when (nameVideo) {
                            "nameVideo" -> {}
                            else -> {
                                val idVideo = intent.getStringExtra("id_Video").toString()
                                val builder = android.app.AlertDialog.Builder(this)
                                builder.setTitle("Edit Video")
                                builder.setMessage("Do you want to Edit the Video?")
                                builder.setPositiveButton("Yes") { _, _ ->
                                    editVideo(
                                        idVideo.toString(),
                                        editNameVideo.text.toString(),
                                        idLecturer
                                    )
                                    intent(Intent(this, CoursePageLecturer::class.java))
                                    Toast.makeText(this, "Edit Successfully", Toast.LENGTH_SHORT).show()
                                }
                                builder.setNegativeButton("No") { d, _ ->
                                    d.dismiss()
                                    intent(Intent(this, CoursePageLecturer::class.java))
                                }
                                builder.create().show()
                            }
                        }
                    }, 500)
                }
            }
        }
        editVideoFab.setOnClickListener {
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
                hideDialog()
            } else {
                hideDialog()
                intent(Intent(this, CoursePageLecturer::class.java))
                Toast.makeText(this, "Edit Failed", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            hideDialog()
            intent(Intent(this, CoursePageLecturer::class.java))
            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editVideo(
        id_Video: String,
        Name_Video: String,
        id_Lecturer: String
    ) {
        val video = mapOf(
            "Name_Video" to Name_Video,
        )
        val idCourse = intent.getStringExtra("id_Course").toString()
        database.child("Courses/$idCourse/Video/$id_Video").updateChildren(video)
        database.child("Lecturer/$id_Lecturer/Courses/$idCourse/Video/$id_Video")
            .updateChildren(video)
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