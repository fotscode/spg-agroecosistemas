package com.example.spgunlp.util

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.repositories.VisitsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VisitsDBViewModel(application: Application):AndroidViewModel(application) {
    //private val getVisits: LiveData<List<AppVisit>>
    private val repository: VisitsRepository

    init {
        val visitsDao = AppDatabase.getDatabase(application).visitsDao()
        repository = VisitsRepository(visitsDao)
        //getVisits = repository.getAllVisits()
    }

    fun insertVisit(visit: AppVisit){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertVisit(visit)
        }
    }

    fun insertVisits(visits: List<AppVisit>){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertVisits(visits)
        }
    }

    fun updateVisit(visit: AppVisit){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateVisit(visit)
        }
    }

    fun updateVisits(visits: List<AppVisit>){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateVisits(visits)
        }
    }

    fun clearVisits(){
        viewModelScope.launch(Dispatchers.IO){
            repository.clearVisits()
        }
    }

    fun getAllVisits(): LiveData<List<AppVisit>>{
        return repository.getAllVisits()
    }
    fun getVisitById(id: Int): LiveData<AppVisit>{
        return repository.getVisitById(id)
    }

}