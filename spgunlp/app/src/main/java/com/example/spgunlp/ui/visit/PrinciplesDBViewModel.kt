package com.example.spgunlp.ui.visit

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.repositories.PrinciplesRepository
import com.example.spgunlp.util.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrinciplesDBViewModel(application: Application):AndroidViewModel(application) {
    private val getPrinciples: LiveData<List<AppVisitParameters.Principle>>
    private val repository: PrinciplesRepository

    init {
        val principlesDao = AppDatabase.getDatabase(application).principlesDao()
        repository = PrinciplesRepository(principlesDao)
        getPrinciples = repository.getPrinciples()
    }

    fun insertPrinciples(principles: List<AppVisitParameters.Principle>){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertPrinciples(principles)
        }
    }

    fun getPrinciples(): LiveData<List<AppVisitParameters.Principle>>{
        return repository.getPrinciples()
    }

    fun clearPrinciples(){
        viewModelScope.launch(Dispatchers.IO){
            repository.clearPrinciples()
        }
    }
}