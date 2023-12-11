package com.example.spgunlp.ui.visit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppVisitParameters

class BundleViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    fun savePrinciplesList(principles: List<AppVisitParameters.Principle>) {
        savedStateHandle["principles"] = principles
    }

    fun getPrinciplesList(): List<AppVisitParameters.Principle>? {
        return this.savedStateHandle["principles"]
    }

    fun saveStatesList(states: List<Boolean>) {
        savedStateHandle["states"] = states
    }

    fun getStatesList(): List<Boolean>? {
        return this.savedStateHandle["states"]
    }

    fun clearState() {
        savedStateHandle.clearSavedStateProvider("principles")
        savedStateHandle.clearSavedStateProvider("states")
    }

    /*
    class YourViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    fun saveList(yourList: List<YourNonParcelableObject>) {
        savedStateHandle.set("yourListKey", yourList)
    }

    fun getList(): List<YourNonParcelableObject>? {
        return savedStateHandle.get("yourListKey")
    }
}
     */
}