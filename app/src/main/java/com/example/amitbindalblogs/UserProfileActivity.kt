package com.example.amitbindalblogs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.amitbindalblogs.databinding.ActivityUserProfileBinding
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class UserProfileActivity : AppCompatActivity() {
    lateinit var binding:ActivityUserProfileBinding
    lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences=getSharedPreferences("User Preference",Context.MODE_PRIVATE)
        binding= ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.userName.text=sharedPreferences.getString("user_name","")
        if (sharedPreferences.getString("profile","")!="")
        Picasso.get()
            .load(sharedPreferences.getString("profile",""))
            .into(binding.userPic)



        binding.editProfile.setOnClickListener {
            val intent= Intent(this@UserProfileActivity,EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.logOutRL.setOnClickListener {

            LoginManager.getInstance().logOut()
            FirebaseAuth.getInstance().signOut().apply {
                val intent=Intent(this@UserProfileActivity,MainActivity::class.java)
                val editor=sharedPreferences.edit()
                editor.clear()
                editor.apply()
                startActivity(intent)
                finish()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (sharedPreferences.getString("profile","")!="")
            Picasso.get()
                .load(sharedPreferences.getString("profile",""))
                .into(binding.userPic)
    }


}