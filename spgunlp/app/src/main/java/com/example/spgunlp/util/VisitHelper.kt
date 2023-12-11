package com.example.spgunlp.util

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spgunlp.R
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.ui.active.VisitAdapter
import com.example.spgunlp.ui.active.VisitClickListener
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

fun updatePreferences(visits: List<AppVisit>, context: Context) {
    val currentDate = Date().time
    val preferences = PreferenceHelper.defaultPrefs(context)
    val gson = Gson()
    val visitsGson = gson.toJson(visits)
    preferences["LIST_VISITS"] = visitsGson
    preferences["LAST_UPDATE"] = currentDate
}

fun getPreferences(context: Context): List<AppVisit> {
    val preferences = PreferenceHelper.defaultPrefs(context)
    val gson = Gson()
    val visitsGson = preferences["LIST_VISITS", ""]
    if (visitsGson == "")
        return emptyList()
    val type = object : TypeToken<List<AppVisit>>() {}.type
    return gson.fromJson(visitsGson, type)
}

suspend fun getVisits(
    header: String,
    context: Context,
    visitService: VisitService,
    useSaved: Boolean
): List<AppVisit> {
    var visits: List<AppVisit> = emptyList()
    val lastUpdate = PreferenceHelper.defaultPrefs(context)["LAST_UPDATE", 0L]
    val currentDate = Date().time

    if (currentDate - lastUpdate < 300000 && useSaved) {// 5mins
        Log.i("SPGUNLP_TAG", "getVisits: last update less than 5 mins")
        visits = getPreferences(context)
        return visits
    }

    try {
        val response = visitService.getVisits(header)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            visits = body
            updatePreferences(visits, context)
            Log.i("SPGUNLP_TAG", "getVisits: made api call and was successful")
        } else if (response.code() == 401 || response.code() == 403) {
            visits = getPreferences(context)
        }
    } catch (e: Exception) {
        Log.e("SPGUNLP_TAG", e.message.toString())
        visits = getPreferences(context)
    }
    return visits
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

fun updatePreferencesPrinciple(principles: List<AppVisitParameters.Principle>, context: Context) {
    val currentDate = Date().time
    val preferences = PreferenceHelper.defaultPrefs(context)
    val gson = Gson()
    val principlesGson = gson.toJson(principles)
    preferences["PRINCIPLES"] = principlesGson
    preferences["UPDATE_PRINCIPLES"] = currentDate
}

fun getPreferencesPrinciple(context: Context): List<AppVisitParameters.Principle> {
    val preferences = PreferenceHelper.defaultPrefs(context)
    val gson = Gson()
    val principlesGson = preferences["PRINCIPLES", ""]
    if (principlesGson == "")
        return emptyList()
    val type = object : TypeToken<List<AppVisitParameters.Principle>>() {}.type
    return gson.fromJson(principlesGson, type)
}

suspend fun getPrinciples(
    header: String,
    context: Context,
    visitService: VisitService,
    useSaved:Boolean
): List<AppVisitParameters.Principle> {
    var principles: List<AppVisitParameters.Principle> = emptyList()
    val lastUpdate = PreferenceHelper.defaultPrefs(context)["UPDATE_PRINCIPLES", 0L]
    val currentDate = Date().time

    if (currentDate - lastUpdate < 300000 && useSaved) {// 5mins
        Log.i("SPGUNLP_TAG", "getPrinciples: last update less than 5 mins")
        principles = getPreferencesPrinciple(context)
        return principles
    }
    try {
        val response = visitService.getPrinciples(header)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            principles = body
            updatePreferencesPrinciple(principles, context)
            Log.i("SPGUNLP_TAG", "getPrinciples: made api call and was successful")
        } else if (response.code() == 401 || response.code() == 403) {
            principles = getPreferencesPrinciple(context)
        }
    } catch (e: Exception) {
        Log.e("SPGUNLP_TAG", e.message.toString())
        principles = getPreferencesPrinciple(context)
    }
    return principles
}