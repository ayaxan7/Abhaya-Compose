package com.ayaan.abhaya.network

import android.util.Log
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
    @POST("api/admin")
    fun sendSos(
//        @Header("Authorization") authToken: String,
        @Body sosRequest: SosRequest
    ): Call<ResponseBody>
}

// 2. Data class for the request body (Kotlin data class)
data class SosRequest(
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("time") val time: String,
    @SerializedName("name") val name: String,
    @SerializedName("phoneNo") val phoneNo: String,
    @SerializedName("uid") val uid: String
)

// 3. Retrofit Client as an object (Kotlin singleton)
object RetrofitClient {
    private const val BASE_URL = "https://sih-backend-m11u.onrender.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            val t1 = System.nanoTime()
            Log.d("RetrofitClient", "Sending request: ${request.url} on ${chain.connection()} ${request.headers}")
            val response = chain.proceed(request)
            val t2 = System.nanoTime()
            Log.d("RetrofitClient", "Received response for ${response.request.url} in ${(t2 - t1) / 1e6}ms ${response.headers}")
            response
        }
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