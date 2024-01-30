package com.example.spgunlp.ui.active

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.util.VisitsViewModel

class ActiveViewModel(private val savedStateHandle: SavedStateHandle) : VisitsViewModel(savedStateHandle) {

    fun saveActiveVisits(visits: List<AppVisit>){
        savedStateHandle["active_visits"] = visits
    }

    fun getActiveVisits(): List<AppVisit>?{
        return this.savedStateHandle["active_visits"]
    }

    fun isActiveVisitListEmpty(): Boolean{
        return this.savedStateHandle.contains("active_visits")
    }
}