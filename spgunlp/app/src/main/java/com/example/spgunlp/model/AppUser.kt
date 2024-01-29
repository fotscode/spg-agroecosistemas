package com.example.spgunlp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

val PROFILE="PROFILE"
val LAST_UPDATE_PROFILE="LAST_UPDATE_PROFILE"
@Parcelize
data class AppUser(
    val id: Int?,
    val email: String?,
    val password: String?,
    val celular: String?,
    val nombre: String?,
    val organizacion: String?,
    val posicion: Int?,
    val posicionResponse: Posicion?,
    val roles: List<Rol>?,
    val estado: Boolean?
): Parcelable {
    constructor(email: String, password: String) : this(null,
        email,
        password,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )

    constructor(email: String, password: String, celular: String, nombre: String, organizacion: String, posicion: Int) : this(null,
        email,
        password,
        celular,
        nombre,
        organizacion,
        posicion,
        null,
        null,
        null)
}
