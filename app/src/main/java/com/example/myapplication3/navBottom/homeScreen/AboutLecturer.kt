package com.example.myapplication3.navBottom.homeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication3.R
import com.example.myapplication3.navBottom.listscreen.MyAdapter

class AboutLecturer : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about_lecturer, container, false)

        return view
    }
}
