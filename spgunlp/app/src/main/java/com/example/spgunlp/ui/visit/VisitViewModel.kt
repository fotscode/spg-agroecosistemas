package com.example.spgunlp.ui.visit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VisitViewModel : ViewModel() {
    private val _nameProducer = MutableLiveData<String>().apply { value = "Productor" }
    private val _members = MutableLiveData<String>().apply { value = "Miembro1,Miembro2" }
    private val _visitDate = MutableLiveData<String>().apply { value = "14/09/2024" }
    private val _surfaceCountry = MutableLiveData<String>().apply { value = "200has" }
    private val _surfaceAgro = MutableLiveData<String>().apply { value = "500has" }
    val nameProducer: LiveData<String> = _nameProducer
    val members: LiveData<String> = _members
    val visitDate: LiveData<String> = _visitDate
    val surfaceCountry: LiveData<String> = _surfaceCountry
    val surfaceAgro: LiveData<String> = _surfaceAgro
}