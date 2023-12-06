package com.example.spgunlp.ui.visit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppVisitParameters

class ParametersViewModel : ViewModel() {
    private val _parameters = MutableLiveData<List<AppVisitParameters?>?>()
    val parameters: LiveData<List<AppVisitParameters?>?> = _parameters

    fun setParameters(value: List<AppVisitParameters?>?){
        _parameters.value = value
    }

}