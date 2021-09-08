package com.myohoon.hometrainingautocounter.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
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
import com.myohoon.hometrainingautocounter.viewmodel.ExerciseViewModel

class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "MainActivity"

        private const val NUM_PAGES = 5         //하단 탭 수

        private var mainActivity: MainActivity? = null

        fun showBottomTab(isShow:Boolean){
            mainActivity?.let {
                it.binding.tabLayout.visibility =
                    if (isShow) View.VISIBLE
                    else View.GONE
            }
        }

//        fun showProgressBar(b: Boolean) {
//            mActivity?.let { it.binding?.let { it.clPB?.let { clPb ->
//                if (b) {
//                    clPb.visibility = View.VISIBLE
//                    mActivity!!.window.setFlags(
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                    )
//                } else {
//                    clPb.visibility = View.GONE
//                    mActivity!!.window.clearFlags(
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                    )
//                }
//            } } }
//        }

        fun addFragmentInMain(fragment: Fragment, backStack:String? = null) {
            mainActivity?.let {
                it.supportFragmentManager
                    .beginTransaction()
                    .add(R.id.mainFrame, fragment)
                    .addToBackStack(backStack)
                    .commit()
            }
        }
    }
    //view
    private lateinit var binding : ActivityMainBinding

    //back press
    private var mBackWait = 0L

    //viewModel
    private val exerciseVM by viewModels<ExerciseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Fragment 변경, 하단 탭 숨김, 프로그레스 등에 사용
        mainActivity = this

        //initDB
        exerciseVM.initRoomDB(applicationContext, "test_db_name")//TODO 나중에 firebase uid

        //view
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.pager.adapter = ScreenSlidePagerAdapter(this)

        //init tab navigation bar
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
        val count = supportFragmentManager.fragments.count()
        var name = supportFragmentManager.fragments.last()::class.java.simpleName
        if (name == "SupportRequestManagerFragment")
            supportFragmentManager.fragments[count-2]?.let {
                name = it::class.java.simpleName
            }

        Log.d(TAG, "onBackPressed: count == $count")
        Log.d(TAG, "onBackPressed: name == $name")

//        //하단 네비게이션 바가 표시되는 곳은 프레그먼트 하나만 있을 때
//        if (count <= 2) showBNV(true)
//        else showBNV(false)
//
        when(name){
            CalenderFragment.TAG, GraphFragment.TAG,
            MainFragment.TAG, RankingFragment.TAG, SettingFragment.TAG -> {
                if((System.currentTimeMillis()-mBackWait) >= 2000){
                    Toast.makeText(applicationContext, getString(R.string.back_pressed_description), Toast.LENGTH_SHORT).show()
                    mBackWait = System.currentTimeMillis()
                } else {
                    moveTaskToBack(true)
                    finishAndRemoveTask()
                    System.exit(0)
                }
            }
            else -> super.onBackPressed()
        }
//
//        //키보드 hide
//        FunctionUtils.dismissKeyboard(this)


//        if (binding.pager.currentItem == 0) {
//            super.onBackPressed()
//        } else {
//            binding.pager.currentItem = binding.pager.currentItem - 1
//        }
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