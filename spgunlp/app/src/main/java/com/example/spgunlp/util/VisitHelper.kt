package com.example.spgunlp.util

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.ui.active.VisitAdapter
import com.example.spgunlp.ui.active.VisitClickListener
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
suspend fun getVisits(header: String, context: Context, visitService:VisitService): List<AppVisit> {
    var visits: List<AppVisit> = emptyList()
    val lastUpdate = PreferenceHelper.defaultPrefs(context)["LAST_UPDATE", 0L]
    val currentDate = Date().time

    if (currentDate - lastUpdate < 300000) {// 5mins
        Log.i("SPGUNLP_TAG", "getVisits: last update less than 5 mins")
        visits = getPreferences(context)
        return visits
    }

    try {
        val response = visitService.getVisits(header)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            visits = body
            updatePreferences(visits,context)
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