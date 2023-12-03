package com.example.spgunlp.model

data class AppVisit(
    val comentarioImagenes: String?,
    val estadoVisita: String?,
    val fechaActualizacion: String?,
    val fechaCreacion: String?,
    val fechaVisita: String?,
    val imagenes: List<AppImage>?,
    val integrantes: List<AppUser>?,
    val quintaResponse: AppQuinta?,
    val usuarioOperacion: String?,
    val visitaParametrosResponse: AppVisitParameters?,
    val id: Int?,
){
    constructor(
        comentarioImagenes: String,
        estadoVisita: String,
        fechaActualizacion: String,
        fechaCreacion: String,
        fechaVisita: String,
        imagenes: List<AppImage>,
        integrantes: List<AppUser>,
        quintaResponse: AppQuinta,
        usuarioOperacion: String,
        visitaParametrosResponse: AppVisitParameters
    ) : this(
        comentarioImagenes,
        estadoVisita,
        fechaActualizacion,
        fechaCreacion,
        fechaVisita,
        imagenes,
        integrantes,
        quintaResponse,
        usuarioOperacion,
        visitaParametrosResponse,
        null
    )
}