package com.example.spgunlp.ui.visit

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.spgunlp.R
import com.example.spgunlp.R.id.active_visit
import com.example.spgunlp.databinding.ActivityVisitBinding
import com.example.spgunlp.io.UserService
import com.example.spgunlp.model.AppMessage
import com.example.spgunlp.model.AppUser
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.CONTENT_TYPE
import com.example.spgunlp.model.PROFILE
import com.example.spgunlp.model.VISIT_ITEM
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

val MESSAGES = "MESSAGES"
class VisitActivity : AppCompatActivity() {

    private val userService: UserService by lazy {
        UserService.create()
    }

    private lateinit var binding: ActivityVisitBinding
    private lateinit var visit: AppVisit
    private val visitViewModel: VisitViewModel by viewModels()
    private val parametersViewModel: ParametersViewModel by viewModels()
    private val messagesViewModel: MessagesViewModel by viewModels()
    private lateinit var sender:AppMessage.ChatUser

    private lateinit var jobToKill: Job

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSupportActionBar()?.hide()

        supportFragmentManager.findFragmentById(active_visit);

        visit = intent.getParcelableExtra<AppVisit>(VISIT_ITEM)!!
        this.intent.extras!!.remove(VISIT_ITEM)

        updateVisitViewModel()
        updateParametersViewModel()
    }

    fun updateVisit(visit: AppVisit){
        this.visit = visit
        updateVisitViewModel()
        updateParametersViewModel()

        val preferences = PreferenceHelper.defaultPrefs(this)
        /*
        val visitGson = Gson().toJson(visit)
        intent.putExtra(VISIT_ITEM, visitGson)

        val visitsGson = preferences["LIST_VISITS", ""]
        val type = object : TypeToken<List<AppVisit>>() {}.type
        val visits = Gson().fromJson<List<AppVisit>>(visitsGson, type)
        val visitsFiltered = visits.filter { it.id != visit.id }.toMutableList()
        visitsFiltered.add(visit)
        val visitsJson = Gson().toJson(visitsFiltered.toList())
        preferences["LIST_VISITS"] = visitsJson

         */
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

    private suspend fun setUserChat(header: String) {
        val preferences = PreferenceHelper.defaultPrefs(baseContext)
        val email: String = preferences["email"]
        var usrName: String = "NAME"
        if (preferences[PROFILE, ""] != "") {
            val type = object : TypeToken<AppUser>() {}.type
            val user = Gson().fromJson<AppUser>(preferences[PROFILE, ""], type)
            usrName = user.nombre.toString()
        } else {
            try {
                val response = userService.getUsers(header)
                if (response.isSuccessful && response.body() != null) {
                    val member =
                        response.body()!!.firstOrNull { it.email.equals(preferences["email"]) }
                    usrName = member?.nombre.toString()
                }
            } catch (e: Exception){
                Log.e("Visit activity", e.message.toString())
            }
        }
        sender = AppMessage.ChatUser(email, usrName)
    }

    @SuppressLint("NewApi", "SimpleDateFormat")
    fun sendNewMessage(contentType: CONTENT_TYPE, data: String, principleId: Int, fragment: ObservationsFragment) {
        jobToKill = lifecycleScope.launch{
            val preferences = PreferenceHelper.defaultPrefs(baseContext)
            val jwt = preferences["jwt", ""]
            if (!jwt.contains("."))
                cancel()
            val header = "Bearer $jwt"
            if (!::sender.isInitialized) {
                setUserChat(header)
            }
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val isoDateTimeString = sdf.format(Date())
            val message =
                AppMessage(contentType, data, isoDateTimeString, sender, visit.id, principleId)
            messagesViewModel.addMessage(message)
            updateMessagesStored(message, principleId)
            fragment.updateMessagesList()
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.messages_list)
            recyclerView.scrollToPosition(recyclerView.adapter?.itemCount?.minus(1) ?: 0)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        if (::jobToKill.isInitialized)
            jobToKill.cancel()
    }
}