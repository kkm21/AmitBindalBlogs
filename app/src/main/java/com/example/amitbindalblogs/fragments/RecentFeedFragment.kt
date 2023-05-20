package com.example.amitbindalblogs.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.amitbindalblogs.adapter.RecentFeedItemAdapter
import com.example.amitbindalblogs.databinding.FragmentRecentFeedBinding
import com.example.amitbindalblogs.utilities.NetworkConstants
import kotlinx.coroutines.launch
import org.json.JSONObject


class RecentFeedFragment : Fragment() {

    private var _binding:FragmentRecentFeedBinding?=null
    private val binding get() = _binding!!
    lateinit var sharedPreferences:SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentRecentFeedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences=requireContext().getSharedPreferences("User Preference", Context.MODE_PRIVATE)
        getRecentBlog()
    }

    override fun onResume() {
        super.onResume()
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
                    Log.d(TAG, "getRecentBlog: $arr")

                    binding.recentFeedRV.layoutManager=LinearLayoutManager(activity as Context)
                    binding.recentFeedRV.adapter=RecentFeedItemAdapter(requireContext(), arr)
                    
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
                    params["Authorization"]=sharedPreferences.getString("token","").toString()
                    return params
                }
            }
            queue.add(jsonObjectRequest)
        }
    }

}