package com.example.myapplication3.navBottom.bottomNav

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.myapplication3.R
import com.example.myapplication3.logIn.MainActivity
import com.example.myapplication3.navBottom.bottomNav.BackButtonBehaviour.POP_HOST_FRAGMENT
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BottomNavFragment : Fragment() {

    private val bottomNavSelectedItemIdKey = "BOTTOM_NAV_SELECTED_ITEM_ID_KEY"
    private var bottomNavSelectedItemId = R.id.home

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_nav, container, false)

        auth = Firebase.auth
        db = Firebase.firestore

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            bottomNavSelectedItemId =
                savedInstanceState.getInt(bottomNavSelectedItemIdKey, bottomNavSelectedItemId)
        }
        setupBottomNavBar(view)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(bottomNavSelectedItemIdKey, bottomNavSelectedItemId)
        super.onSaveInstanceState(outState)
    }

    private fun setupBottomNavBar(view: View) {
        val bottomNavView = view.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val toolbar = view.findViewById<Toolbar>(R.id.bottom_nav_toolbar)

        val navGraphIds = listOf(
            R.navigation.home,
            R.navigation.list,
            R.navigation.home2,
            R.navigation.form,
            R.navigation.home3
        )

        addToolbarListener(toolbar)
        bottomNavView.selectedItemId =
            bottomNavSelectedItemId

        val controller = bottomNavView.setupWithNavController(
            fragmentManager = childFragmentManager,
            navGraphIds = navGraphIds,
            backButtonBehaviour = POP_HOST_FRAGMENT,
            containerId = R.id.bottom_nav_container,
            firstItemId = R.id.home,
            intent = requireActivity().intent
        )

        controller.observe(viewLifecycleOwner, { navController ->
            NavigationUI.setupWithNavController(toolbar, navController)
            bottomNavSelectedItemId =
                navController.graph.id
        })
    }

    private fun addToolbarListener(toolbar: Toolbar) {
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addsCourse -> {
                    db!!.collection("Lecturer").get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot) {
                                if (document.get("Email") == auth.currentUser!!.email) {
                                    findNavController().navigate(R.id.addsCourse)

                                }
                            }
                        }
                    db!!.collection("Student").get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot) {
                                if (document.get("Email") == auth.currentUser!!.email) {
                                    Toast.makeText(
                                        context, "It's not for you, only for Lecturer",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }
                        }
                    true
                }
                R.id.searchCourse -> {
                    findNavController().navigate(R.id.searchCourse)
                    true
                }
                else -> false
            }
        }
    }
}
