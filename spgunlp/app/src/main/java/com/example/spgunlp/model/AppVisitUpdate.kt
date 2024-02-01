package com.example.spgunlp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

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

@Entity(tableName = "changes_table")
data class VisitUpdate(
    @PrimaryKey(autoGenerate = false)
    val email: String,
    val visit: AppVisitUpdate
){}