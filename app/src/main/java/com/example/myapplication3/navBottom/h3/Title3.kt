package com.example.myapplication3.navBottom.h3

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myapplication3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class Title3 : Fragment() {

    private lateinit var et_fNamePro: EditText
    private lateinit var et_mNamePro: EditText
    private lateinit var et_lNamePro: EditText
    private lateinit var et_birth_datePro: EditText
    private lateinit var et_addressPro: EditText
    private lateinit var et_emailPro: EditText
    private lateinit var et_mobilePro: EditText
    private lateinit var editPro: TextView

    var db: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_title3, container, false)

        et_fNamePro = view.findViewById(R.id.et_fNamePro)
        et_mNamePro = view.findViewById(R.id.et_mNamePro)
        et_lNamePro = view.findViewById(R.id.et_lNamePro)
        et_birth_datePro = view.findViewById(R.id.et_birth_datePro)
        et_addressPro = view.findViewById(R.id.et_addressPro)
        et_emailPro = view.findViewById(R.id.et_emailPro)
        et_mobilePro = view.findViewById(R.id.et_mobilePro)
        editPro = view.findViewById(R.id.editPro)

        db = Firebase.firestore
        auth = Firebase.auth

        getProfileData()

        et_birth_datePro.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH)
            val year = currentDate.get(Calendar.YEAR)
            val picker = context?.let { it1 ->
                DatePickerDialog(
                    it1, { _, y, m, d ->
                        et_birth_datePro.setText("$y / ${m + 1} / $d")
                    }, year, month, day
                )
            }
            picker!!.show()
        }

        disableEdit()
        editPro.setOnClickListener {
            if (editPro.text.toString() == " Edit Profile ") {
                editPro.text = "Save"
                editPro.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_check,
                    0,
                    0,
                    0
                )
                enableEdit()
            } else {
                editPro.text = " Edit Profile "
                editPro.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_save,
                    0,
                    0,
                    0
                )
                disableEdit()
                editUser()
            }
        }

        return view
    }

    fun getProfileData() {
        db!!.collection("Student").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    if (document.get("Email") == auth.currentUser!!.email) {
                        et_fNamePro.setText(document.get("First_Name").toString())
                        et_mNamePro.setText(document.get("Middle_Name").toString())
                        et_lNamePro.setText(document.get("Family_Name").toString())
                        et_emailPro.setText(document.get("Email").toString())
                        et_birth_datePro.setText(document.get("Birth_Date").toString())
                        et_addressPro.setText(document.get("Address").toString())
                        et_mobilePro.setText(document.get("Mobile").toString())
                    }
                }
            }
        db!!.collection("Lecturer").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    if (document.get("Email") == auth.currentUser!!.email) {
                        et_fNamePro.setText(document.get("First_Name").toString())
                        et_mNamePro.setText(document.get("Middle_Name").toString())
                        et_lNamePro.setText(document.get("Family_Name").toString())
                        et_emailPro.setText(document.get("Email").toString())
                        et_birth_datePro.setText(document.get("Birth_Date").toString())
                        et_addressPro.setText(document.get("Address").toString())
                        et_mobilePro.setText(document.get("Mobile").toString())
                    }
                }
            }
    }

    private fun editUser() {
        db!!.collection("Student").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    if (document.get("Email") == auth.currentUser!!.email) {
                        db!!.collection("Student").document(document.id)
                            .update("First_Name", et_fNamePro.text.toString())
                        db!!.collection("Student").document(document.id)
                            .update("Middle_Name", et_mNamePro.text.toString())
                        db!!.collection("Student").document(document.id)
                            .update("Family_Name", et_lNamePro.text.toString())
                        db!!.collection("Student").document(document.id)
                            .update("Birth_Date", et_birth_datePro.text.toString())
                        db!!.collection("Student").document(document.id)
                            .update("Address", et_addressPro.text.toString())
                        db!!.collection("Student").document(document.id)
                            .update("Mobile", et_mobilePro.text.toString())
                    }
                }
            }

        db!!.collection("Lecturer").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    if (document.get("Email") == auth.currentUser!!.email) {
                        db!!.collection("Lecturer").document(document.id)
                            .update("First_Name", et_fNamePro.text.toString())
                        db!!.collection("Lecturer").document(document.id)
                            .update("Middle_Name", et_mNamePro.text.toString())
                        db!!.collection("Lecturer").document(document.id)
                            .update("Family_Name", et_lNamePro.text.toString())
                        db!!.collection("Lecturer").document(document.id)
                            .update("Birth_Date", et_birth_datePro.text.toString())
                        db!!.collection("Lecturer").document(document.id)
                            .update("Address", et_addressPro.text.toString())
                        db!!.collection("Lecturer").document(document.id)
                            .update("Mobile", et_mobilePro.text.toString())
                    }
                }
            }
        db!!.collection("Courses").get()
            .addOnSuccessListener { querySnapshot1 ->
                for (document1 in querySnapshot1) {
                    db!!.collection("Lecturer").get()
                        .addOnSuccessListener { querySnapshot2 ->
                            for (document2 in querySnapshot2) {
                                if (document2.get("Email") == auth.currentUser!!.email) {
                                    if (document1.get("Lecturer") == "${
                                            document2.get("First_Name").toString()
                                        } ${
                                            document2.get("Middle_Name").toString()
                                        } ${document2.get("Family_Name").toString()}"
                                    ) {
                                        db!!.collection("Courses").document(document1.id).update(
                                            "Lecturer",
                                            "${et_fNamePro.text} ${et_mNamePro.text} ${et_lNamePro.text}"
                                        )
                                    }
                                }
                            }
                        }
                }
            }
    }


    private fun disableEdit() {
        et_fNamePro.isEnabled = false
        et_mNamePro.isEnabled = false
        et_lNamePro.isEnabled = false
        et_emailPro.isEnabled = false
        et_birth_datePro.isEnabled = false
        et_addressPro.isEnabled = false
        et_mobilePro.isEnabled = false
    }

    private fun enableEdit() {
        et_fNamePro.isEnabled = true
        et_mNamePro.isEnabled = true
        et_lNamePro.isEnabled = true
        et_emailPro.isEnabled = true
        et_birth_datePro.isEnabled = true
        et_addressPro.isEnabled = true
        et_mobilePro.isEnabled = true
    }
}
