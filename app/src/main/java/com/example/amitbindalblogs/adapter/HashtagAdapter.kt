package com.example.amitbindalblogs.adapter

import android.annotation.SuppressLint
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.amitbindalblogs.R

class HashtagAdapter(private val imageList: Array<String>, private val tagList: Array<String>): RecyclerView.Adapter<HashtagAdapter.ViewHolder>() {

    class ViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        val imageView: ImageView = binding.findViewById(R.id.img)
        val textView: TextView = binding.findViewById(R.id.tagname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hashtag, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(R.mipmap.ic_launcher)
        holder.textView.text = "#"+tagList[position]
    }
}