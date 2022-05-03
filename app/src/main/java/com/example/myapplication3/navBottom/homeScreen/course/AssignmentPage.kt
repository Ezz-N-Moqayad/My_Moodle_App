package com.example.myapplication3.navBottom.homeScreen.course

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication3.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AssignmentPage : AppCompatActivity() {

    private lateinit var nameAssignment: TextView
    private lateinit var numberAssignment: TextView
    private lateinit var requiredAssignment: TextView
    private lateinit var uploadDeliveryAssBtn: Button
    private lateinit var pickDeliveryAssFab: FloatingActionButton
    private lateinit var backPageCourseAss: ImageView

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    private var deliveryUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_page)

        nameAssignment = findViewById(R.id.nameAssignment)
        numberAssignment = findViewById(R.id.numberAssignment)
        requiredAssignment = findViewById(R.id.requiredAssignment)
        uploadDeliveryAssBtn = findViewById(R.id.uploadDeliveryAssBtn)
        pickDeliveryAssFab = findViewById(R.id.pickDeliveryAssFab)
        backPageCourseAss = findViewById(R.id.backPageCourseAss)

        auth = Firebase.auth
        database = Firebase.database.reference

        nameAssignment.text = intent.getStringExtra("Name_Assignment").toString()
        numberAssignment.text = intent.getStringExtra("Number_Assignment").toString()
        requiredAssignment.text = intent.getStringExtra("Required_Assignment").toString()

        var idLecturer = ""

        database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
            for (document in dataSnapshot.children) {
                if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                    idLecturer = document.child("id_Lecturer").value.toString()
                }
            }
        }
        backPageCourseAss.setOnClickListener {
            database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
                for (document in dataSnapshot.children) {
                    if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                        intent(Intent(this, CoursePage::class.java))
                        Toast.makeText(this, "Lecturer", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            database.child("Student").get().addOnSuccessListener { dataSnapshot ->
                for (document in dataSnapshot.children) {
                    if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                        intent(Intent(this, CoursePage::class.java))
                        Toast.makeText(this, "Student", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        pickDeliveryAssFab.setOnClickListener {
            deliveryPickIntent()
        }

        uploadDeliveryAssBtn.setOnClickListener {
            when (deliveryUri) {
                null -> {
                    Toast.makeText(this, "Pick the Delivery First", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Handler().postDelayed({
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Add Delivery")
                        builder.setMessage("Do you want to Add the Delivery?")
                        builder.setPositiveButton("Yes") { _, _ ->
                            uploadDeliveryFirebase(idLecturer)
                        }
                        builder.setNegativeButton("No") { d, _ ->
                            d.dismiss()
                            intent(Intent(this, CoursePage::class.java))
                        }
                        builder.create().show()

                    }, 1000)
                }
            }
        }
    }


    private fun uploadDeliveryFirebase(idLecturer: String) {
        showDialog()
        val idDelivery = System.currentTimeMillis()
        val filePathAndName = "Files/file_$idDelivery"
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(deliveryUri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isSuccessful);
            val downloadUri = uriTask.result
            if (uriTask.isSuccessful) {
                addDelivery(
                    idDelivery.toString(),
                    downloadUri.toString(),
                    intent.getStringExtra("id_Assignment").toString(),
                    intent.getStringExtra("Number_Assignment").toString(),
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

    private fun addDelivery(
        id_Delivery: String,
        Uri_Delivery: String,
        id_Assignment: String,
        Number_Assignment: String,
        Number_Course: String,
        idLecturer: String
    ) {
        val delivery = hashMapOf(
            "id_Delivery" to id_Delivery,
            "Uri_Delivery" to Uri_Delivery,
            "Number_Assignment" to Number_Assignment,
            "Number_Course" to Number_Course,
            "idLecturer" to idLecturer
        )
        val idCourse = intent.getStringExtra("id_Course").toString()
        database.child("Lecturer/$idLecturer/Courses/$idCourse/Assignment/$id_Assignment/delivery/$id_Delivery")
            .setValue(delivery)
    }

    private fun deliveryPickIntent() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        deliveryActivityResultLauncher.launch(intent)
    }

    private val deliveryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {
                deliveryUri = result.data!!.data
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Uploading Delivery...")
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