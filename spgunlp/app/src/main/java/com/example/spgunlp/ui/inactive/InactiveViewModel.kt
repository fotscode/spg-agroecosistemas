package com.example.spgunlp.ui.inactive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.util.VisitsViewModel

class InactiveViewModel(private val savedStateHandle: SavedStateHandle) : VisitsViewModel(savedStateHandle) {
    var showAll: Boolean = false

    fun saveInactiveVisits(visits: List<AppVisit>){
        savedStateHandle["inactive_visits"] = visits
    }

    fun getInactiveVisits(): List<AppVisit>?{
        return this.savedStateHandle["inactive_visits"]
    }

    fun isInactiveVisitListEmpty(): Boolean{
        return this.savedStateHandle.contains("inactive_visits")
    }
}