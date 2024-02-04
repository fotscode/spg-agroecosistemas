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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.spgunlp.databinding.ActivityMainBinding
import com.example.spgunlp.io.AuthService
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.io.sync.AndroidAlarmScheduler
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import com.example.spgunlp.util.PrinciplesViewModel
import com.example.spgunlp.util.VisitsViewModel
import com.example.spgunlp.util.performLogin
import com.example.spgunlp.util.performSync
import com.example.spgunlp.util.updatePreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : AppCompatActivity() {
    private val authService: AuthService by lazy {
        AuthService.create()
    }
    private val visitService: VisitService by lazy {
        VisitService.create()
    }

    private lateinit var binding: ActivityMainBinding

    private val principlesViewModel: PrinciplesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scheduler = AndroidAlarmScheduler(this)
        scheduler.schedule()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //getSupportActionBar()?.hide()

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_active,
                R.id.navigation_inactive,
                R.id.navigation_profile,
                R.id.navigation_stats
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
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
                    makeLoginPopup()
                } else
                    Toast.makeText(
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

    override fun onDestroy() {
        super.onDestroy()
        val visitsViewModel: VisitsViewModel by viewModels()
        visitsViewModel.clearVisitList()
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

    suspend fun getVisits(
        header: String,
        context: Context,
        visitService: VisitService,
        useSaved: Boolean,
    ): List<AppVisit> {
        var visits: List<AppVisit> = emptyList()
        val lastUpdate = PreferenceHelper.defaultPrefs(context)["LAST_UPDATE", 0L]
        val currentDate = Date().time

        val visitsViewModel: VisitsViewModel by viewModels()

        if (currentDate - lastUpdate < 300000 && useSaved && !visitsViewModel.isVisitListEmpty()) {// 5mins
            Log.i("SPGUNLP_TAG", "getVisits: last update less than 5 mins")
            visits = visitsViewModel.getVisits() ?: emptyList()
            return visits
        }

        try {
            val response = visitService.getVisits(header)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                visits = body
                updatePreferences(context)
                visitsViewModel.saveVisits(visits)
                Log.i("SPGUNLP_TAG", "getVisits: made api call and was successful")
            } else if (response.code() == 401 || response.code() == 403) {
                //visits = visitsViewModel.getVisits() ?: emptyList()
                makeLoginPopup().also {
                    it.observe(this) { visits = it }
                }
            }
        } catch (e: Exception) {
            Log.e("SPGUNLP_TAG", e.message.toString())
            visits = visitsViewModel.getVisits() ?: emptyList()
        }
        return visits
    }

    private fun makeLoginPopup(): LiveData<List<AppVisit>>{
        val dialog = MaterialAlertDialogBuilder(this@MainActivity).create()
        val inflater = LayoutInflater.from(this@MainActivity)
        val view = inflater.inflate(R.layout.fragment_login, null)
        view.findViewById<TextView>(R.id.title_inicio).text =
            "Token expirado, inicie sesión nuevamente"
        view.findViewById<Button>(R.id.btn_crear_usuario).visibility = View.GONE
        val mail = view.findViewById<EditText>(R.id.edit_mail)
        val preferences = PreferenceHelper.defaultPrefs(this)
        mail.setText(preferences["email", ""])
        dialog.setView(view)
        val pwd = view.findViewById<EditText>(R.id.edit_password)
        val result = MutableLiveData<List<AppVisit>>()

        preferences["COLOR_FAB"] = ContextCompat.getColor(this, R.color.red)
        updateColorFab()

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
                    val header = "Bearer ${preferences["jwt", ""]}"
                    val visits = getVisits(header, this@MainActivity, visitService, false)
                    result.postValue(visits)
                    preferences["COLOR_FAB"] = ContextCompat.getColor(this@MainActivity, R.color.green)
                    updateColorFab()
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
        return result
    }
}