package com.example.spgunlp.ui.visit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VisitViewModel : ViewModel() {
    private val _nameProducer = MutableLiveData<String?>()
    private val _members = MutableLiveData<String?>()
    private val _visitDate = MutableLiveData<String?>()
    private val _surfaceCountry = MutableLiveData<Int?>()
    private val _surfaceAgro = MutableLiveData<Int?>()
    val nameProducer: LiveData<String?> get() = _nameProducer
    val members: LiveData<String?> get() = _members
    val visitDate: LiveData<String?> get() = _visitDate
    val surfaceCountry: LiveData<Int?> get() = _surfaceCountry
    val surfaceAgro: LiveData<Int?> get() = _surfaceAgro

    fun setNameProducer(value: String?){
        _nameProducer.value = value
    }
    fun setMembers(value: String?){
        _members.value = value
    }
    fun setVisitDate(value: String?){
        _visitDate.value = value
    }
    fun setSurfaceCountry(value: Int?){
        _surfaceCountry.value = value
    }
    fun setSurfaceAgro(value: Int?){
        _surfaceAgro.value = value
    }
}