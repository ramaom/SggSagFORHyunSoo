package com.sopt.appjam_sggsag.Network

import com.google.gson.JsonObject
import com.sopt.appjam_sggsag.Post.PostPosterListResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NetworkService{
    //Poster_승완
    @POST("/posters/show")
    fun postPosterResponse(
        @Header("Authorization") token : String//,
//        @Header("Content-Type") content_type : JSONObject
//        @Body() body : JsonObject
    ): Call<PostPosterListResponse>
}