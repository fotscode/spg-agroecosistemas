package com.example.spgunlp.io.response

data class LoginResponse(
    val usuario: String,
    val token: String,
    val authorities: List<String>
)
