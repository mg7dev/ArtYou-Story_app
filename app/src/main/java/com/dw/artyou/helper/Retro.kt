package com.designoweb.marketplace.subcontractor.activity.api

import android.content.Context
import com.dw.artyou.helper.ApiService
import com.dw.felgen_inserate.helperMethods.SessionManager
import com.facebook.FacebookSdk.getCacheDir
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Retro {
    companion object {
        var cacheSize = 20 * 1024 * 1024 // 10 MB

        var cache: Cache = Cache(getCacheDir(), cacheSize.toLong())
        var gson = GsonBuilder()
            .setLenient()
            .create()


        fun Api(context: Context): ApiService {
            // try {

            val client = OkHttpClient.Builder().cache(cache)
            val loggingInterceptor = HttpLoggingInterceptor()

            val auth = SessionManager.getInstance(context)!!.authToken
            val interceptor = Interceptor { chain ->
                val original: Request = chain.request()
                val request: Request = original.newBuilder()
                    .header("Authorization", "Bearer " + auth)
                    .method(original.method(), original.body())
                    .build()
                chain.proceed(request)
            }


            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(loggingInterceptor)
            client.addInterceptor(interceptor)

            return Retrofit.Builder().baseUrl("https://webapi-staging.artyou.global/")
                .addConverterFactory(
                    GsonConverterFactory.create(gson)
                )
                .client(client.build())
                .build()
                .create(ApiService::class.java)
        }

    }
}