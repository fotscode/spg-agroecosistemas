package com.example.spgunlp.ui.visit

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.example.spgunlp.R.id.active_visit
import com.example.spgunlp.databinding.ActivityVisitBinding
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppMessage
import com.example.spgunlp.model.AppUser
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.model.AppVisitUpdate
import com.example.spgunlp.model.CONTENT_TYPE
import com.example.spgunlp.model.VISIT_ITEM
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone

val MESSAGES = "MESSAGES"
class VisitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisitBinding
    private lateinit var visit: AppVisit
    private val visitViewModel: VisitViewModel by viewModels()
    private val parametersViewModel: ParametersViewModel by viewModels()
    private val messagesViewModel: MessagesViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSupportActionBar()?.hide()

        supportFragmentManager.beginTransaction()
            .add(active_visit, VisitFragment())
            .commit()

        val visitGson= intent.getStringExtra(VISIT_ITEM)
        visit = Gson().fromJson(visitGson, AppVisit::class.java)

        updateVisitViewModel()
        updateParametersViewModel()
    }

    fun updateVisit(visit: AppVisit){
        this.visit = visit
        updateVisitViewModel()
        updateParametersViewModel()

        val preferences = PreferenceHelper.defaultPrefs(this)
        val visitsGson = preferences["LIST_VISITS", ""]
        val type = object : TypeToken<List<AppVisit>>() {}.type
        val visits = Gson().fromJson<List<AppVisit>>(visitsGson, type)
        val visitsFiltered = visits.filter { it.id != visit.id }.toMutableList()
        visitsFiltered.add(visit)
        val visitsJson = Gson().toJson(visitsFiltered.toList())
        preferences["LIST_VISITS"] = visitsJson
        preferences["LAST_UPDATE"] = Date().time
    }

    @SuppressLint("NewApi")
    fun updateVisitViewModel(){
        val memberValues = visit.integrantes?.map { it.nombre }
        val members= memberValues?.joinToString(separator=",")
        val formatter = java.time.format.DateTimeFormatter.ISO_DATE_TIME
        val date = LocalDateTime.parse(visit.fechaVisita, formatter)
        val dateFormatted = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        visitViewModel.setNameProducer(visit.quintaResponse?.nombreProductor)
        visitViewModel.setVisitDate(dateFormatted)
        visitViewModel.setMembers(members)
        visitViewModel.setSurfaceAgro(visit.quintaResponse?.superficieAgroecologiaCampo)
        visitViewModel.setSurfaceCountry(visit.quintaResponse?.superficieTotalCampo)
        visitViewModel.setId(visit.id)
        visitViewModel.setCountryId(visit.quintaResponse?.id)
        visitViewModel.setMembersList(visit.integrantes)
        visitViewModel.setUnformattedVisitDate(visit.fechaVisita)
    }
    private fun updateParametersViewModel(){
        val parameterValues = visit.visitaParametrosResponse?.map { it }
        val parametersFiltered = parameterValues?.filter { it?.parametro?.habilitado == true }
        parametersViewModel.setParameters(parametersFiltered)
    }

    fun updateMessagesViewModel(principleId: Int){
        try{
            val preferences = PreferenceHelper.defaultPrefs(this)
            val messagesString = preferences["${MESSAGES}:${visit.id}:${principleId}", ""]
            val messagesType = object : TypeToken<List<AppMessage>>() {}.type
            val messages = Gson().fromJson<List<AppMessage>>(messagesString, messagesType)
            val messageList = messages.sortedBy { item -> item.date }
            messagesViewModel.setMessages(messageList)
        } catch (e: Exception) {
            messagesViewModel.setMessages(emptyList())
        }
    }

    @SuppressLint("NewApi")
    fun sendNewMessage(contentType: CONTENT_TYPE, data: String, principleId: Int): AppMessage{
        val preferences = PreferenceHelper.defaultPrefs(this)
        val sender = AppMessage.ChatUser(preferences["email"], "USR NAME")
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val isoDateTimeString = sdf.format(Date())
        val message = AppMessage(contentType, data, isoDateTimeString, sender, visit.id, principleId)
        messagesViewModel.addMessage(message)
        updateMessagesStored(message, principleId)
        return message
    }

    private fun updateMessagesStored(message: AppMessage, principleId: Int){
        val preferences = PreferenceHelper.defaultPrefs(this)
        try{
            val messagesString = preferences["${MESSAGES}:${visit.id}:${principleId}", ""]
            val messagesType = object : TypeToken<MutableList<AppMessage>>() {}.type
            val messages = Gson().fromJson<MutableList<AppMessage>>(messagesString, messagesType)
            messages.add(message)
            val messageList = messages.sortedBy { msg -> msg.date }
            val jsonMessages = Gson().toJson(messageList)
            preferences["${MESSAGES}:${visit.id}:${principleId}"] = jsonMessages
        } catch (e: Exception) {
            val messages = mutableListOf<AppMessage>()
            messages.add(message)
            val jsonMessages = Gson().toJson(messages)
            preferences["${MESSAGES}:${visit.id}:${principleId}"] = jsonMessages
        }
    }

}