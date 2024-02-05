package com.example.spgunlp.ui.active

import androidx.lifecycle.SavedStateHandle
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.util.VisitsViewModel

class ActiveViewModel(private val savedStateHandle: SavedStateHandle) : VisitsViewModel(savedStateHandle) {

    fun saveActiveVisits(visits: List<AppVisit>){
        savedStateHandle["active_visits"] = visits
    }

    fun getActiveVisits(): List<AppVisit>?{
        return this.savedStateHandle["active_visits"]
    }

    fun isActiveVisitListEmpty(): Boolean {
        val list = this.savedStateHandle.get<List<AppVisit>>("active_visits")
        return list.isNullOrEmpty()
    }

}