package com.example.myapplication3.navBottom.h2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication3.R

class Title2 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_title2, container, false)

        view.findViewById<Button>(R.id.about_btn2).setOnClickListener {
            findNavController().navigate(R.id.action_title_to_about2)
        }
        return view
    }
}
