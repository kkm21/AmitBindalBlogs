package com.example.amitbindalblogs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.amitbindalblogs.databinding.ActivityOtpBinding
import com.example.amitbindalblogs.utilities.NetworkConstants
import kotlinx.coroutines.launch
import org.json.JSONObject

class OtpActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpBinding
    lateinit var sharedPreferences: SharedPreferences
    var first:Int=0
    var second:Int=0
    var third:Int= 0
    var fourth:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("User Preference", Context.MODE_PRIVATE)

        binding.otp1.doAfterTextChanged {
            if (it?.length==1)
            {binding.otp2.requestFocus()
                first=binding.otp1.text.toString().toInt()}
            else
                binding.otp1.requestFocus()
        }
        binding.otp2.doAfterTextChanged {
            if (it?.length==1)
            {binding.otp3.requestFocus()
                second=binding.otp2.text.toString().toInt()}
            else
                binding.otp2.requestFocus()
        }
        binding.otp3.doAfterTextChanged {
            if (it?.length==1)
            {binding.otp4.requestFocus()
                third=binding.otp3.text.toString().toInt()}
            else
                binding.otp3.requestFocus()
        }
        binding.otp4.doAfterTextChanged {
            if (it?.length==1)
            {binding.otp4.clearFocus()
                val inputManager:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(currentFocus?.windowToken , InputMethodManager.SHOW_FORCED)
                fourth=binding.otp4.text.toString().toInt()}
            else
                binding.otp4.requestFocus()
        }
        binding.btnProceed.setOnClickListener {

                lifecycleScope.launch {
                    val otpIntent=intent.getStringExtra("otp").toString()
                    if (binding.otp1.text!=null && binding.otp2.text!=null && binding.otp3.text!=null && binding.otp4.text!=null){
                        if (otpIntent.toInt()%10==fourth && (otpIntent.toInt()/10)%10==third && (otpIntent.toInt()/100)%10==second && (otpIntent.toInt()/1000)%10==first){
                            if (intent.getStringExtra("from")=="signup")
                                checkRegisterOtp(otpIntent)
                            else
                                checkOtp(otpIntent = otpIntent)
                        }
                        else
                            Toast.makeText(this@OtpActivity,"Incorrect Otp Entered", Toast.LENGTH_LONG).show()
                    }else
                        Toast.makeText(applicationContext,"Please enter the OTP correctly", Toast.LENGTH_LONG).show()
                }

        }

        binding.back.setOnClickListener {
            onBackPressed()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun checkRegisterOtp(otpIntent:String) {
        lifecycleScope.launch {
            val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
            val url = NetworkConstants().VERIFY_REG_OTP

            val json = JSONObject()
            json.put("otp", otpIntent)

            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, url,json,
                Response.Listener<JSONObject> { response ->
                    Toast.makeText(
                        this@OtpActivity,
                        response.toString(),
                        Toast.LENGTH_LONG
                    ).show()

                    val obj = response.getJSONObject("user")
                    val editor = sharedPreferences.edit()
                    editor.putString("user_name", obj.getString("user_name"))
                    editor.putString("email", obj.getString("email"))
                    editor.putString("mobile_number", obj.getString("mobile_number"))

                    editor.putString("token", obj.getString("token"))
                    editor.putString("pincode", obj.getString("pincode"))
                    editor.putString("address", obj.getString("address"))
                    editor.putString("_id", obj.getString("_id"))
                    editor.putBoolean("isLoggedin", true)
                    editor.apply()
                    Toast.makeText(
                        this@OtpActivity,
                        "Welcome ${obj.getString("user_name")}",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(applicationContext, FeedPageActivity::class.java)
                    intent.putExtra("fromRegisterActivity",true)
                    startActivity(intent)
                },
                Response.ErrorListener { error ->
                    Toast.makeText(
                        applicationContext,
                        error.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["otp"] = otpIntent
                    return params
                }
            }
            queue.add(jsonObjectRequest)
        }

    }

    private fun checkOtp(otpIntent:String) {
        lifecycleScope.launch {
            val queue: RequestQueue = Volley.newRequestQueue(this@OtpActivity)

            val url = NetworkConstants().LOGIN_VERIFY_API

            val json = JSONObject()
            json.put("otp", otpIntent)

            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, url,json,
                Response.Listener<JSONObject> { response ->


                    val obj = response.getJSONObject("user")
                    val editor = sharedPreferences.edit()
                    editor.putString("user_name", obj.getString("user_name"))
                    editor.putString("email", obj.getString("email"))
                    editor.putString("mobile_number", obj.getString("mobile_number"))
                    editor.putString("city",obj.getString("city"))
                    editor.putString("state",obj.getString("state"))
                    editor.putString("adhar_number",obj.getString("adhar_number"))
                    editor.putString("profile","https://amitbindal.in"+obj.getString("profile"))
                    editor.putString("token", obj.getString("token"))
                    editor.putString("pincode", obj.getString("pincode"))
                    editor.putString("address", obj.getString("address"))
                    editor.putString("_id", obj.getString("_id"))
                    editor.putBoolean("isLoggedin", true)
                    editor.apply()

                    Log.d("checkToken", "checkOtp: ${sharedPreferences.getString("token","")}, ${obj.getString("token")}")
                    Toast.makeText(
                        this@OtpActivity,
                        "Welcome Back, ${obj.getString("user_name")} ",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(applicationContext, FeedPageActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(
                        this@OtpActivity,
                        error.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    return params
                }
            }
            queue.add(jsonObjectRequest)
        }
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}