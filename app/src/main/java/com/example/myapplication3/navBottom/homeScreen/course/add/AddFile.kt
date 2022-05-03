package com.example.myapplication3.navBottom.homeScreen.course.add

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication3.R
import com.example.myapplication3.navBottom.homeScreen.course.CoursePage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AddFile : AppCompatActivity() {

    private lateinit var addNameFile: EditText
    private lateinit var uploadFileBtn: Button
    private lateinit var backPageCourseFile: ImageView
    private lateinit var pickFileFab: FloatingActionButton

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    private var pdfUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_file)

        addNameFile = findViewById(R.id.addNameFile)
        pickFileFab = findViewById(R.id.pickFileFab)
        uploadFileBtn = findViewById(R.id.uploadFileBtn)
        backPageCourseFile = findViewById(R.id.backPageCourseFile)

        auth = Firebase.auth
        database = Firebase.database.reference
        val idFile = System.currentTimeMillis()
        var idLecturer = ""

        database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    idLecturer = document.child("id_Lecturer").value.toString()
                }
            }
        }
        backPageCourseFile.setOnClickListener {
            intent(Intent(this, CoursePage::class.java))
        }
        pickFileFab.setOnClickListener {
            pdfPickIntent()
        }

        uploadFileBtn.setOnClickListener {
            val idCourse = intent.getStringExtra("id_Course").toString()
            when {
                addNameFile.text.isEmpty() -> {
                    Toast.makeText(this, "File Name is required", Toast.LENGTH_SHORT).show()
                }
                pdfUri == null -> {
                    Toast.makeText(this, "Pick the File First", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    var nameFile = "null"
                    database.child("Lecturer/$idLecturer/Courses/$idCourse/File").get()
                        .addOnSuccessListener { dataSnapshot ->
                            for (document in dataSnapshot.children) {
                                if (document.child("Name_File").value.toString() == addNameFile.text.toString()) {
                                    nameFile = "nameFile"
                                    Toast.makeText(
                                        this,
                                        "This ${addNameFile.text} is already in use, try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    Handler().postDelayed({
                        when (nameFile) {
                            "nameFile" -> {}
                            else -> {
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("Add File")
                                builder.setMessage("Do you want to Add the File?")
                                builder.setPositiveButton("Yes") { _, _ ->
                                    uploadFileFirebase(idLecturer)
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
    }

    private fun uploadFileFirebase(idLecturer: String) {
        showDialog()
        val idFile = System.currentTimeMillis()
        val filePathAndName = "Files/file_$idFile"
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isSuccessful);
            val downloadUri = uriTask.result
            if (uriTask.isSuccessful) {
                addFile(
                    idFile.toString(),
                    addNameFile.text.toString(),
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

    private fun addFile(
        id_File: String,
        Name_File: String,
        Uri_File: String,
        Number_Course: String,
        idLecturer: String
    ) {
        val file = hashMapOf(
            "id_File" to id_File,
            "Name_File" to Name_File,
            "Uri_File" to Uri_File,
            "Number_Course" to Number_Course,
            "idLecturer" to idLecturer
        )
        val idCourse = intent.getStringExtra("id_Course").toString()
        database.child("Lecturer/$idLecturer/Courses/$idCourse/File/$id_File").setValue(file)
    }

    private fun pdfPickIntent() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }

    private val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {
                pdfUri = result.data!!.data
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Uploading File...")
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