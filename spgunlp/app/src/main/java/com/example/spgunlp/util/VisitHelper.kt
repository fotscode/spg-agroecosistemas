package com.example.spgunlp.util

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spgunlp.R
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.ui.active.VisitAdapter
import com.example.spgunlp.ui.active.VisitClickListener
import com.example.spgunlp.ui.visit.BundleViewModel
import com.example.spgunlp.util.PreferenceHelper.set
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
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
    viewModel: BundleViewModel,
    context: Context
): List<AppVisitParameters.Principle> {
    var principles: List<AppVisitParameters.Principle> = emptyList()
    val dao = AppDatabase.getDatabase(context).principlesDao()
    try {
        val response = visitService.getPrinciples(header)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            principles = body
            viewModel.updatePrinciplesList(principles)
            Log.i("SPGUNLP_TAG", "getPrinciples: made api call and was successful")
        } else if (response.code() == 401 || response.code() == 403) {
            principles = withContext(Dispatchers.IO){
                dao.getPrinciples()
            }
        }
    } catch (e: Exception) {
        Log.e("SPGUNLP_TAG", e.message.toString())
        principles = withContext(Dispatchers.IO) {
            dao.getPrinciples()
        }
    }
    return principles
}

suspend fun syncPrinciplesWithDB(
    header: String,
    visitService: VisitService,
    context: Context
) {
    try {
        val response = visitService.getPrinciples(header)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val dao = AppDatabase.getDatabase(context).principlesDao()
            dao.clearPrinciples()
            dao.insertPrinciples(body)
            Log.i("SPGUNLP_TAG", "sync Principles with DB: made api call and was successful")
        } else if (response.code() == 401 || response.code() == 403) {
            Log.i("SPGUNLP_TAG", "couldn't sync Principles with DB")
        }
    } catch (e: Exception) {
        Log.e("SPGUNLP_TAG", e.message.toString())
    }
}
