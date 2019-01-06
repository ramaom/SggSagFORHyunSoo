package com.sopt.appjam_sggsag

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.sopt.appjam_sggsag.Adapter.MyFragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureBottomNavigation()
    }

    private fun configureBottomNavigation() {
        vp_main_act_view_frag_pager.adapter = MyFragmentStatePagerAdapter(supportFragmentManager, 3) //3개를 고정시키겠다.
        vp_main_act_view_frag_pager.offscreenPageLimit = 3
        vp_main_act_view_frag_pager.setCurrentItem(1,true)
        // ViewPager와 Tablayout을 엮어줍니다!
        tl_top_navi_act_top_menu.setupWithViewPager(vp_main_act_view_frag_pager)
        //TabLayout에 붙일 layout을 찾아준 다음
        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.activity_top_navi_avtivity, null, false)
        //탭 하나하나 TabLayout에 연결시켜줍니다.
        tl_top_navi_act_top_menu.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_top_navi_my_page_tab) as RelativeLayout
        tl_top_navi_act_top_menu.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_top_navi_home_tab) as RelativeLayout
        tl_top_navi_act_top_menu.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.btn_top_navi_calendar_tab) as RelativeLayout

    }
}
