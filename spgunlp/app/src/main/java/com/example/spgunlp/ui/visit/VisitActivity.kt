package com.example.spgunlp.ui.visit

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.spgunlp.R.id.active_visit
import com.example.spgunlp.databinding.ActivityVisitBinding
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.VISIT_ITEM
import com.google.gson.Gson
import java.time.LocalDateTime

class VisitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisitBinding
    private lateinit var visit: AppVisit
    private val visitViewModel: VisitViewModel by viewModels()
    private val principlesViewModel: PrinciplesViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .add(active_visit, VisitFragment())
            .commit()

        // viewModel for visit
        val visitGson= intent.getStringExtra(VISIT_ITEM)
        visit = Gson().fromJson(visitGson, AppVisit::class.java)
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

        //TODO usar API de principios en lugar de extraerlos de la visita. Tomar parámetros de la visita y asignarlos como checks de cada principio

        /* viewModel for principles
        val principleValues = visit.visitaParametrosResponse?.map { it.parametro?.principioAgroecologico }
        principlesViewModel.setPrinciples(principleValues)
        principleValues?.forEach {
                principle -> Log.d("Principles", "${principle?.nombre}, ${principle?.id}")
        }
        val parameters = visit.visitaParametrosResponse?.map {it.parametro}
        parameters?.forEach{
            parameter ->
            Log.d("Parameters", "${parameter?.nombre}, ${parameter?.id}, ${parameter?.principioAgroecologico?.nombre}")
        }
        */


        //Logger.getGlobal().log(Level.SEVERE,visit.quintaResponse?.nombreProductor) TODO(remove)
    }

}