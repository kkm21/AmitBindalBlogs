package com.example.amitbindalblogs.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.amitbindalblogs.FeedFullScreenActivity
import com.example.amitbindalblogs.R
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

open class RecentFeedItemAdapter(val context:Context, val jsonArray: JSONArray): RecyclerView.Adapter<RecentFeedItemAdapter.ViewHolder>() {


    class ViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        val imageView1: ImageView = binding.findViewById(R.id.feedType)
        val imageView2: ImageView = binding.findViewById(R.id.feedBookmark)
        val imageView3:ImageView=binding.findViewById(R.id.recentFeedImg)
        val title: TextView = binding.findViewById(R.id.title)
        val textView2: TextView = binding.findViewById(R.id.date)
        val cardVIew:CardView=binding.findViewById(R.id.recentFeed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_feed, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val json=jsonArray.getJSONObject(position)

        if (position%2==0) {
            holder.imageView1.setImageResource(R.drawable.camera)
            holder.imageView2.setImageResource(R.drawable.bookmark_pressed)}
        else {
            holder.imageView1.setImageResource(R.drawable.video_camera)
            holder.imageView2.setImageResource(R.drawable.bookmark_notpressed)}
        val images=json.get("image")
        if (json.has("thumbnail"))
            Picasso.get().load("https://amitbindal.in"+json.getString("thumbnail")).into(holder.imageView3)
        holder.title.text = json.getString("title")
        holder.textView2.text="${position+1}-Jun-2005"
        holder.cardVIew.setOnClickListener {
            Toast.makeText(context,"item clicked at position number $position, $images",Toast.LENGTH_LONG).show()
            val intent=Intent(context,FeedFullScreenActivity::class.java)
            intent.putExtra("thumbnail",json.getString("thumbnail"))
            intent.putExtra("position",position)
            intent.putExtra("description",json.getString("description"))
            context.startActivity(intent)
        }

    }



}