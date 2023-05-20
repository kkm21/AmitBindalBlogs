@file:Suppress("OverrideDeprecatedMigration")

package com.example.amitbindalblogs

import android.graphics.BitmapFactory
import android.media.MediaPlayer.*
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.amitbindalblogs.adapter.CommentAdapter
import com.example.amitbindalblogs.databinding.ActivityFeedFullScreenBinding
import com.squareup.picasso.Picasso
import org.json.JSONObject


class FeedFullScreenActivity : AppCompatActivity() {

    lateinit var binding: ActivityFeedFullScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFeedFullScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val json: JSONObject? = intent.getStringExtra("feedDetails")?.let { JSONObject(it) }
        //val videoUri=intent.getStringExtra("video_link")
        if (intent.getIntExtra("position",0)%2==0){
            binding.imageLayout.visibility=View.VISIBLE
            binding.recentFeedImg.visibility=View.VISIBLE
            binding.newsBody1.text = intent.getStringExtra("description").toString()
            binding.title.visibility=View.VISIBLE
            Picasso.get()
                .load("https://amitbindal.in"+intent.getStringExtra("thumbnail"))
                .into(binding.recentFeedImg)
           // binding.recentFeedImg.setImageURI(Uri.parse("https://amitbindal.in"+))
            binding.dateNewsViews.visibility=View.VISIBLE
            binding.recentFeedVideo.visibility=View.GONE
        }
        else{
            binding.imageLayout.visibility=View.GONE
            binding.recentFeedVideo.visibility=View.VISIBLE
            binding.newsBody1.setText(intent.getStringExtra("description"))
            Picasso.get()
                .load("https://amitbindal.in"+intent.getStringExtra("thumbnail"))
                .into(binding.recentFeedImg)
            //Log.e("videoCheck", "${ videoUri } ,\n ${Uri.parse(videoUri)}")
            //binding.recentFeedVideo.setVideoURI(Uri.parse(videoUri))

            binding.recentFeedVideo.requestFocus()

            binding.recentFeedVideo.setOnCompletionListener(OnCompletionListener { })

            binding.recentFeedVideo.setOnPreparedListener(OnPreparedListener { mediaPlayer ->
                val mediaController = MediaController(this@FeedFullScreenActivity)
                binding.recentFeedVideo.setMediaController(mediaController)
                mediaController.setAnchorView(binding.recentFeedVideo)
                binding.recentFeedVideo.start()

            })

            binding.recentFeedVideo.setOnErrorListener(OnErrorListener { mediaPlayer, i, i1 ->
                Log.d("TAG", "onCreate: $mediaPlayer, $i, $i1")
                false})
        }
        binding.newsBody1.text = json?.getString("description")

        binding.commentTab.addTab(binding.commentTab.newTab().setText("Recent"))
        binding.commentTab.addTab(binding.commentTab.newTab().setText("Popular"))

        binding.commentRV.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.commentRV.adapter=CommentAdapter()

        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}