package com.example.spgunlp.ui.visit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppMessage
import com.example.spgunlp.model.AppVisitParameters

class MessagesViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<AppMessage?>?>()
    val messages: LiveData<List<AppMessage?>?> = _messages

    fun setMessages(value: List<AppMessage?>?){
        _messages.value = value
    }

    fun addMessage(message: AppMessage){
        val newMessages = _messages.value?.toMutableList() ?: mutableListOf()
        newMessages.add(message)
        _messages.value = newMessages.toList()
    }

}