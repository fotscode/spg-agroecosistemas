package com.example.spgunlp.ui.inactive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InactiveViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is inactive Fragment"
    }
    val text: LiveData<String> = _text
}