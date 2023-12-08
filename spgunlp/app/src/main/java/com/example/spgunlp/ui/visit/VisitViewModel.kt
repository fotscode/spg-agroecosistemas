package com.example.spgunlp.ui.visit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spgunlp.model.AppUser

class VisitViewModel : ViewModel() {
    private val _id = MutableLiveData<Int?>()
    private val _nameProducer = MutableLiveData<String?>()
    private val _members = MutableLiveData<String?>()
    private val _visitDate = MutableLiveData<String?>()
    private val _surfaceCountry = MutableLiveData<Int?>()
    private val _surfaceAgro = MutableLiveData<Int?>()
    private val _countryId  = MutableLiveData<Int?>()
    private val _membersList = MutableLiveData<List<AppUser>?>()
    private val _unformattedVisitDate = MutableLiveData<String?>()

    val id: LiveData<Int?> get() = _id
    val nameProducer: LiveData<String?> get() = _nameProducer
    val members: LiveData<String?> get() = _members
    val visitDate: LiveData<String?> get() = _visitDate
    val surfaceCountry: LiveData<Int?> get() = _surfaceCountry
    val surfaceAgro: LiveData<Int?> get() = _surfaceAgro
    val membersList: LiveData<List<AppUser>?> get() = _membersList
    val countryId: LiveData<Int?> get() = _countryId
    val unformattedVisitDate: LiveData<String?> get() = _unformattedVisitDate

    fun setId(value: Int?){
        _id.value = value
    }
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
    fun setCountryId(value: Int?){
        _countryId.value = value
    }
    fun setMembersList(value: List<AppUser>?){
        _membersList.value = value
    }
    fun setUnformattedVisitDate(value: String?){
        _unformattedVisitDate.value = value
    }
}