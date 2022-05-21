package com.example.myapplication3.navBottom.homeScreen.course.edit

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

class EditFile : AppCompatActivity() {

    private lateinit var editNameFile: EditText
    private lateinit var editFileBtn: Button
    private lateinit var editFileFab: FloatingActionButton
    private lateinit var backPageCourseFile: ImageView

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    private var pdfUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_file)

        editNameFile = findViewById(R.id.editNameFile)
        editFileFab = findViewById(R.id.editFileFab)
        editFileBtn = findViewById(R.id.editFileBtn)
        backPageCourseFile = findViewById(R.id.backPageCourseFile)

        editNameFile.setText(intent.getStringExtra("Name_File").toString())
        pdfUri = intent.getStringExtra("Uri_File")!!.toUri()

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
        backPageCourseFile.setOnClickListener {
            intent(Intent(this, CoursePageLecturer::class.java))
        }
        editFileFab.setOnClickListener {
            pdfPickIntent()
        }

        editFileBtn.setOnClickListener {
            val idCourse = intent.getStringExtra("id_Course").toString()
            when {
                editNameFile.text.isEmpty() -> {
                    Toast.makeText(this, "File Name is required", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    var nameFile = "null"
                    database.child("Lecturer/$idLecturer/Courses/$idCourse/File").get()
                        .addOnSuccessListener { dataSnapshot ->
                            for (document in dataSnapshot.children) {
                                if (document.child("Name_File").value.toString() == editNameFile.text.toString()) {
                                    nameFile = "nameFile"
                                    Toast.makeText(
                                        this,
                                        "This ${editNameFile.text} is already in use, try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    Handler().postDelayed({
                        when (nameFile) {
                            "nameFile" -> {}
                            else -> {
                                val idFile = intent.getStringExtra("id_File").toString()
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("Edit File")
                                builder.setMessage("Do you want to Edit the File?")
                                builder.setPositiveButton("Yes") { _, _ ->
                                    editFile(
                                        idFile,
                                        editNameFile.text.toString(),
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
    }

    private fun uploadFileFirebase() {
        showDialog()
        val idFile = intent.getStringExtra("id_File").toString()
        val filePathAndName = "Files/file_$idFile"
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!).addOnSuccessListener { taskSnapshot ->
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

    private fun editFile(
        id_File: String,
        Name_File: String,
        id_Lecturer: String
    ) {
        val file = mapOf(
            "Name_File" to Name_File
        )
        val idCourse = intent.getStringExtra("id_Course").toString()
        database.child("Courses/$idCourse/File/$id_File").updateChildren(file)
        database.child("Lecturer/$id_Lecturer/Courses/$idCourse/File/$id_File").updateChildren(file)
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
        progressDialog.setMessage("Edit File...")
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