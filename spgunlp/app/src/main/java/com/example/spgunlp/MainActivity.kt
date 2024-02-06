package com.example.spgunlp

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.spgunlp.databinding.ActivityMainBinding
import com.example.spgunlp.io.AuthService
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.io.sync.AndroidAlarmScheduler
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.model.AppVisitUpdate
import com.example.spgunlp.ui.visit.PrinciplesDBViewModel
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import com.example.spgunlp.util.VisitChangesDBViewModel
import com.example.spgunlp.util.VisitsDBViewModel
import com.example.spgunlp.util.performLogin
import com.example.spgunlp.util.performSync
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val authService: AuthService by lazy {
        AuthService.create()
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var visitsDBViewModel: VisitsDBViewModel
    private lateinit var visitUpdateViewModel: VisitChangesDBViewModel
    private lateinit var principlesViewModel: PrinciplesDBViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scheduler = AndroidAlarmScheduler(this)
        scheduler.schedule()

        // init viewmodel
        visitsDBViewModel = ViewModelProvider(this)[VisitsDBViewModel::class.java]
        visitUpdateViewModel = ViewModelProvider(this)[VisitChangesDBViewModel::class.java]
        principlesViewModel = ViewModelProvider(this)[PrinciplesDBViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        binding.fabSync.setOnClickListener() {
            val preferences = PreferenceHelper.defaultPrefs(this)
            if (!preferences["jwt", ""].contains(".")) {
                return@setOnClickListener
            }
            lifecycleScope.launch {
                binding.fabSync.animate().rotationBy(360f).setDuration(1000).start()
                val colorRed = ContextCompat.getColor(applicationContext, R.color.red)
                if (performSync(this@MainActivity)) {
                    makeText(
                        this@MainActivity,
                        "Sincronización exitosa",
                        Toast.LENGTH_SHORT
                    ).show()
                    preferences["COLOR_FAB"] =
                        ContextCompat.getColor(applicationContext, R.color.green)
                    updateColorFab()
                } else if (preferences["COLOR_FAB", -1] == colorRed) {
                    val dialog = MaterialAlertDialogBuilder(this@MainActivity).create()
                    val inflater = LayoutInflater.from(this@MainActivity)
                    val view = inflater.inflate(R.layout.fragment_login, null)
                    view.findViewById<TextView>(R.id.title_inicio).text =
                        "Token expirado, inicie sesión nuevamente"
                    view.findViewById<Button>(R.id.btn_crear_usuario).visibility = View.GONE
                    val mail = view.findViewById<EditText>(R.id.edit_mail)
                    mail.setText(preferences["email", ""])
                    dialog.setView(view)
                    val pwd = view.findViewById<EditText>(R.id.edit_password)
                    view.findViewById<Button>(R.id.btn_iniciar_sesion).setOnClickListener() {
                        lifecycleScope.launch {
                            if (
                                performLogin(
                                    mail.text.toString(),
                                    pwd.text.toString(),
                                    this@MainActivity,
                                    authService
                                ) && performSync(this@MainActivity)
                            ) {
                                makeText(
                                    this@MainActivity,
                                    "Se ha sincronizado correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()
                            } else {
                                makeText(
                                    this@MainActivity,
                                    "Inicio de sesion fallido",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    dialog.show()
                } else
                    makeText(
                        this@MainActivity,
                        "Sincronización fallida",
                        Toast.LENGTH_SHORT
                    ).show()
                updateColorFab()
                // cancel animation after 1 second
                delay(1000)
                binding.fabSync.animate().cancel()
            }
        }
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        binding.navView.visibility = visibility
        binding.fabSync.visibility = visibility
    }

    override fun onBackPressed() {
        val preferences = PreferenceHelper.defaultPrefs(this)
        if (!preferences["jwt", ""].contains(".")) {
            return
        }

        val bottomNavigationView = binding.navView
        val selectedItemId = bottomNavigationView.selectedItemId
        if (R.id.navigation_active != selectedItemId) {
            bottomNavigationView.selectedItemId = R.id.navigation_active
            return
        }
        super.onBackPressed()
    }

    fun getFab(): FloatingActionButton {
        return binding.fabSync
    }

    //TODO refactor
    fun updateColorFab() {
        val preferences = PreferenceHelper.defaultPrefs(this)
        val color = preferences["COLOR_FAB", -1]
        val ids = preferences["VISIT_IDS", ""]

        if (color != -1) {
            binding.fabSync.backgroundTintList = ColorStateList.valueOf(color)
        } else if (ids == "") {
            binding.fabSync.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.green))
        } else {
            binding.fabSync.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.yellow))
        }

        Log.i("MainActivity", "updateColorFab: ${preferences["COLOR_FAB", -1]}")
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getVisits(
        header: String,
        context: Context,
        visitService: VisitService,
    ): List<AppVisit> {
        var visits: List<AppVisit> = emptyList()

        try {
            val response = visitService.getVisits(header)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                visits = body
                visitsDBViewModel.clearVisits()
                visitsDBViewModel.insertVisits(visits)
                visits = updateVisitsWithLocalChanges(context, visits)
                Log.i("SPGUNLP_TAG", "getVisits: made api call and was successful")
            } else if (response.code() == 401 || response.code() == 403) {
                visits = GlobalScope.async {
                    return@async visitsDBViewModel.getAllVisits()
                }.await()
            }
        } catch (e: Exception) {
            Log.e("SPGUNLP_TAG", e.message.toString())
            visits = GlobalScope.async {
                return@async visitsDBViewModel.getAllVisits()
            }.await()
        }

        return visits
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun updateVisitsWithLocalChanges(context: Context, visits: List<AppVisit>): List<AppVisit> {
        val visitsUpdates = mutableListOf<AppVisit>()
        val preferences = PreferenceHelper.defaultPrefs(context)
        val email: String = preferences["email"]
        val updates = GlobalScope.async {
            return@async visitUpdateViewModel.getVisitsByEmail(email)
        }.await()
        visits.forEach { visit ->
            val visitFound = updates.find {
                it.visitId == visit.id
            }
            if (visitFound != null) {
                visitsUpdates.add(updateVisitWithLocalVisitUpdate(visit, visitFound.visit))
            } else {
                visitsUpdates.add(visit)
            }
        }

        return visitsUpdates.toList()
    }

    private fun updateVisitWithLocalVisitUpdate(visit: AppVisit, visitUpdate: AppVisitUpdate): AppVisit{
        val newParameters = mutableListOf<AppVisitParameters>()
        visit.visitaParametrosResponse?.forEach {parameter ->
           val parFound = visitUpdate.parametros?.find { it.parametroId == parameter.parametro?.id }
            if (parFound != null){
                newParameters.add(
                    AppVisitParameters(
                        parFound.aspiracionesFamiliares,
                        parFound.comentarios,
                        parFound.cumple,
                        parameter.id,
                        parameter.nombre,
                        parameter.parametro,
                        parFound.sugerencias,
                        visit.id
                    )
                )
            } else {
                newParameters.add(parameter)
            }

        }

        return AppVisit(
            visit.id,
            visit.comentarioImagenes,
            visit.estadoVisita,
            visit.fechaActualizacion,
            visit.fechaCreacion,
            visitUpdate.fechaVisita,
            visit.imagenes,
            visit.integrantes,
            visit.quintaResponse,
            visit.usuarioOperacion,
            newParameters
        )
    }
}