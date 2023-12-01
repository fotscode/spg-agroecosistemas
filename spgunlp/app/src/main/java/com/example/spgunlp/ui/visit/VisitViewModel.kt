package com.example.spgunlp.ui.visit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VisitViewModel : ViewModel() {
    val nameProducer = MutableLiveData<String>()
    val members = MutableLiveData<String>()
    val visitDate = MutableLiveData<String>()
    val surfaceCountry = MutableLiveData<String>()
    val surfaceAgro = MutableLiveData<String>()
}