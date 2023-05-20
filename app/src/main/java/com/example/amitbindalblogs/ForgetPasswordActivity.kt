package com.example.amitbindalblogs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.amitbindalblogs.databinding.ActivityForgetPasswordBinding
import com.example.amitbindalblogs.databinding.ActivityOtpBinding

class ForgetPasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}