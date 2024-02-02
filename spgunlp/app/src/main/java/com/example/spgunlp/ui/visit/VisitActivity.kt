package com.example.spgunlp.ui.visit

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.spgunlp.R.id.active_visit
import com.example.spgunlp.databinding.ActivityVisitBinding
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.VISIT_ITEM
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.set
import kotlinx.coroutines.Job
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class VisitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisitBinding
    private lateinit var visit: AppVisit
    private val visitViewModel: VisitViewModel by viewModels()
    private val parametersViewModel: ParametersViewModel by viewModels()

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
        val formatter = DateTimeFormatter.ISO_DATE_TIME
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

    override fun onDestroy() {
        super.onDestroy()
        if (::jobToKill.isInitialized)
            jobToKill.cancel()
    }
}