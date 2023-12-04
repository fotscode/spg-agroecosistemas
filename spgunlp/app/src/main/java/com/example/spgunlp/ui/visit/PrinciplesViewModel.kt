package com.example.spgunlp.ui.visit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppVisitParameters

class PrinciplesViewModel : ViewModel() {
    private val _principles = MutableLiveData<List<AppVisitParameters.Principle?>?>()
    val principles: LiveData<List<AppVisitParameters.Principle?>?> = _principles

    fun setPrinciples(value: List<AppVisitParameters.Principle?>?){
        _principles.value = value
    }
}