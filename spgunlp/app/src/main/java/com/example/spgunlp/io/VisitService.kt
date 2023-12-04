package com.example.spgunlp.io

import com.example.spgunlp.io.response.VisitByIdResponse
import com.example.spgunlp.model.AppVisit
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface VisitService {



    @Headers("Accept: */*")
    @GET("visitas/")
    suspend fun getVisits(@Header("Authorization") token: String): Response<List<AppVisit>>

    @GET("visitas/{id}")
    suspend fun getVisitById(@Path("id") visitId: Int): Response<VisitByIdResponse>

    companion object Factory {
        fun create(): VisitService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://192.168.1.87:9090/tesina/")
                .build()

            return retrofit.create(VisitService::class.java)
        }
    }
}