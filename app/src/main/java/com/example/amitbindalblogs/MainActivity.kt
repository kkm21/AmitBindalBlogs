package com.example.amitbindalblogs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.amitbindalblogs.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=getSharedPreferences("User Preference", Context.MODE_PRIVATE)

        lifecycleScope.launch {

            if (!sharedPreferences.getBoolean("isLoggedin",false)){
                binding.btnSignUp.setOnClickListener {
                    val intent=Intent(this@MainActivity,SignUpActivity::class.java)
                    startActivity(intent)
                }

                binding.btnSignIn.setOnClickListener {
                    val intent=Intent(this@MainActivity,LoginActivity::class.java)
                    startActivity(intent)
                }
                printHashKey()
            }else{
                val intent=Intent(this@MainActivity,FeedPageActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("PackageManagerGetSignatures")
    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(
                "com.example.amitbindalblogs",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                //Log.d("KeyHash:", Base64.getEncoder().encodeToString(md.digest()))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }

}