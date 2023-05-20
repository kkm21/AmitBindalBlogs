package com.example.amitbindalblogs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.amitbindalblogs.databinding.ActivityLoginBinding
import com.example.amitbindalblogs.utilities.NetworkConstants
import com.facebook.AccessToken
import com.facebook.AccessTokenTracker
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject


class LoginActivity : AppCompatActivity(){

    lateinit var binding:ActivityLoginBinding
    private val Google_SIGN_IN = 1001
    lateinit var accessTokenTracker: AccessTokenTracker
    private val Facebook_SIGN_IN = 12345
    var googleSignInClient: GoogleSignInClient? = null
    lateinit var sharedPreferences: SharedPreferences
    private var firebaseAuth: FirebaseAuth? = null
    lateinit var mCallbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=getSharedPreferences("User Preference", Context.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()
        mCallbackManager = CallbackManager.Factory.create()
        binding.facebookLogin.setReadPermissions(listOf("email", "public_profile"))
        /*binding.facebookLogin.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("fbCheck", "facebook:onSuccess:$loginResult, ${loginResult.accessToken.userId} ${loginResult.authenticationToken?.token}")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d("fbCheck", "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d("fbCheck", "facebook:onError", error)
                }
            },
        )*/
        binding.facebookLogin.registerCallback(
            mCallbackManager,
            object : FacebookCallback<LoginResult> {

                // called when the dialog is canceled.
                override fun onCancel() {
                    Log.e("message: ", "onCancel")
                }

                // called when the dialog finishes with an error.
                override fun onError(exception: FacebookException) {
                    exception.localizedMessage?.let { Log.e("message: ", it) }
                }

                override fun onSuccess(result: LoginResult) {
                    Log.e("message: ", "success"+result.accessToken)
                }
            })

        accessTokenTracker = object : AccessTokenTracker() {

            // The method that will be called with the access token changes.
            override fun onCurrentAccessTokenChanged(
                oldAccessToken: AccessToken?,
                currentAccessToken: AccessToken?
            ) {
                // condition is true when user logs out
                if (currentAccessToken == null) {

                    Toast.makeText(this@LoginActivity, "LogOut", Toast.LENGTH_SHORT).show()
                } else {
                    //get user data from Json object and display it in textviews.
                    loadUserProfile(currentAccessToken)
                }
            }
        }



        binding.googleLogin.setOnClickListener {
            configureGoogleClient()
            signInToGoogle()
        }

        binding.btnProceed.setOnClickListener {

            if(binding.etMobileNo.text.isNotEmpty()){
                login()
            }
            else {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter the Mobile Number!",
                    Toast.LENGTH_LONG
                ).show()
                binding.etMobileNo.requestFocus()
            }
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }
    }
    fun signInToGoogle() {
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, Google_SIGN_IN)
    }
    override fun onStart() {
        super.onStart()

        // Checking if the user is signed in (non-null) and update UI accordingly.
        val currentUser: FirebaseUser? = firebaseAuth?.currentUser
        if (currentUser != null) {
            Toast.makeText(
                this@LoginActivity,
                "Currently Logged in: " + currentUser.email,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Google_SIGN_IN) {
            val result: GoogleSignInResult? = data?.let {
                Auth.GoogleSignInApi.getSignInResultFromIntent(
                    it
                )
            }
            val res: GoogleSignInAccount? =result?.signInAccount
            Log.e("GoogleCheck", "${res?.email}, ${res?.displayName}, ${res?.id}, ${res?.account},, ")
            if (result != null) {
                if(result.isSuccess){
                    signUpWithGoogle(res?.displayName, res?.email, res?.id)
                    val intent = Intent(this@LoginActivity, FeedPageActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        if (requestCode==Facebook_SIGN_IN)
            mCallbackManager?.onActivityResult(requestCode, resultCode, data);
    }

    private fun configureGoogleClient() {

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.example.amitbindalblogs.R.string.web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.googleLogin.setSize(SignInButton.SIZE_WIDE)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun login() {

        val queue: RequestQueue = Volley.newRequestQueue(this@LoginActivity)
        val url = NetworkConstants().LOGIN_API

        val json= JSONObject()
        json.put("mobile_number",binding.etMobileNo.text.toString())
        val jsonObjectRequest: JsonObjectRequest = @SuppressLint("SuspiciousIndentation")
        object : JsonObjectRequest(
            Method.POST, url,json,
            Response.Listener<JSONObject?> { response ->

                checkOtp(response.getString("otp").toString())
                Log.d("otp","$response")
                Toast.makeText(this,"$response",Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    this@LoginActivity,
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

    private fun signUpWithGoogle(name:String?,email:String?,id:String?){
        lifecycleScope.launch {
            val queue: RequestQueue = Volley.newRequestQueue(this@LoginActivity)
            val url = NetworkConstants().Gmail_Login

            val json= JSONObject()
            json.put("user_name",name)
            json.put("email",email)
            json.put("gmail_id",id)
            val jsonObjectRequest: JsonObjectRequest = @SuppressLint("SuspiciousIndentation")
            object : JsonObjectRequest(
                Method.POST, url,json,
                Response.Listener<JSONObject?> { response ->

                    val obj = response.getJSONObject("data")
                    val editor = sharedPreferences.edit()
                    if (obj.has("dob"))
                        editor.putString("dob", obj.getString("dob"))
                    if (obj.has("adhar_number"))
                        editor.putString("adhar_number", obj.getString("adhar_number"))
                    if (obj.has("state"))
                        editor.putString("state", obj.getString("state"))
                    if (obj.has("city"))
                        editor.putString("city", obj.getString("city"))
                    if (obj.has("mobile_number"))
                        editor.putString("mobile_number", obj.getString("mobile_number"))
                    editor.putString("user_name", obj.getString("user_name"))
                    editor.putString("email", obj.getString("email"))
                    editor.putString("gmail_id", obj.getString("gmail_id"))
                    editor.putString("profile","https://amitbindal.in/"+obj.getString("profile"))
                    editor.putString("token", obj.getString("token"))
                    editor.putString("pincode", obj.getString("pincode"))
                    editor.putString("address", obj.getString("address"))
                    editor.putString("_id", obj.getString("_id"))
                    editor.putBoolean("isLoggedin", true)
                    editor.apply()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(
                        this@LoginActivity,
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

    private fun checkOtp(otp: String) {
        val intent=Intent(this@LoginActivity,OtpActivity::class.java)
        intent.putExtra("otp",otp)
        startActivity(intent)
        finish()
    }

    private fun loadUserProfile(currentAccessToken: AccessToken) {
        // creates a new Request configured to retrieve a user's own profile.
        lifecycleScope.launch {
            val request = GraphRequest.newMeRequest(
                currentAccessToken
            ) { obj, response ->
                Log.d("fbCHeck", obj.toString() + " ")
                try {
                    val first_name = obj?.getString("first_name")
                    val last_name = obj?.getString("last_name")
                    val email = obj?.getString("email")
                    val id = obj?.getString("id")
                    val photoUrl = "https://graph.facebook.com/$id/picture?type=normal"
                    val fullName = "${obj?.getString("first_name")} ${obj?.getString("last_name")}"

                    Log.d(
                        "fbCheck",
                        "onCompleted: ${first_name}, ${last_name}, \n${email},${id} \n${photoUrl}  "
                    )
                    signUpWithFacebook(fullName, email, id, photoUrl)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            val parameters = Bundle()
            parameters.putString("fields", "first_name,last_name,email,id")
            request.parameters = parameters
            request.executeAsync()
        }
    }

    private fun signUpWithFacebook(name:String?,email:String?,id:String?,photo:String){
        lifecycleScope.launch {
            val queue: RequestQueue = Volley.newRequestQueue(this@LoginActivity)
            val url = NetworkConstants().Facebook_Login

            val json= JSONObject()
            json.put("user_name",name)
            json.put("email",email)
            json.put("fb_id",id)
            val jsonObjectRequest: JsonObjectRequest = @SuppressLint("SuspiciousIndentation")
            object : JsonObjectRequest(
                Method.POST, url,json,
                Response.Listener<JSONObject?> { response ->

                    val obj = response.getJSONObject("data")
                    val editor = sharedPreferences.edit()
                    //editor.putString("_id", obj.getString("_id"))
                    editor.putString("user_name", obj.getString("user_name"))
                    editor.putString("email", obj.getString("email"))
                    editor.putString("profile",photo)
                    //editor.putString("gmail_id", obj.getString("gmail_id"))
                    /*editor.putString("role_id", obj.getString("role_id"))
                    editor.putString("address", obj.getString("address"))
                    editor.putString("pincode", obj.getString("pincode"))*/
                    editor.putString("token", obj.getString("token"))
                    editor.putBoolean("isLoggedin", true)
                    editor.apply()
                    val intent = Intent(this@LoginActivity, FeedPageActivity::class.java)
                    startActivity(intent)
                },
                Response.ErrorListener { error ->
                    Toast.makeText(
                        this@LoginActivity,
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

}