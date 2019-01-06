package com.sopt.appjam_sggsag.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sopt.appjam_sggsag.R

class MyPageFragment:Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myPageFragment : View = inflater!!.inflate(R.layout.fragment_my_page, container, false)
        return myPageFragment
    }

    companion object {
        private var instance : MyPageFragment? = null
        @Synchronized
        fun getInstance() : MyPageFragment {
            if (instance == null){
                instance = MyPageFragment()
            }
            return instance!!
        }
    }
}