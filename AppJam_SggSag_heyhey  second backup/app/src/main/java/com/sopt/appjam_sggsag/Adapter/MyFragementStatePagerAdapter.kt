package com.sopt.appjam_sggsag.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.sopt.appjam_sggsag.Fragment.CalendarFragment
import com.sopt.appjam_sggsag.Fragment.HomeFragment
import com.sopt.appjam_sggsag.Fragment.MyPageFragment

//import com.example.dohee.ssgsag.Fragment.CalendarFragment
//import com.example.dohee.ssgsag.Fragment.HomeFragment
//import com.example.dohee.ssgsag.Fragment.MyPageFragment


class MyFragmentStatePagerAdapter(fm: FragmentManager, val fCount: Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return MyPageFragment()
            1 -> {
                val homeFragment: Fragment = HomeFragment()
                return homeFragment
            }
            2 -> return CalendarFragment()
            else -> return null
        }
    }

    override fun getCount(): Int = fCount //return fCount
}