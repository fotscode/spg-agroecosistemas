package com.example.spgunlp.util

import android.app.Application
import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitUpdate
import com.example.spgunlp.model.VisitUpdate
import com.example.spgunlp.repositories.VisitUpdateRepository
import com.example.spgunlp.repositories.VisitsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VisitChangesDBViewModel(application: Application):AndroidViewModel(application) {
    private val repository: VisitUpdateRepository

    init {
        val visitUpdateDao = AppDatabase.getDatabase(application).visitUpdatesDao()
        repository = VisitUpdateRepository(visitUpdateDao)
    }

    fun addVisit(visit: AppVisitUpdate, email: String){
        viewModelScope.launch(Dispatchers.IO){
            val visitUpdate = VisitUpdate(email, visit)
            repository.addVisit(visitUpdate)
        }
    }

    fun getVisitsByEmail(email: String): List<AppVisitUpdate>?{
        return repository.getVisitsByEmail(email)
    }

}