package com.example.spgunlp.util

import androidx.lifecycle.MutableLiveData
import com.example.spgunlp.model.AppVisit

class SharedVisitRepo {
    private val currentVisit: MutableLiveData<AppVisit> = MutableLiveData()

    fun getCurrentVisit(): AppVisit? {
        return currentVisit.value
    }

    fun updateCurrentVisit(visit: AppVisit) {
        currentVisit.value = visit
    }
}