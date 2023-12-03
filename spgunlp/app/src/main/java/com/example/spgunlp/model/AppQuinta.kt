package com.example.spgunlp.model

data class AppQuinta (
    val comentarios: String?,
    val direccion: String?,
    val fechaCreacion: String?,
    val fechaUltimaVisita: String?,
    val imagenes: List<AppImage>?,
    val nombreProductor: String?,
    val organizacion: String?,
    val selloGarantia: String?,
    val superficieAgroecologiaCampo: Int?,
    val superficieAgroecologiaInvernaculo: Int?,
    val superficieTotalCampo: Int?,
    val superficieTotalInvernaculo : Int?,
    val usuarioOperacion: String?,
    val id: Int?
){
    constructor(
        comentarios: String,
        direccion: String,
        fechaCreacion: String,
        fechaUltimaVisita: String,
        imagenes: List<AppImage>,
        nombreProductor: String,
        organizacion: String,
        selloGarantia: String,
        superficieAgroecologiaCampo: Int,
        superficieAgroecologiaInvernaculo: Int,
        superficieTotalCampo: Int,
        superficieTotalInvernaculo: Int,
        usuarioOperacion: String
    ):this(
        comentarios,
        direccion,
        fechaCreacion,
        fechaUltimaVisita,
        imagenes,
        nombreProductor,
        organizacion,
        selloGarantia,
        superficieAgroecologiaCampo,
        superficieAgroecologiaInvernaculo,
        superficieTotalCampo,
        superficieTotalInvernaculo,
        null,
        null
    )
}