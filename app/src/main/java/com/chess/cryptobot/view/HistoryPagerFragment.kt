package com.chess.cryptobot.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chess.cryptobot.R
import com.chess.cryptobot.content.history.HistoryHolder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy

class HistoryPagerFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.history_pager_fragment, container, false)
        if (this.activity == null) return view
        val viewPager: ViewPager2 = view.findViewById(R.id.pager)
        val pagerAdapter: FragmentStateAdapter = PagerAdapter(this.activity as FragmentActivity)
        viewPager.adapter = pagerAdapter
        val tabs: TabLayout = view.findViewById(R.id.pager_header)
        TabLayoutMediator(tabs, viewPager,
                TabConfigurationStrategy { tab: TabLayout.Tab, position: Int -> tab.text = if (position == 0) "History" else "Orders" }
        ).attach()
        return view
    }

    internal class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HistoryFragment(HistoryHolder.State.HISTORY)
                1 -> HistoryFragment(HistoryHolder.State.ORDERS)
                else -> throw IllegalArgumentException("Unknown viewPager Position")
            }
        }

        override fun getItemCount(): Int {
            return 2
        }
    }
}