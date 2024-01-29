package com.example.spgunlp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppImage(
    val contenido: ByteArray?,
    val id: Int?,
    val tipo: String?,
): Parcelable {
    constructor(contenido: ByteArray, tipo: String) : this(contenido, null, tipo)
}
