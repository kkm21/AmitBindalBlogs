package com.example.amitbindalblogs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.amitbindalblogs.databinding.ActivityEditProfileBinding
import com.example.amitbindalblogs.utilities.NetworkConstants
import com.example.amitbindalblogs.utilities.VolleyMultipartRequest
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException


class EditProfileActivity  : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding
    private val GALLERY = 1
     var bitmap: Bitmap? =null
    private val uploadURL = NetworkConstants().Profile_Update
    lateinit var rQueue: RequestQueue


    lateinit var sharedPreferences: SharedPreferences
//    private var arraylist: ArrayList<HashMap<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=getSharedPreferences("User Preference", Context.MODE_PRIVATE)
        println(NetworkConstants().token)
        binding.etName.setText(sharedPreferences.getString("user_name",""))
        binding.etEmail.setText(sharedPreferences.getString("email",""))
        binding.etMobileNo.setText(sharedPreferences.getString("mobile_number",""))
        binding.etAadhaar.setText(sharedPreferences.getString("adhar_number",""))
        binding.etAddressLine1.setText(sharedPreferences.getString("address",""))
        binding.etState.setText(sharedPreferences.getString("state",""))
        binding.etCity.setText(sharedPreferences.getString("city",""))
        binding.etPincode.setText(sharedPreferences.getString("pincode",""))
        binding.etDOB.setText(sharedPreferences.getString("dob",""))
        if(sharedPreferences.getString("profile","")!="")
            Picasso.get()
                .load(sharedPreferences.getString("profile",""))
                .into(binding.IVUserPic)

        requestMultiplePermissions()


        binding.uploadPicture.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY)
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.btnProceed.setOnClickListener {
            bitmap?.let { it1 -> uploadImage(it1) }
        }
    }

       @Deprecated("Deprecated in Java")
       override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val contentURI = data?.data
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                try {
//                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                     val thisbitmap = BitmapFactory.decodeStream(contentURI?.let {
                        contentResolver.openInputStream(
                            it
                        )
                    })
                    bitmap=thisbitmap
                    binding.IVUserPic.setImageBitmap(bitmap)
//                    imageView?.setImageBitmap(bitmap)


                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@EditProfileActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImage(bitmap: Bitmap) {

        val volleyMultipartRequest =  object : VolleyMultipartRequest(Method.POST, uploadURL,
            Response.Listener { response ->
                Log.d("ressssssoo", String(response.data))
                try {
                    val jsonObject = JSONObject(String(response.data))
                    Toast.makeText(applicationContext, jsonObject.getString("user"), Toast.LENGTH_SHORT).show()
                    val jsonData=JSONObject(jsonObject.getString("user"))
                    println(jsonObject)
                    val editor =sharedPreferences.edit()
                    editor.putString("address",jsonData.getString("address"))
                    editor.putString("pincode",jsonData.getString("pincode"))
                    editor.putString("city",jsonData.getString("city"))
                    editor.putString("state",jsonData.getString("state"))
                    editor.putString("adhar_number",jsonData.getString("adhar_number"))
                    editor.putString("profile","https://amitbindal.in"+jsonData.getString("profile"))
                    editor.apply()

                    Log.d("profilePic", "https://amitbindal.in"+sharedPreferences.getString("profile","").toString())
                    finish()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                Log.d("error", "${error.message}")
            }) {


            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()

                params["address"] = binding.etAddressLine1.text.toString()
                params["state"] = binding.etState.text.toString()
                params["city"] = binding.etCity.text.toString()
                params["pincode"] = binding.etPincode.text.toString()
                params["adhar_number"] = binding.etAadhaar.text.toString()
                params["dob"] = binding.etDOB.text.toString()
                return params
            }

            /*
             *pass files using below method
             * */
//            @Throws(AuthFailureError::class)
            override fun getByteData(): Map<String, DataPart> {
                val params = HashMap<String, DataPart>()
                val imagename = System.currentTimeMillis()
                params["profile"] = DataPart("$imagename.png", getFileDataFromDrawable(bitmap))
                return params
            }

            override fun getHeaders(): Map<String, String> {
                val params =HashMap<String,String>()
                params["Authorization"]=sharedPreferences.getString("token","").toString()
                return params
            }


        }


        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        rQueue = Volley.newRequestQueue(this@EditProfileActivity)
        rQueue.add(volleyMultipartRequest)
    }

    private fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun requestMultiplePermissions() {
        Dexter.withActivity(this)
            .withPermissions(

                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                 override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Toast.makeText(applicationContext, "All permissions are granted by user!", Toast.LENGTH_SHORT).show()
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).withErrorListener { Toast.makeText(applicationContext, "Some Error! ", Toast.LENGTH_SHORT).show() }
            .onSameThread()
            .check()
    }
}