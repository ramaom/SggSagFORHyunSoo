package com.sopt.appjam_sggsag.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sopt.appjam_sggsag.R

class CalendarFragment:Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val calendarFragment : View = inflater!!.inflate(R.layout.fragment_calendar, container, false)
        return calendarFragment
    }

    companion object {
        private var instance : CalendarFragment? = null
        @Synchronized
        fun getInstance() : CalendarFragment {
            if (instance == null){
                instance = CalendarFragment()
            }
            return instance!!
        }
    }
}