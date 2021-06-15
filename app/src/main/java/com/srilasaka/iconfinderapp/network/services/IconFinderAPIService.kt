package com.srilasaka.iconfinderapp.network.services

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.srilasaka.iconfinderapp.network.models.IconSets
import com.srilasaka.iconfinderapp.network.models.Icons
import com.srilasaka.iconfinderapp.network.utils.EndPoints
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Moshi is used for Kotlin Compatibility with Retrofit
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val logging: HttpLoggingInterceptor by lazy {
    HttpLoggingInterceptor().also {
        it.level = HttpLoggingInterceptor.Level.BASIC
    }
}

private val client = OkHttpClient.Builder().addInterceptor(logging).build()


/**
 * Retrofit Builder which adds Moshi as ConverterFactory and uses Coroutine as CallAdapterFactory
 * and adds the BASE_URL as EndPoints.BASE_URL
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(client)
    .baseUrl(EndPoints.BASE_URL)
    .build()

interface IconFinderAPIService {

    @Headers("Authorization: ${EndPoints.AuthorizationHeader}")
    @GET("iconsets")
    suspend fun getAllPublicIconSets(
        // Setting the default count to 20
        @Query("count") count: Int = 20,
        // "after" query parameter is optional - if no need to use this, just pass null
        @Query("after") after: Int?,
        // premium - "false" or "0" means not-premium, "true" or "1" means premium,
        // "all" means include all icons no matter if they are premium icons or not and it's the default
        @Query("premium") premium: String = "all"
    ): IconSets


    @Headers("Authorization: ${EndPoints.AuthorizationHeader}")
    @GET("icons/search")
    suspend fun searchIcons(
        // Setting the default count to 20
        @Query("count") count: Int = 20,
        // "query" parameter is mandatory
        @Query("query") query: String,
        // offset value ranges from 0 - 100
        @Query("offset") offset: Int,
        // premium - "false" or "0" means not-premium, "true" or "1" means premium,
        // "all" means include all icons no matter if they are premium icons or not and it's the default
        @Query("premium") premium: String = "all"
    ): Icons

    companion object {
        fun create(): IconFinderAPIService = IconFinderAPI.retrofitService
    }
}

/**
 * All the methods in the IconFinderAPIService can be called using the IconFinderAPI object
 * via retrofitService
 */
object IconFinderAPI {
    val retrofitService: IconFinderAPIService by lazy {
        retrofit.create(IconFinderAPIService::class.java)
    }
}