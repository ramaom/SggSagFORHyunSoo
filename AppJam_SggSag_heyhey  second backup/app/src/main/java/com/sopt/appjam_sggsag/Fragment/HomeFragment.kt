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
    //    private var LeftButtonView: CardStackView?= null
    private val manager by lazy { CardStackLayoutManager(context, this) }
    private val adapter by lazy { CardStackAdapter(createPosters()) }
    //    private val lbadapter by lazy  {LeftButtonAdpater(createPosters())}
//    private var manager: CardStackLayoutManager? = null
//    private val adapter:CardStackAdapter = CardStackAdapter(createPosters())
    private var homeFragmentView: View? = null

    //For Server Communication
    val WRITE_FRAGMENT_REQUESET_CODE = 1000
    val dataList: ArrayList<DetailPosterData> by lazy {
        ArrayList<DetailPosterData>()
    }
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }
    var inputPosterData : PosterData? =null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeFragmentView = inflater!!.inflate(R.layout.fragment_home, container, false)
        getPosterListResponse()
//        setupNavigation()
        return homeFragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        getPosterListResponse()
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
//        val jsonObject: JSONObject = JSONObject()
//        val gsonObject : JSONObject= JsonParser().parse(jsonObject.toString()) as JSONObject
        Log.e("1111111111111111", "1111111111111111111111")
        //바로 아래 라인에서 터진다
        val postPosterListResponse: Call<PostPosterListResponse> =
            networkService.postPosterResponse("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJEb0lUU09QVCIsInVzZXJfaWR4IjoxfQ.5lCvAqnzYP4-2pFx1KTgLVOxYzBQ6ygZvkx5jKCFM08")//,gsonObject)
        Log.e("2222222222222","2222222222222")
        postPosterListResponse.enqueue(object : Callback<PostPosterListResponse> {
            override fun onFailure(call: Call<PostPosterListResponse>, t: Throwable) {
                Log.e("iiiiiiiiiiiiiiiiiiiiPoster call fail", t.toString())
            }
            override fun onResponse(
                call: Call<PostPosterListResponse>,
                response: Response<PostPosterListResponse>
            ) {
                Log.e("cutedohee" ,"cute jinee")
                if (response.isSuccessful) {
                    Log.e("pleaseeeeeeeeeeeee", "can you come to here")
                    inputPosterData =response.body()!!.data
                }
            }
        })
    }


    private fun createPosters(): ArrayList<DetailPosterData> {
        val posters = ArrayList<DetailPosterData>()
//        Log.e(inputPosterData!!.posters[0].photoUrl, inputPosterData!!.posters[0].photoUrl.toString())
        Log.e("imageeeee", "eeeeeeeeeeeee")
        //1번 CARD
        posters.add(
            DetailPosterData(
                inputPosterData!!.posters[0].posterIdx,
                inputPosterData!!.posters[0].categoryIdx,
                inputPosterData!!.posters[0].photoUrl,
                inputPosterData!!.posters[0].posterName,
                inputPosterData!!.posters[0].posterRegDate,
                inputPosterData!!.posters[0].posterStartDate,
                inputPosterData!!.posters[0].posterEndDate,
                inputPosterData!!.posters[0].posterWebsite,
                inputPosterData!!.posters[0].isSeek,
                inputPosterData!!.posters[0].outline,
                inputPosterData!!.posters[0].target,
                inputPosterData!!.posters[0].period,
                inputPosterData!!.posters[0].benefit,
                inputPosterData!!.posters[0].documentDate,
                inputPosterData!!.posters[0].announceDate1,
                inputPosterData!!.posters[0].announceDate2,
                inputPosterData!!.posters[0].finalAnnounceDate,
                inputPosterData!!.posters[0].interviewDate
            )
        )
        //2번 CARD
        posters.add(
            DetailPosterData(
                inputPosterData!!.posters[1].posterIdx,
                inputPosterData!!.posters[1].categoryIdx,
                inputPosterData!!.posters[1].photoUrl,
                inputPosterData!!.posters[1].posterName,
                inputPosterData!!.posters[1].posterRegDate,
                inputPosterData!!.posters[1].posterStartDate,
                inputPosterData!!.posters[1].posterEndDate,
                inputPosterData!!.posters[1].posterWebsite,
                inputPosterData!!.posters[1].isSeek,
                inputPosterData!!.posters[1].outline,
                inputPosterData!!.posters[1].target,
                inputPosterData!!.posters[1].period,
                inputPosterData!!.posters[1].benefit,
                inputPosterData!!.posters[1].documentDate,
                inputPosterData!!.posters[1].announceDate1,
                inputPosterData!!.posters[1].announceDate2,
                inputPosterData!!.posters[1].finalAnnounceDate,
                inputPosterData!!.posters[1].interviewDate
            )
        )

        //3번 CARD
        posters.add(
            DetailPosterData(
                inputPosterData!!.posters[2].posterIdx,
                inputPosterData!!.posters[2].categoryIdx,
                inputPosterData!!.posters[2].photoUrl,
                inputPosterData!!.posters[2].posterName,
                inputPosterData!!.posters[2].posterRegDate,
                inputPosterData!!.posters[2].posterStartDate,
                inputPosterData!!.posters[2].posterEndDate,
                inputPosterData!!.posters[2].posterWebsite,
                inputPosterData!!.posters[2].isSeek,
                inputPosterData!!.posters[2].outline,
                inputPosterData!!.posters[2].target,
                inputPosterData!!.posters[2].period,
                inputPosterData!!.posters[2].benefit,
                inputPosterData!!.posters[2].documentDate,
                inputPosterData!!.posters[2].announceDate1,
                inputPosterData!!.posters[2].announceDate2,
                inputPosterData!!.posters[2].finalAnnounceDate,
                inputPosterData!!.posters[2].interviewDate
            )
        )

        //4번 CARD
        posters.add(
            DetailPosterData(
                inputPosterData!!.posters[3].posterIdx,
                inputPosterData!!.posters[3].categoryIdx,
                inputPosterData!!.posters[3].photoUrl,
                inputPosterData!!.posters[3].posterName,
                inputPosterData!!.posters[3].posterRegDate,
                inputPosterData!!.posters[3].posterStartDate,
                inputPosterData!!.posters[3].posterEndDate,
                inputPosterData!!.posters[3].posterWebsite,
                inputPosterData!!.posters[3].isSeek,
                inputPosterData!!.posters[3].outline,
                inputPosterData!!.posters[3].target,
                inputPosterData!!.posters[3].period,
                inputPosterData!!.posters[3].benefit,
                inputPosterData!!.posters[3].documentDate,
                inputPosterData!!.posters[3].announceDate1,
                inputPosterData!!.posters[3].announceDate2,
                inputPosterData!!.posters[3].finalAnnounceDate,
                inputPosterData!!.posters[3].interviewDate
            )
        )

        //5번 CARD
        posters.add(
            DetailPosterData(
                inputPosterData!!.posters[4].posterIdx,
                inputPosterData!!.posters[4].categoryIdx,
                inputPosterData!!.posters[4].photoUrl,
                inputPosterData!!.posters[4].posterName,
                inputPosterData!!.posters[4].posterRegDate,
                inputPosterData!!.posters[4].posterStartDate,
                inputPosterData!!.posters[4].posterEndDate,
                inputPosterData!!.posters[4].posterWebsite,
                inputPosterData!!.posters[4].isSeek,
                inputPosterData!!.posters[4].outline,
                inputPosterData!!.posters[4].target,
                inputPosterData!!.posters[4].period,
                inputPosterData!!.posters[4].benefit,
                inputPosterData!!.posters[4].documentDate,
                inputPosterData!!.posters[4].announceDate1,
                inputPosterData!!.posters[4].announceDate2,
                inputPosterData!!.posters[4].finalAnnounceDate,
                inputPosterData!!.posters[4].interviewDate
            )
        )

        //6번 CARD
        posters.add(
            DetailPosterData(
                inputPosterData!!.posters[5].posterIdx,
                inputPosterData!!.posters[5].categoryIdx,
                inputPosterData!!.posters[5].photoUrl,
                inputPosterData!!.posters[5].posterName,
                inputPosterData!!.posters[5].posterRegDate,
                inputPosterData!!.posters[5].posterStartDate,
                inputPosterData!!.posters[5].posterEndDate,
                inputPosterData!!.posters[5].posterWebsite,
                inputPosterData!!.posters[5].isSeek,
                inputPosterData!!.posters[5].outline,
                inputPosterData!!.posters[5].target,
                inputPosterData!!.posters[5].period,
                inputPosterData!!.posters[5].benefit,
                inputPosterData!!.posters[5].documentDate,
                inputPosterData!!.posters[5].announceDate1,
                inputPosterData!!.posters[5].announceDate2,
                inputPosterData!!.posters[5].finalAnnounceDate,
                inputPosterData!!.posters[5].interviewDate
            )
        )

        //7번 CARD
        posters.add(
            DetailPosterData(
                inputPosterData!!.posters[6].posterIdx,
                inputPosterData!!.posters[6].categoryIdx,
                inputPosterData!!.posters[6].photoUrl,
                inputPosterData!!.posters[6].posterName,
                inputPosterData!!.posters[6].posterRegDate,
                inputPosterData!!.posters[6].posterStartDate,
                inputPosterData!!.posters[6].posterEndDate,
                inputPosterData!!.posters[6].posterWebsite,
                inputPosterData!!.posters[6].isSeek,
                inputPosterData!!.posters[6].outline,
                inputPosterData!!.posters[6].target,
                inputPosterData!!.posters[6].period,
                inputPosterData!!.posters[6].benefit,
                inputPosterData!!.posters[6].documentDate,
                inputPosterData!!.posters[6].announceDate1,
                inputPosterData!!.posters[6].announceDate2,
                inputPosterData!!.posters[6].finalAnnounceDate,
                inputPosterData!!.posters[6].interviewDate
            )
        )

        //8번 CARD
        posters.add(
            DetailPosterData(
                inputPosterData!!.posters[7].posterIdx,
                inputPosterData!!.posters[7].categoryIdx,
                inputPosterData!!.posters[7].photoUrl,
                inputPosterData!!.posters[7].posterName,
                inputPosterData!!.posters[7].posterRegDate,
                inputPosterData!!.posters[7].posterStartDate,
                inputPosterData!!.posters[7].posterEndDate,
                inputPosterData!!.posters[7].posterWebsite,
                inputPosterData!!.posters[7].isSeek,
                inputPosterData!!.posters[7].outline,
                inputPosterData!!.posters[7].target,
                inputPosterData!!.posters[7].period,
                inputPosterData!!.posters[7].benefit,
                inputPosterData!!.posters[7].documentDate,
                inputPosterData!!.posters[7].announceDate1,
                inputPosterData!!.posters[7].announceDate2,
                inputPosterData!!.posters[7].finalAnnounceDate,
                inputPosterData!!.posters[7].interviewDate
            )
        )

        //9번 CARD
        posters.add(
            DetailPosterData(
                inputPosterData!!.posters[8].posterIdx,
                inputPosterData!!.posters[8].categoryIdx,
                inputPosterData!!.posters[8].photoUrl,
                inputPosterData!!.posters[8].posterName,
                inputPosterData!!.posters[8].posterRegDate,
                inputPosterData!!.posters[8].posterStartDate,
                inputPosterData!!.posters[8].posterEndDate,
                inputPosterData!!.posters[8].posterWebsite,
                inputPosterData!!.posters[8].isSeek,
                inputPosterData!!.posters[8].outline,
                inputPosterData!!.posters[8].target,
                inputPosterData!!.posters[8].period,
                inputPosterData!!.posters[8].benefit,
                inputPosterData!!.posters[8].documentDate,
                inputPosterData!!.posters[8].announceDate1,
                inputPosterData!!.posters[8].announceDate2,
                inputPosterData!!.posters[8].finalAnnounceDate,
                inputPosterData!!.posters[8].interviewDate
            )
        )

        //10번 CARD
        posters.add(
            DetailPosterData(
                inputPosterData!!.posters[9].posterIdx,
                inputPosterData!!.posters[9].categoryIdx,
                inputPosterData!!.posters[9].photoUrl,
                inputPosterData!!.posters[9].posterName,
                inputPosterData!!.posters[9].posterRegDate,
                inputPosterData!!.posters[9].posterStartDate,
                inputPosterData!!.posters[9].posterEndDate,
                inputPosterData!!.posters[9].posterWebsite,
                inputPosterData!!.posters[9].isSeek,
                inputPosterData!!.posters[9].outline,
                inputPosterData!!.posters[9].target,
                inputPosterData!!.posters[9].period,
                inputPosterData!!.posters[9].benefit,
                inputPosterData!!.posters[9].documentDate,
                inputPosterData!!.posters[9].announceDate1,
                inputPosterData!!.posters[9].announceDate2,
                inputPosterData!!.posters[9].finalAnnounceDate,
                inputPosterData!!.posters[9].interviewDate
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
