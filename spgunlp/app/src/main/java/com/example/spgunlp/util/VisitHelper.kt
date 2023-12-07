package com.example.spgunlp.util

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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