package com.example.spgunlp.ui.visit

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.util.PrinciplesViewModel

class BundleViewModel(private val savedStateHandle: SavedStateHandle) : PrinciplesViewModel(savedStateHandle) {

    fun savePrinciplesState(principles: List<AppVisitParameters.Principle>, states: List<Boolean>) {
        super.updatePrinciplesList(principles)
        savedStateHandle["states"] = states
    }
    fun getStatesList(): List<Boolean>? {
        return this.savedStateHandle["states"]
    }

    fun clearPrinciplesState() {
        super.clearPrinciplesList()
        savedStateHandle.remove<List<Boolean>>("states")
    }

    fun isPrinciplesStateEmpty(): Boolean {
        return !savedStateHandle.contains("principles") && !savedStateHandle.contains("states")
    }

    fun clearParametersState() {
        savedStateHandle.remove<String>("principleName")
    }

    fun saveObservationsState(principleId: Int, principleName: String, email: String) {
        savedStateHandle["principleId"] = principleId
        savedStateHandle["principleObsName"] = principleName
        savedStateHandle["email"] = email
    }

    fun getPrincipleId(): Int? {
        return this.savedStateHandle["principleId"]
    }

    fun getPrincipleObsName(): String? {
        return this.savedStateHandle["principleObsName"]
    }

    fun getEmail(): String? {
        return this.savedStateHandle["email"]
    }

    fun isObservationsStateEmpty(): Boolean {
        return !savedStateHandle.contains("principleId") && !savedStateHandle.contains("principleObsName") && !savedStateHandle.contains(
            "email"
        )
    }

    fun clearObservationsState() {
        savedStateHandle.remove<Int>("principleId")
        savedStateHandle.remove<String>("principleObsName")
        savedStateHandle.remove<String>("email")
    }

    fun saveParametersState(parametersList: List<AppVisitParameters>) {
        savedStateHandle["parametersList"] = parametersList
    }

    fun getParametersList(): List<AppVisitParameters>? {
        return this.savedStateHandle["parametersList"]
    }

    fun clearParametersList() {
        savedStateHandle.remove<String>("parametersList")
    }

    fun isParametersStateEmpty(): Boolean {
        return !savedStateHandle.contains("parametersList")
    }
}