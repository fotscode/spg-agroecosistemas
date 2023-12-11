package com.example.spgunlp.ui.visit

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppVisitParameters

class BundleViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    fun savePrinciplesState(principles: List<AppVisitParameters.Principle>, states: List<Boolean>) {
        savedStateHandle["principles"] = principles
        savedStateHandle["states"] = states
    }

    fun getPrinciplesList(): List<AppVisitParameters.Principle>? {
        return this.savedStateHandle["principles"]
    }

    fun getStatesList(): List<Boolean>? {
        return this.savedStateHandle["states"]
    }

    fun clearPrinciplesState() {
        savedStateHandle.remove<List<AppVisitParameters.Principle>>("principles")
        savedStateHandle.remove<List<Boolean>>("states")
    }

    fun isPrinciplesStateEmpty(): Boolean {
        return !savedStateHandle.contains("principles") && !savedStateHandle.contains("states")
    }

    fun saveParametersState(principleName: String) {
        savedStateHandle["principleName"] = principleName
    }

    fun getPrincipleName(): String? {
        return this.savedStateHandle["principleName"]
    }

    fun isParametersStateEmpty(): Boolean {
        return !savedStateHandle.contains("principleName")
    }

    fun clearParametersState() {
        savedStateHandle.remove<String>("principleName")
    }
}