package com.example.spgunlp.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spgunlp.R
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.ui.active.ActiveViewModel
import com.example.spgunlp.ui.active.VisitAdapter
import com.example.spgunlp.ui.active.VisitClickListener
import com.example.spgunlp.ui.visit.BundleViewModel
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.ZonedDateTime
import java.util.Date

fun updateRecycler(
    recyler: RecyclerView,
    list: List<AppVisit>,
    activity: FragmentActivity?,
    listener: VisitClickListener
) {
    recyler.apply {
        layoutManager = LinearLayoutManager(activity)
        adapter = VisitAdapter(list, listener)
    }
}

fun updatePreferences(context: Context) {
    val currentDate = Date().time
    val preferences = PreferenceHelper.defaultPrefs(context)
    preferences["LAST_UPDATE"] = currentDate
}

@RequiresApi(Build.VERSION_CODES.O)
fun calendar(
    parentFragmentManager: FragmentManager,
    visitList: List<AppVisit>,
    listener: VisitClickListener,
    recyler: RecyclerView,
    activity: FragmentActivity?
): View.OnClickListener {
    return View.OnClickListener {

        val picker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTheme(R.style.ThemeMaterialCalendar)
                .setTitleText("Seleccionar fechas")
                .build()

        picker.show(parentFragmentManager, "DATE_RANGE_PICKER")

        picker.addOnPositiveButtonClickListener {
            val startDate = Date(it.first!!)
            val endDate = Date(it.second!!)
            val filteredList = visitList.filter { visit ->
                val date = Date.from(ZonedDateTime.parse(visit.fechaVisita).toInstant())
                date.after(startDate) && date.before(endDate)
            }
            updateRecycler(
                recyler, filteredList,
                activity, listener
            )
        }
    }
}
suspend fun getPrinciples(
    header: String,
    visitService: VisitService,
    viewModel: BundleViewModel
): List<AppVisitParameters.Principle> {
    var principles: List<AppVisitParameters.Principle> = emptyList()
    try {
        val response = visitService.getPrinciples(header)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            principles = body
            viewModel.updatePrinciplesList(principles)
            Log.i("SPGUNLP_TAG", "getPrinciples: made api call and was successful")
        } else if (response.code() == 401 || response.code() == 403) {
            principles = viewModel.getPrinciplesList()!!
        }
    } catch (e: Exception) {
        Log.e("SPGUNLP_TAG", e.message.toString())
        principles = viewModel.getPrinciplesList()!!
    }
    return principles
}
