package com.example.spgunlp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppQuinta (
    val id: Int?,
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
): Parcelable {
}