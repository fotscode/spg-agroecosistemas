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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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
    private val bundleViewModel: BundleViewModel by viewModels()
    private lateinit var sender:AppMessage.ChatUser

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        supportFragmentManager.findFragmentById(active_visit);

        if (bundleViewModel.isActivityStateEmpty()) {
            visit = intent.getParcelableExtra<AppVisit>(VISIT_ITEM)!!
            intent.removeExtra(VISIT_ITEM)
            updateVisitViewModel()
            updateParametersViewModel()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bundleViewModel.saveActivityState(visit)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        visit = bundleViewModel.getVisit()!!
    }
    fun updateVisit(visit: AppVisit){
        Log.i("SPGUNLP_TAG", "Updates visit...")
        this.visit = visit
        updateVisitViewModel()
        updateParametersViewModel()
    }

    @SuppressLint("NewApi")
    fun updateVisitViewModel(){
        visitViewModel.setVisit(visit)
    }
    private fun updateParametersViewModel(){
        val parameterValues = visit.visitaParametrosResponse?.map { it }
        val parametersFiltered = parameterValues?.filter { it.parametro?.habilitado == true }
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
        var usrName = "NAME"
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
        lifecycleScope.launch{
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

}