package com.example.spgunlp.model

data class AppImage(
    val contenido: ByteArray?,
    val id: Int?,
    val tipo: String?,
){
    constructor(contenido: ByteArray, id: Int, tipo: String) : this(contenido, id, null)
}
