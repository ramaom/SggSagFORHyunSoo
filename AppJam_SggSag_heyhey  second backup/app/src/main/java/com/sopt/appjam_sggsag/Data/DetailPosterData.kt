package com.sopt.appjam_sggsag.Data

data class DetailPosterData(
    //서버에서 받은 거 Int인데 망가질까 무서워ㅜㅜㅜ
    val posterIdx : Int,// = counter++,
    val categoryIdx : Int,
    val photoUrl: String,
    val posterName : String,
    val posterRegDate : String,
    val posterStartDate: String,
    val posterEndDate: String,
    val posterWebsite: String,
    val isSeek : Int,
    val outline : String,
    val target : String,
    val period : String,
    val benefit : String,
    val documentDate : String,
    val announceDate1 : String,
    val announceDate2 : String,
    val finalAnnounceDate : String,
    val interviewDate : String
)
//{
//    companion object {
//        private var counter = 0
//    }
//}