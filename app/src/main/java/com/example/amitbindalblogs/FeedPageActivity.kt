package com.example.amitbindalblogs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.amitbindalblogs.adapter.FeedViewPagerAdapter
import com.example.amitbindalblogs.adapter.HashtagAdapter
import com.example.amitbindalblogs.adapter.RecentFeedItemAdapter
import com.example.amitbindalblogs.databinding.ActivityFeedPageBinding
import com.example.amitbindalblogs.databinding.ActivityForgetPasswordBinding
import com.example.amitbindalblogs.fragments.ImagesFeedFragment
import com.example.amitbindalblogs.fragments.PopularFeedFragment
import com.example.amitbindalblogs.fragments.RecentFeedFragment
import com.example.amitbindalblogs.fragments.VideosFeedFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FeedPageActivity : AppCompatActivity() {

    lateinit var binding: ActivityFeedPageBinding

    var fragmentManager: FragmentManager? = null
    var fragmentTransaction: FragmentTransaction? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFeedPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imgList= arrayOf("")
        val taglist= arrayOf("kkm", "rahul", "gandhi","kkm", "rahul", "gandhi","kkm", "rahul", "gandhi")

        binding.hashtagRV.layoutManager=LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        binding.hashtagRV.adapter=HashtagAdapter(imgList, taglist)
        binding.feedTab.addTab(binding.feedTab.newTab().setText("Recent"))
        binding.feedTab.addTab(binding.feedTab.newTab().setText("Popular"))
        binding.feedTab.addTab(binding.feedTab.newTab().setText("Images"))
        binding.feedTab.addTab(binding.feedTab.newTab().setText("Videos"))

        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.frameContainer, RecentFeedFragment())
        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction!!.commit()

        var fragment: Fragment? = null
        binding.feedTab!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // creating cases for fragment
                when (tab.position) {
                    0 -> fragment = RecentFeedFragment()
                    1 -> fragment = PopularFeedFragment()
                    2 -> fragment = ImagesFeedFragment()
                    3 -> fragment = VideosFeedFragment()
                }
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                fragment?.let { ft.replace(R.id.frameContainer, it) }
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        binding.userProfile.setOnClickListener {
            val intent =Intent(this@FeedPageActivity,UserProfileActivity::class.java)
            startActivity(intent)
        }




    }

    override fun onResume() {
        super.onResume()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}