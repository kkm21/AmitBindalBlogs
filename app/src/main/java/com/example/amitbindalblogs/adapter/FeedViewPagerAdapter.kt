package com.example.amitbindalblogs.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.amitbindalblogs.fragments.ImagesFeedFragment
import com.example.amitbindalblogs.fragments.PopularFeedFragment
import com.example.amitbindalblogs.fragments.RecentFeedFragment
import com.example.amitbindalblogs.fragments.VideosFeedFragment
import kotlin.system.exitProcess

class FeedViewPagerAdapter(
    fragmentActivity: FragmentActivity, val totalTabs:Int
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return totalTabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RecentFeedFragment()
            1 -> PopularFeedFragment()
            2 -> ImagesFeedFragment()
            3 -> VideosFeedFragment()

            else -> {
                exitProcess(0)
            }
        }
    }
}