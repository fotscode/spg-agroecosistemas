package com.example.spgunlp.util

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitParameters

open class PrinciplesViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    fun updatePrinciplesList(principles: List<AppVisitParameters.Principle>) {
        this.savedStateHandle["principles"] = principles
    }
    fun getPrinciplesList(): List<AppVisitParameters.Principle>?{
        return savedStateHandle["principles"]
    }
    fun isPrinciplesListEmpty(): Boolean{
        return !savedStateHandle.contains("principles")
    }
    fun clearPrinciplesList() {
        savedStateHandle.remove<List<AppVisitParameters.Principle>>("principles")
    }
}