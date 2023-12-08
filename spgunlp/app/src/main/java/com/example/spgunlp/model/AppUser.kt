package com.example.spgunlp.model

data class AppUser(
    val email: String?,
    val password: String?,
    val celular: String?,
    val nombre: String?,
    val organizacion: String?,
    val posicion: Int?,
    val id: Int?,
    val roles: List<String>?
){
    constructor(email: String, password: String) : this(email, password, null, null, null, null, null, null)
}
