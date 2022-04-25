package com.example.myapplication3.navBottom.listscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication3.R

class Leaderboard : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        val viewAdapter = MyAdapter(Array(2) { "Person ${it + 1}" })

        view.findViewById<RecyclerView>(R.id.leaderboard_list).run {
            setHasFixedSize(true)

            adapter = viewAdapter

        }
        return view
    }

}

class MyAdapter(private val myDataset: Array<String>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_view_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.item.findViewById<TextView>(R.id.user_name_text).text = myDataset[position]

        holder.item.findViewById<ImageView>(R.id.user_avatar_image)
            .setImageResource(listOfAvatars[position % listOfAvatars.size])

        holder.item.setOnClickListener {
            val bundle = bundleOf(USERNAME_KEY to myDataset[position])

            holder.item.findNavController().navigate(
                R.id.action_leaderboard_to_userProfile,
                bundle
            )
        }
    }

    override fun getItemCount() = myDataset.size

    companion object {
        const val USERNAME_KEY = "userName"
    }
}

private val listOfAvatars = listOf(
    R.drawable.avatar_1_raster,
    R.drawable.avatar_1_raster
)
