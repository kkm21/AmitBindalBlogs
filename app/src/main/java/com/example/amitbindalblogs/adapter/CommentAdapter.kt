package com.example.amitbindalblogs.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.amitbindalblogs.R

class CommentAdapter: RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        val userpic: ImageView = binding.findViewById(R.id.commenterPic)
        val likeImage: ImageView = binding.findViewById(R.id.likeComment)
        val commenterName: TextView = binding.findViewById(R.id.etName)
        val commentTime: TextView = binding.findViewById(R.id.etTime)
        val comment: TextView = binding.findViewById(R.id.etComment)
        val likeCount: TextView = binding.findViewById(R.id.etLikeCount)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position%2==0) {
            holder.userpic.setImageResource(R.mipmap.ic_launcher)
            holder.likeImage.setImageResource(R.drawable.like)}
        else {
            holder.userpic.setImageResource(R.mipmap.ic_launcher_round)
            holder.likeImage.setImageResource(R.drawable.unlike)}

        holder.comment.text="This is the demo comment done by the programmer to display the list of comments"
        holder.commenterName.text="demo commenter name"
        holder.commentTime.text="12-12-1996"
        holder.likeCount.text="${position+1}"

        holder.likeImage.setOnClickListener {
            if(holder.likeImage.id==R.drawable.unlike)
                holder.likeImage.setImageResource(R.drawable.like)
            else
                holder.likeImage.setImageResource(R.drawable.unlike)
            notifyDataSetChanged()

        }
    }

    override fun getItemCount(): Int {
        return 3
    }


}