package com.example.spgunlp.ui.visit

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.spgunlp.R.id.active_visit
import com.example.spgunlp.databinding.ActivityVisitBinding
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppUser
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.model.AppVisitUpdate
import com.example.spgunlp.model.VISIT_ITEM
import com.google.gson.Gson
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class VisitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisitBinding
    private lateinit var visit: AppVisit
    private val visitViewModel: VisitViewModel by viewModels()
    private val parametersViewModel: ParametersViewModel by viewModels()

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


        //Logger.getGlobal().log(Level.SEVERE,visit.quintaResponse?.nombreProductor) TODO(remove)
    }

    fun updateVisit(visit: AppVisit){
        this.visit = visit
        updateVisitViewModel()
        updateParametersViewModel()
    }

    @SuppressLint("NewApi")
    fun updateVisitViewModel(){
        val memberValues = visit.integrantes?.map { it.nombre }
        val members= memberValues?.joinToString(separator=",")
        val formatter = java.time.format.DateTimeFormatter.ISO_DATE_TIME
        val date = LocalDateTime.parse(visit.fechaVisita, formatter)
        val dateFormatted = "${date.dayOfMonth}/${date.monthValue}/${date.year}"
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
    fun updateParametersViewModel(){
        val parameterValues = visit.visitaParametrosResponse?.map { it }
        val parametersFiltered = parameterValues?.filter { it?.parametro?.habilitado == true }
        parametersViewModel.setParameters(parametersFiltered)
    }


}