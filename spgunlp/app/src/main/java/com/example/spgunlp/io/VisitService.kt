package com.example.spgunlp.io

import com.example.spgunlp.io.response.VisitByIdResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface VisitService {
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