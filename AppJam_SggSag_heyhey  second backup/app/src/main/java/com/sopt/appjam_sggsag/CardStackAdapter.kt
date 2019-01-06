package com.sopt.appjam_sggsag

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.sopt.appjam_sggsag.Data.DetailPosterData


class CardStackAdapter(
//    private var posters: List<DetailPosterData> = emptyList()
//    var posters: List<DetailPosterData> = emptyList()
    var posters: ArrayList<DetailPosterData>
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {
    //    private var homeFragmentView: View? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
//        homeFragmentView=inflater!!.inflate(R.layout.fragment_home,parent,false)
        return ViewHolder(
            inflater.inflate(R.layout.item_spot, parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = posters[position]
        holder.name.text = "${spot.posterIdx}. ${spot.photoUrl}"
        holder.city.text = spot.outline
        Glide.with(holder.image)
            .load(spot.photoUrl)
            .into(holder.image)
        //for fix error of background of second tab->now it's not error
        //우선 item_spot에서 FrameLayout끼리 있는 곳에 놔줘야해. CardView보다 텍스트 상 위에 놓으면 View상으로 밑면에 놓이게 돼.
        //FrameLayout있는 곳에 second tab을 놔두고, 이게 라이브러리 내부 로직에 있지 않으므로 다른 itme_spot안의 view들처럼 기본적으로 안 보이지 않아.
        //그러니까 INVISIBLE 처리해주고, 클릭할 때만 VISIBLE해주는 것으로. 그리고 왼쪽을 누르면 다시 INVISIBLE.
        //bringToFront가 holder.image에만 먹히고, holder.name, holer.city에 안 먹는 이유, holer.cardContentBackground에 제대로 안 먹는 이유는 미지수
        //일부만 잘려서 움직이던데 같이 엮인 TextView 및 음수 margin값 때문일 것 같다만 이 부분은 실험 안 해봄.
        holder.tmpcontent1.visibility=View.INVISIBLE
        holder.tmpcontent2.visibility=View.INVISIBLE
        holder.cardContentBackground.visibility=View.INVISIBLE

        //for card information
        holder.tmpcontent1.text = spot.posterName
        holder.tmpcontent2.text = spot.posterName

        var widthOfCard=holder.image.width
        var xAtDown : Float
        var xAtUp :Float
//        var isClick : Boolean = false
        holder.itemView.setOnTouchListener { v, event ->
            when (event.action) {
                //true로 하면 다음으로 내려간다. false를 하면 거기서 끝나버려
                //눌렀다가 바로 떼려해도 미세하게 움직이면 그걸 누른채 움직이는 걸로 감지하는 동시에 떼는 걸로 감지함
                //그런데 작정하고 움직이면 떼는 것을 감지 안 함
                //ACTION_DOWN이 없으면 ACTION_UP이 감지가 안 되네
                //isClick 초기화는 안 해도 되는 듯
                MotionEvent.ACTION_DOWN-> {
                    xAtDown = event.getX()
                    Toast.makeText(v.context,spot.posterIdx.toString(),Toast.LENGTH_SHORT).show()
                    return@setOnTouchListener true
                }
                /*
                MotionEvent.ACTION_MOVE->{
                    Toast.makeText(v.context,"누른 채 움직일 때",Toast.LENGTH_SHORT).show()
                    return@setOnTouchListener false
                }
                */
                MotionEvent.ACTION_UP->{//클릭
//                    isClick = true
                    xAtUp = event.getX()
                    if (xAtUp<=widthOfCard/2) {//car의 왼쪽 클릭
                        holder.tmpcontent1.visibility=View.INVISIBLE
                        holder.tmpcontent2.visibility=View.INVISIBLE
                        holder.cardContentBackground.visibility=View.INVISIBLE
                    }
                    else{//card의 오른쪽 클릭
                        holder.tmpcontent1.visibility=View.VISIBLE
                        holder.tmpcontent2.visibility=View.VISIBLE
                        holder.cardContentBackground.visibility=View.VISIBLE
                    }
                    return@setOnTouchListener true
                }
                else -> {
                    return@setOnTouchListener false
                }
            }
//            v?.onTouchEvent(event) ?: true
        }
    }

    override fun getItemCount(): Int {
        return posters.size
    }

    fun setSpots(posters: ArrayList<DetailPosterData>) {
        this.posters = posters
    }

    fun getSpots(): ArrayList<DetailPosterData> {
        return posters
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var city: TextView = view.findViewById(R.id.item_city)
        var image: ImageView = view.findViewById(R.id.item_image)
        //for card content
        var tmpcontent1 : TextView =view.findViewById(R.id.tmp_text_1)
        var tmpcontent2 : TextView =view.findViewById(R.id.tmp_text_2)
        var cardContentBackground : ImageView =view.findViewById((R.id.IV_card_content))
//        var viewForCardWidth : FrameLayout? = view.findViewById(R.posterIdx.FL_for_width)
    }
}