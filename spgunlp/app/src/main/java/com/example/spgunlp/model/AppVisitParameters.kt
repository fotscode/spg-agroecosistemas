package com.example.spgunlp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppVisitParameters (
    val aspiracionesFamiliares: String?,
    val comentarios: String?,
    val cumple: Boolean?,
    val id: Int?,
    val nombre: String?,
    val parametro: Parameter?,
    val sugerencias: String?
): Parcelable {
    @Parcelize
    data class Parameter(
        val habilitado: Boolean?,
        val id: Int?,
        val nombre: String?,
        val principioAgroecologico: Principle?,
        val situacionEsperable: String?
    ): Parcelable{
        constructor(
            habilitado: Boolean,
            id: Int,
            nombre: String,
            principioAgroecologico: Principle,
            situacionEsperable: String
        ):this(
            habilitado,
            null,
            nombre,
            principioAgroecologico,
            situacionEsperable
        )
    }

    @Parcelize
    data class Principle(
        val habilitado: Boolean?,
        val id: Int?,
        val nombre: String?
    ): Parcelable{
        constructor(
            habilitado: Boolean,
            id: Int,
            nombre: String
        ):this(
            habilitado,
            null,
            nombre
        )
    }

}
