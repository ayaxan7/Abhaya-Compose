package com.ayaan.abhaya.network

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// 1. API Interface
interface SosApiService {
    @POST("api/data")
    fun sendSos(
        @Header("Authorization") authToken: String,
        @Body sosRequest: SosRequest
    ): Call<ResponseBody>
}

// 2. Data class for the request body (Kotlin data class)
data class SosRequest(
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("name") val name: String,
    @SerializedName("phoneNo") val phoneNo: String,
    @SerializedName("time") val time: Long,
    @SerializedName("uid") val uid: String
)

// 3. Retrofit Client as an object (Kotlin singleton)
object RetrofitClient {
    private const val BASE_URL = "https://sih-backend-8bsr.onrender.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val sosApiService: SosApiService = retrofit.create(SosApiService::class.java)
}