package com.sopt.appjam_sggsag.Fragment

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import com.sopt.appjam_sggsag.Network.ApplicationController
import com.sopt.appjam_sggsag.R
import com.sopt.appjam_sggsag.Data.DetailPosterData
import com.sopt.appjam_sggsag.Post.PostPosterListResponse
import com.sopt.appjam_sggsag.CardStackAdapter
import com.sopt.appjam_sggsag.Data.PosterData
import com.sopt.appjam_sggsag.SpotDiffCallback
import com.sopt.appjam_sggsag.Network.NetworkService
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), CardStackListener {
    private val drawerLayout by lazy { drawer_layout }
    private var cardStackView: CardStackView? = null

    private val manager by lazy { CardStackLayoutManager(context, this) }
    //본래 선언
    //    private val adapter by lazy { CardStackAdapter(createPosters()) }
    lateinit var adapter : CardStackAdapter
    private var homeFragmentView: View? = null

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }
    var inputPosterData : PosterData? =null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeFragmentView = inflater!!.inflate(R.layout.fragment_home, container, false)

//        setupNavigation()
        return homeFragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getPosterListResponse()
        Thread.sleep(1000)
        //by lazy에서 lateinit으로 변경함에 따라 adapter 초기화하기 위함
        adapter= CardStackAdapter(createPosters())
        setupCardStackView()//CardStackAdapter가 처음 쓰이는 부분
        setupButton()
    }
/*
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }
*/

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        //스와이프 시 반응
        Toast.makeText(this.context, "eeeeeeeeeee", Toast.LENGTH_SHORT).show()
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }

/*
    private fun setupNavigation() {
        // Toolbar
        val toolbar = findViewById<Toolbar>(R.posterIdx.toolbar)
        setSupportActionBar(toolbar)

        // DrawerLayout
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        actionBarDrawerToggle.syncState()
        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        // NavigationView
        val navigationView = findViewById<NavigationView>(R.posterIdx.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.posterIdx.reload -> reload()
                R.posterIdx.add_spot_to_first -> addFirst(1)
                R.posterIdx.add_spot_to_last -> addLast(1)
                R.posterIdx.remove_spot_from_first -> removeFirst(1)
                R.posterIdx.remove_spot_from_last -> removeLast(1)
                R.posterIdx.replace_first_spot -> replace()
                R.posterIdx.swap_first_for_last -> swap()
            }
            drawerLayout.closeDrawers()
            true
        }
    }
*/

    private fun setupCardStackView() {
        cardStackView = homeFragmentView!!.find(R.id.card_stack_view)
//        LeftButtonView=homeFragmentView!!.find(R.posterIdx.cs_view_for_progress)
//        manager=CardStackLayoutManager(context, this)
//        adapter=CardStackAdapter(createPosters())
        initialize()
    }

    private fun setupButton() {
        val skip: FloatingActionButton = homeFragmentView!!.find(R.id.skip_button)
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(200)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView?.swipe()
        }

        val like: FloatingActionButton = homeFragmentView!!.find(R.id.like_button)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(200)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView?.swipe()
        }
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)

        cardStackView!!.layoutManager = manager
        cardStackView!!.adapter = adapter
//        LeftButtonView!!.adapter = lbadapter
        cardStackView!!.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
//        setupProgressView()
//        setupCardTab()
    }

/*
    private fun setupProgressView(){
        var pvReverse: View = homeFragmentView!!.find(R.posterIdx.pv_reverse)
        cardStackView!!.bringToFront()
        pvReverse.setOnClickListener {
            pvReverse.bringToFront()
            Toast.makeText(activity,"눌리는거 맞냐?",Toast.LENGTH_SHORT).show()
        }

        var pvSkip: View =homeFragmentView!!.find(R.posterIdx.pv_skip)
        pvSkip.setOnClickListener {
            Toast.makeText(activity,"아아아아아아앙",Toast.LENGTH_LONG).show()
        }
    }
*/

/*
    private fun setupCardTab(){
        var viewForCardSize : View = homeFragmentView!!.find(R.posterIdx.button_container)
        var widthOfCard : Int= viewForCardSize.width
//        var widthOfCard : Int= viewForCardSize.layoutParams.width
        homeFragmentView!!.setOnTouchListener{v,event->
            when (event?.action){
                MotionEvent.ACTION_BUTTON_PRESS->{
                    var x :  Float =event.getX()
                    if (x<=widthOfCard/2){
                        Toast.makeText(v.context,"이것도 왼쪽일까",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(v.context,"제발 오른쪽",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            v?.onTouchEvent(event)?:true
        }
    }
*/

    /*
    private fun paginate() {
        val old = adapter.getSpots()
        val new = old.plus(createPosters())
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
    */

    private fun paginate() {
        val old = adapter.getSpots()
//        val new = old.plus(createPosters())
        val new = old.plus(createPosters())
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new as ArrayList<DetailPosterData>)
        result.dispatchUpdatesTo(adapter)
    }

/*
    private fun reload() {
        val old = adapter.getSpots()
        val new = createPosters()
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
*/
/*
    private fun addFirst(size: Int) {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                add(manager.topPosition, createSpot())
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
*/
/*
    private fun addLast(size: Int) {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            addAll(List(size) { createSpot() })
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
*/
/*
    private fun removeFirst(size: Int) {
        if (adapter.getSpots().isEmpty()) {
            return
        }

        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(manager.topPosition)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
*/
/*
    private fun removeLast(size: Int) {
        if (adapter.getSpots().isEmpty()) {
            return
        }

        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(this.size - 1)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
*/
/*
    private fun replace() {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            removeAt(manager.topPosition)
            add(manager.topPosition, createSpot())
        }
        adapter.setSpots(new)
        adapter.notifyItemChanged(manager.topPosition)
    }
*/
/*
    private fun swap() {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            val first = removeAt(manager.topPosition)
            val last = removeAt(this.size - 1)
            add(manager.topPosition, last)
            add(first)
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
*/
/*
    private fun createSpot(): Spot {
        return Spot(
            name = "Yasaka Shrine",
            city = "Kyoto",
            photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800"
        )
    }
*/

    private fun getPosterListResponse() {
        Log.e("1111111111111111", "1111111111111111111111")
        //바로 아래 라인에서 터진다
        val postPosterListResponse: Call<PostPosterListResponse> =
            networkService.postPosterResponse("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJEb0lUU09QVCIsInVzZXJfaWR4IjoxfQ.5lCvAqnzYP4-2pFx1KTgLVOxYzBQ6ygZvkx5jKCFM08")
        //networkService.postPosterResponse(SharedPreferenceController.getAuthorization(this.context!!))
        //Log.e("authcheck",SharedPreferenceController.getAuthorization((this.context!!)))
        Log.e("2222222222222","2222222222222")
        Thread.sleep(300)
        postPosterListResponse.enqueue(object : Callback<PostPosterListResponse> {
            override fun onFailure(call: Call<PostPosterListResponse>, t: Throwable) {
                Log.e("333333Poster call fail", t.toString())
            }
            override fun onResponse(
                call: Call<PostPosterListResponse>,
                response: Response<PostPosterListResponse>
            ) {
                Log.e("3333333" ,"33333333")
                if (response.isSuccessful) {
                    //?.의 오른쪽이 함수이면 null safe operator
                    //?.의 오른쪽이 변수/상수이면 null이 될 수 있는 타입 표시
                    if (response.body()?.data!=null) {
                        Log.e("pleaseeeeeeeeeeeee", "can you come to here")
                        inputPosterData = response.body()!!.data
                        Thread.sleep(300)
                    }
                }
            }
        })
    }

    private fun createPosters(): ArrayList<DetailPosterData> {
        val posters = ArrayList<DetailPosterData>()
//        Log.e(inputPosterData!!.posters[0].photoUrl, inputPosterData!!.posters[0].photoUrl.toString())
//        Log.e("posterIdx",inputPosterData!!.posters[0].posterIdx.toString())
        Log.e("imageeeee", "eeeeeeeeeeeee")
        //1번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[0].posterIdx,
                categoryIdx = inputPosterData!!.posters[0].categoryIdx,
                photoUrl = inputPosterData!!.posters[0].photoUrl,
                posterName = inputPosterData!!.posters[0].posterName,
                posterRegDate = inputPosterData!!.posters[0].posterRegDate,
                posterStartDate = inputPosterData!!.posters[0].posterStartDate,
                posterEndDate = inputPosterData!!.posters[0].posterEndDate,
                posterWebsite = inputPosterData!!.posters[0].posterWebsite,
                isSeek = inputPosterData!!.posters[0].isSeek,
                outline = inputPosterData!!.posters[0].outline,
                target = inputPosterData!!.posters[0].target,
                period = inputPosterData!!.posters[0].period,
                benefit = inputPosterData!!.posters[0].benefit,
                announceDate1 = inputPosterData!!.posters[0].announceDate1,
                announceDate2 = inputPosterData!!.posters[0].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[0].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[0].interviewDate,
                documentDate = inputPosterData!!.posters[0].documentDate
            )
        )
        //2번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[1].posterIdx,
                categoryIdx = inputPosterData!!.posters[1].categoryIdx,
                photoUrl = inputPosterData!!.posters[1].photoUrl,
                posterName = inputPosterData!!.posters[1].posterName,
                posterRegDate = inputPosterData!!.posters[1].posterRegDate,
                posterStartDate = inputPosterData!!.posters[1].posterStartDate,
                posterEndDate = inputPosterData!!.posters[1].posterEndDate,
                posterWebsite = inputPosterData!!.posters[1].posterWebsite,
                isSeek = inputPosterData!!.posters[1].isSeek,
                outline = inputPosterData!!.posters[1].outline,
                target = inputPosterData!!.posters[1].target,
                period = inputPosterData!!.posters[1].period,
                benefit = inputPosterData!!.posters[1].benefit,
                announceDate1 = inputPosterData!!.posters[1].announceDate1,
                announceDate2 = inputPosterData!!.posters[1].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[1].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[1].interviewDate,
                documentDate = inputPosterData!!.posters[1].documentDate
            )
        )

        //3번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[2].posterIdx,
                categoryIdx = inputPosterData!!.posters[2].categoryIdx,
                photoUrl = inputPosterData!!.posters[2].photoUrl,
                posterName = inputPosterData!!.posters[2].posterName,
                posterRegDate = inputPosterData!!.posters[2].posterRegDate,
                posterStartDate = inputPosterData!!.posters[2].posterStartDate,
                posterEndDate = inputPosterData!!.posters[2].posterEndDate,
                posterWebsite = inputPosterData!!.posters[2].posterWebsite,
                isSeek = inputPosterData!!.posters[2].isSeek,
                outline = inputPosterData!!.posters[2].outline,
                target = inputPosterData!!.posters[2].target,
                period = inputPosterData!!.posters[2].period,
                benefit = inputPosterData!!.posters[2].benefit,
                announceDate1 = inputPosterData!!.posters[2].announceDate1,
                announceDate2 = inputPosterData!!.posters[2].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[2].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[2].interviewDate,
                documentDate = inputPosterData!!.posters[2].documentDate
            )
        )

        //4번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[3].posterIdx,
                categoryIdx = inputPosterData!!.posters[3].categoryIdx,
                photoUrl = inputPosterData!!.posters[3].photoUrl,
                posterName = inputPosterData!!.posters[3].posterName,
                posterRegDate = inputPosterData!!.posters[3].posterRegDate,
                posterStartDate = inputPosterData!!.posters[3].posterStartDate,
                posterEndDate = inputPosterData!!.posters[3].posterEndDate,
                posterWebsite = inputPosterData!!.posters[3].posterWebsite,
                isSeek = inputPosterData!!.posters[3].isSeek,
                outline = inputPosterData!!.posters[3].outline,
                target = inputPosterData!!.posters[3].target,
                period = inputPosterData!!.posters[3].period,
                benefit = inputPosterData!!.posters[3].benefit,
                announceDate1 = inputPosterData!!.posters[3].announceDate1,
                announceDate2 = inputPosterData!!.posters[3].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[3].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[3].interviewDate,
                documentDate = inputPosterData!!.posters[3].documentDate
            )
        )

        //5번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[4].posterIdx,
                categoryIdx = inputPosterData!!.posters[4].categoryIdx,
                photoUrl = inputPosterData!!.posters[4].photoUrl,
                posterName = inputPosterData!!.posters[4].posterName,
                posterRegDate = inputPosterData!!.posters[4].posterRegDate,
                posterStartDate = inputPosterData!!.posters[4].posterStartDate,
                posterEndDate = inputPosterData!!.posters[4].posterEndDate,
                posterWebsite = inputPosterData!!.posters[4].posterWebsite,
                isSeek = inputPosterData!!.posters[4].isSeek,
                outline = inputPosterData!!.posters[4].outline,
                target = inputPosterData!!.posters[4].target,
                period = inputPosterData!!.posters[4].period,
                benefit = inputPosterData!!.posters[4].benefit,
                announceDate1 = inputPosterData!!.posters[4].announceDate1,
                announceDate2 = inputPosterData!!.posters[4].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[4].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[4].interviewDate,
                documentDate = inputPosterData!!.posters[4].documentDate
            )
        )

        //6번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[5].posterIdx,
                categoryIdx = inputPosterData!!.posters[5].categoryIdx,
                photoUrl = inputPosterData!!.posters[5].photoUrl,
                posterName = inputPosterData!!.posters[5].posterName,
                posterRegDate = inputPosterData!!.posters[5].posterRegDate,
                posterStartDate = inputPosterData!!.posters[5].posterStartDate,
                posterEndDate = inputPosterData!!.posters[5].posterEndDate,
                posterWebsite = inputPosterData!!.posters[5].posterWebsite,
                isSeek = inputPosterData!!.posters[5].isSeek,
                outline = inputPosterData!!.posters[5].outline,
                target = inputPosterData!!.posters[5].target,
                period = inputPosterData!!.posters[5].period,
                benefit = inputPosterData!!.posters[5].benefit,
                announceDate1 = inputPosterData!!.posters[5].announceDate1,
                announceDate2 = inputPosterData!!.posters[5].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[5].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[5].interviewDate,
                documentDate = inputPosterData!!.posters[5].documentDate
            )
        )

        //7번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[6].posterIdx,
                categoryIdx = inputPosterData!!.posters[6].categoryIdx,
                photoUrl = inputPosterData!!.posters[6].photoUrl,
                posterName = inputPosterData!!.posters[6].posterName,
                posterRegDate = inputPosterData!!.posters[6].posterRegDate,
                posterStartDate = inputPosterData!!.posters[6].posterStartDate,
                posterEndDate = inputPosterData!!.posters[6].posterEndDate,
                posterWebsite = inputPosterData!!.posters[6].posterWebsite,
                isSeek = inputPosterData!!.posters[6].isSeek,
                outline = inputPosterData!!.posters[6].outline,
                target = inputPosterData!!.posters[6].target,
                period = inputPosterData!!.posters[6].period,
                benefit = inputPosterData!!.posters[6].benefit,
                announceDate1 = inputPosterData!!.posters[6].announceDate1,
                announceDate2 = inputPosterData!!.posters[6].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[6].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[6].interviewDate,
                documentDate = inputPosterData!!.posters[6].documentDate
            )
        )

        //8번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[7].posterIdx,
                categoryIdx = inputPosterData!!.posters[7].categoryIdx,
                photoUrl = inputPosterData!!.posters[7].photoUrl,
                posterName = inputPosterData!!.posters[7].posterName,
                posterRegDate = inputPosterData!!.posters[7].posterRegDate,
                posterStartDate = inputPosterData!!.posters[7].posterStartDate,
                posterEndDate = inputPosterData!!.posters[7].posterEndDate,
                posterWebsite = inputPosterData!!.posters[7].posterWebsite,
                isSeek = inputPosterData!!.posters[7].isSeek,
                outline = inputPosterData!!.posters[7].outline,
                target = inputPosterData!!.posters[7].target,
                period = inputPosterData!!.posters[7].period,
                benefit = inputPosterData!!.posters[7].benefit,
                announceDate1 = inputPosterData!!.posters[7].announceDate1,
                announceDate2 = inputPosterData!!.posters[7].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[7].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[7].interviewDate,
                documentDate = inputPosterData!!.posters[7].documentDate
            )
        )

        //9번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[8].posterIdx,
                categoryIdx = inputPosterData!!.posters[8].categoryIdx,
                photoUrl = inputPosterData!!.posters[8].photoUrl,
                posterName = inputPosterData!!.posters[8].posterName,
                posterRegDate = inputPosterData!!.posters[8].posterRegDate,
                posterStartDate = inputPosterData!!.posters[8].posterStartDate,
                posterEndDate = inputPosterData!!.posters[8].posterEndDate,
                posterWebsite = inputPosterData!!.posters[8].posterWebsite,
                isSeek = inputPosterData!!.posters[8].isSeek,
                outline = inputPosterData!!.posters[8].outline,
                target = inputPosterData!!.posters[8].target,
                period = inputPosterData!!.posters[8].period,
                benefit = inputPosterData!!.posters[8].benefit,
                announceDate1 = inputPosterData!!.posters[8].announceDate1,
                announceDate2 = inputPosterData!!.posters[8].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[8].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[8].interviewDate,
                documentDate = inputPosterData!!.posters[8].documentDate
            )
        )

        //10번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[9].posterIdx,
                categoryIdx = inputPosterData!!.posters[9].categoryIdx,
                photoUrl = inputPosterData!!.posters[9].photoUrl,
                posterName = inputPosterData!!.posters[9].posterName,
                posterRegDate = inputPosterData!!.posters[9].posterRegDate,
                posterStartDate = inputPosterData!!.posters[9].posterStartDate,
                posterEndDate = inputPosterData!!.posters[9].posterEndDate,
                posterWebsite = inputPosterData!!.posters[9].posterWebsite,
                isSeek = inputPosterData!!.posters[9].isSeek,
                outline = inputPosterData!!.posters[9].outline,
                target = inputPosterData!!.posters[9].target,
                period = inputPosterData!!.posters[9].period,
                benefit = inputPosterData!!.posters[9].benefit,
                announceDate1 = inputPosterData!!.posters[9].announceDate1,
                announceDate2 = inputPosterData!!.posters[9].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[9].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[9].interviewDate,
                documentDate = inputPosterData!!.posters[9].documentDate
            )
        )
        return posters
    }

/*
    private fun createPosters(): ArrayList<DetailPosterData> {
        val posters = ArrayList<DetailPosterData>()
        Log.e("imageeeee", "eeeeeeeeeeeee")
        //1번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 1,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"
            )
        )

        //2번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 1,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"            )
        )

        //3번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 1,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"            )
        )

        //4번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 1,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"            )
        )

        //5번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 1,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"            )
        )

        //6번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 1,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"            )
        )

        //7번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 1,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"            )
        )

        //8번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 1,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"            )
        )

        //9번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 1,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"
            )
        )

        //10번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 1,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"            )
        )
        return posters
    }
*/
}
