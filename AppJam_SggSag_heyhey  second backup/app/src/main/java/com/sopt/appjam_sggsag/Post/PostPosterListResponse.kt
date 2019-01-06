package com.sopt.appjam_sggsag.Post

import com.sopt.appjam_sggsag.Data.PosterData

data class PostPosterListResponse(
    val status : Int,
    val message :String,
    val data : PosterData
)