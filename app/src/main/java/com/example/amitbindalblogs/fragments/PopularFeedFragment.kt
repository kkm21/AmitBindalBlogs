package com.example.amitbindalblogs.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.amitbindalblogs.R
import com.example.amitbindalblogs.databinding.FragmentPopularFeedBinding
import com.example.amitbindalblogs.databinding.FragmentRecentFeedBinding
import com.example.amitbindalblogs.utilities.NetworkConstants
import kotlinx.coroutines.launch
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PopularFeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PopularFeedFragment : Fragment() {

    private var _binding: FragmentPopularFeedBinding?=null
    private val binding get() = _binding!!
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentPopularFeedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences=requireContext().getSharedPreferences("User Preference", Context.MODE_PRIVATE)
        getRecentBlog()
    }

    private fun getRecentBlog() {
        lifecycleScope.launch {
            val queue: RequestQueue = Volley.newRequestQueue(requireContext())
            val url = NetworkConstants().BASE_URL+"get-blog"

            val jsonObjectRequest: JsonObjectRequest = @SuppressLint("SuspiciousIndentation")
            object : JsonObjectRequest(
                Method.GET, url,null,
                Response.Listener<JSONObject?> { response ->

                    val arr = response.getJSONArray("blog")
                    Log.d(ContentValues.TAG, "getRecentBlog: $arr")
                    var base64Image=arr.getJSONObject(2).getString("image")
                    base64Image=base64Image.drop(25)
                    base64Image.dropLast(3)
                    Log.d("base64Check", "getRecentBlog: $base64Image")
                    val decodedString= Base64.decode(base64Image, Base64.DEFAULT)
                    binding.testiMAGE.setImageBitmap(BitmapFactory.decodeByteArray(decodedString,0,decodedString.size))

                },
                Response.ErrorListener { error ->
                    Toast.makeText(
                        requireContext(),
                        error.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {

                    val params: MutableMap<String, String> = HashMap()
                    params["Authorization"]="Bearer"+" "+sharedPreferences.getString("token","")
                    return params
                }
            }
            queue.add(jsonObjectRequest)
        }
    }


}