package com.example.spgunlp.io

import com.example.spgunlp.io.response.LoginResponse
import com.example.spgunlp.model.AppUser
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body user:AppUser): Response<LoginResponse>

    @POST("auth/registro")
    suspend fun registro(@Body user:AppUser): Response<Void>

    companion object Factory {
        fun create(): AuthService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://192.168.0.53:9090/tesina/")
                .build()

            return retrofit.create(AuthService::class.java)
        }
    }
}