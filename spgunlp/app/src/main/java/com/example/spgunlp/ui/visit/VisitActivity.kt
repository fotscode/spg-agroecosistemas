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
import com.example.spgunlp.util.PreferenceHelper.set
import kotlinx.coroutines.Job
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class VisitActivity : AppCompatActivity() {


    private lateinit var binding: ActivityVisitBinding
    private lateinit var visit: AppVisit
    private val visitViewModel: VisitViewModel by viewModels()
    private val parametersViewModel: ParametersViewModel by viewModels()
    private val bundleViewModel: BundleViewModel by viewModels()

    private lateinit var jobToKill: Job

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

    override fun onDestroy() {
        super.onDestroy()
        if (::jobToKill.isInitialized)
            jobToKill.cancel()
    }
}