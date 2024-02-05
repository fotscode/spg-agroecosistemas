package com.example.spgunlp.util

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppVisit

open class VisitsViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    fun saveVisits(visits: List<AppVisit>) {
        this.savedStateHandle["visits"] = visits
    }

    fun updateVisit(visit: AppVisit) {
        val visits = getVisits()?.toMutableList()
        val oldVisit = visits?.find { it.id == visit.id }
        if (oldVisit != null) {
            val pos = visits.indexOf(oldVisit)
            visits[pos] = visit
        }
        this.savedStateHandle["visits"] = visits?.toList()
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