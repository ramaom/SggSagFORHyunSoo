package com.sopt.appjam_sggsag.Adapter

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class NonSwipeableViewPager : ViewPager {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        // Never allow swiping to switch between pages
        return false
    }

/* 이 함수를 MainActivity에서 호출하는 방법?
    var swipeLock : Boolean = true

    fun lockSwipe() {
        swipeLock = true
    }

    fun unlockSwipe() {
        swipeLock = false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (swipeLock) return false
        else false
    }

*/

}