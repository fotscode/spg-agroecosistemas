package com.example.spgunlp.model

data class AppVisitParameters (
    val aspiracionesFamiliares: String?,
    val comentarios: String?,
    val cumple: Boolean?,
    val id: Int?,
    val nombre: String?,
    val parametro: Parameter?,
    val sugerencias: String?
){
    constructor(
        aspiracionesFamiliares: String,
        comentarios: String,
        cumple: Boolean,
        id: Int,
        nombre: String,
        parametro: Parameter,
        sugerencias: String
    ):this(
        aspiracionesFamiliares,
        comentarios,
        cumple,
        null,
        nombre,
        parametro,
        sugerencias
    )

    data class Parameter(
        val habilitado: Boolean?,
        val id: Int?,
        val nombre: String?,
        val principioAgroecologico: Principle?,
        val situacionEsperable: String?
    ){
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

    data class Principle(
        val habilitado: Boolean?,
        val id: Int?,
        val nombre: String?
    ){
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
