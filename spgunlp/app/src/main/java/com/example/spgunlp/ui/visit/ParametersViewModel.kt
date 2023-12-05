package com.example.spgunlp.ui.visit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppVisitParameters

class ParametersViewModel : ViewModel() {
    private val _parameters = MutableLiveData<List<Pair<Int?, Boolean?>>?>()
    val parameters: LiveData<List<Pair<Int?, Boolean?>>?> = _parameters

    fun setParameters(value: List<Pair<Int?, Boolean?>>?){
        _parameters.value = value
    }

}