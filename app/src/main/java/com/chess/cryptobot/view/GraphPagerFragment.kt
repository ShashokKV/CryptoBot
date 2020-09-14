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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class GraphPagerFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.graph_pager_layout, container, false)
        val viewPager: ViewPager2 = view.findViewById(R.id.graph_pager)
        if (this.activity == null) return view
        val pagerAdapter: FragmentStateAdapter = PagerAdapter(this.activity as FragmentActivity)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled = false
        val tabs: TabLayout = view.findViewById(R.id.graph_pager_header)
        TabLayoutMediator(tabs, viewPager) {
            tab: TabLayout.Tab, position: Int ->
            tab.text = if (position == 0) "Balance" else "Pairs" }.attach()
        return view
    }

    internal class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> BalanceGraphFragment()
                1 -> PairsGraphFragment()
                else -> throw IllegalArgumentException("Unknown viewPager Position")
            }
        }

        override fun getItemCount(): Int {
            return 2
        }
    }
}