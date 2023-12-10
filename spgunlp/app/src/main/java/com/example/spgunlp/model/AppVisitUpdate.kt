package com.example.spgunlp.model

data class AppVisitUpdate(
    val fechaVisita: String?,
    val integrantes: List<Int>?,
    val parametros: List<ParametersUpdate>?,
    val quintaId: Int?
){
    data class ParametersUpdate (
        val aspiracionesFamiliares: String?,
        val comentarios: String?,
        val cumple: Boolean?,
        val parametroId: Int?,
        val sugerencias: String?
    )
}