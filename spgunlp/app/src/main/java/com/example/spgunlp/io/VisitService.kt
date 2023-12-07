package com.example.spgunlp.io

import com.example.spgunlp.BuildConfig
import com.example.spgunlp.io.response.VisitByIdResponse
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitParameters
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

    @Headers("Accept: */*")
    @GET("principios/")
    suspend fun getPrinciples(@Header("Authorization") token: String): Response<List<AppVisitParameters.Principle>>

    @Headers("Accept: */*")
    @GET("parametros/habilitados")
    suspend fun getParameters(@Header("Authorization") token: String): Response<List<AppVisitParameters>>

    companion object Factory {
        fun create(): VisitService {
            val baseUrl= BuildConfig.BASE_URL
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("$baseUrl/tesina/")
                .build()

            return retrofit.create(VisitService::class.java)
        }
    }
}