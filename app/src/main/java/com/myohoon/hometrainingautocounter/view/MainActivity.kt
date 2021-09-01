package com.myohoon.hometrainingautocounter.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TAB_LABEL_VISIBILITY_UNLABELED
import com.google.android.material.tabs.TabLayoutMediator
import com.myohoon.hometrainingautocounter.R
import com.myohoon.hometrainingautocounter.databinding.ActivityMainBinding
import com.myohoon.hometrainingautocounter.view.fragment.calender.CalenderFragment
import com.myohoon.hometrainingautocounter.view.fragment.graph.GraphFragment
import com.myohoon.hometrainingautocounter.view.fragment.main.MainFragment
import com.myohoon.hometrainingautocounter.view.fragment.ranking.RankingFragment
import com.myohoon.hometrainingautocounter.view.fragment.setting.SettingFragment

class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "MainActivity"
        private const val NUM_PAGES = 5
    }
    //view
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding.pager.adapter = ScreenSlidePagerAdapter(this)

        initTabLayout()
    }

    private fun initTabLayout() {
        //아이콘 설정
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when(position){
                0 -> {
                    tab.text = getString(R.string.calender)
                    tab.icon = getDrawable(R.drawable.ic_baseline_today_24)
                }
                1 -> {
                    tab.text = getString(R.string.chart)
                    tab.icon = getDrawable(R.drawable.ic_baseline_insert_chart_outlined_24)
                }
                2 -> {
                    tab.text = getString(R.string.home)
                    tab.icon = getDrawable(R.drawable.ic_baseline_home_24)
                }
                3 -> {
                    tab.text = getString(R.string.ranking)
                    tab.icon = getDrawable(R.drawable.ic_baseline_military_tech_24)
                }
                4 -> {
                    tab.text = getString(R.string.setting)
                    tab.icon = getDrawable(R.drawable.ic_baseline_settings_applications_24)
                }
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {
                p0?.icon?.setTint(ContextCompat.getColor(this@MainActivity, R.color.gray_300))
            }
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.icon?.setTint(ContextCompat.getColor(this@MainActivity, R.color.blue_200))
            }
        })
        binding.tabLayout.getTabAt(2)?.select()
    }

    override fun onBackPressed() {
        if (binding.pager.currentItem == 0) {
            super.onBackPressed()
        } else {
            binding.pager.currentItem = binding.pager.currentItem - 1
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> CalenderFragment()
                1 -> GraphFragment()
                2 -> MainFragment()
                3 -> RankingFragment()
                4 -> SettingFragment()
                else -> MainFragment()
            }
        }
    }
}