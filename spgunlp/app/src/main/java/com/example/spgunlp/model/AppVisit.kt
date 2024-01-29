package com.example.spgunlp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


val VISIT_ITEM="VISIT_ITEM"
val IS_ACTIVE="IS_ACTIVE"
val MODIFIED_VISIT="MODIFIED_VISIT"

@Parcelize
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
    val visitaParametrosResponse: List<AppVisitParameters>?,
    val id: Int?,
): Parcelable {
}