package com.dw.artyou.helper

import com.dw.artyou.models.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("objects/public/{object_id}")
    fun getObjectDetail(@Path("object_id") version: String): Call<ObjectDetailModel>

    @GET("users")
    fun getUserInfo(): Call<UserInfoModel>

    @GET("users/segments")
    fun getUserSegment(): Call<UserSegmentModel>

    @GET("storyes/public")
    fun getStoriesHome(): Call<StoryesListModel>

    @GET("experiences/{object_id}")
    fun getExperiences(@Path("object_id") version: String): Call<ExperienceModel>

}