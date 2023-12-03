package com.example.spgunlp.model

data class AppImage(
    val contenido: ByteArray?,
    val id: Int?,
    val tipo: String?,
){
    constructor(contenido: ByteArray, tipo: String) : this(contenido, null, tipo)
}
