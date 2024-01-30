package com.example.spgunlp.util

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppVisit

open class VisitsViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    fun saveVisits(visits: List<AppVisit>) {
        this.savedStateHandle["visits"] = visits
    }
    fun getVisits(): List<AppVisit>?{
        return savedStateHandle["visits"]
    }
    fun isVisitListEmpty(): Boolean{
        return !savedStateHandle.contains("visits")
    }

    fun clearVisitList() {
        savedStateHandle.remove<List<AppVisit>>("visits")
    }
}