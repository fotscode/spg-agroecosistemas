package com.example.spgunlp.ui.active

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spgunlp.databinding.FragmentActiveBinding
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.VISIT_ITEM
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.visit.VisitActivity
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Date

class ActiveFragment : BaseFragment(), VisitClickListener {
    private val visitService: VisitService by lazy {
        VisitService.create()
    }

    val visitList = mutableListOf<AppVisit>()
    private var _binding: FragmentActiveBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val activeViewModel =
                ViewModelProvider(this).get(ActiveViewModel::class.java)

        _binding = FragmentActiveBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textActive
        //activeViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}

        binding.btnVisita.setOnClickListener(){
            val intent = Intent(requireActivity(), VisitActivity::class.java)
            startActivity(intent)
        }

        binding.searchView.clearFocus()

        populateVisits()


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList=visitList.filter { visit ->
                    val searchQuery = newText?.lowercase() ?: ""
                    val quinta=visit.quintaResponse
                    val quintaName=quinta?.organizacion?.lowercase()
                    val quintaProductor=quinta?.nombreProductor?.lowercase()
                    quintaName?.contains(searchQuery)!! || quintaProductor?.contains(searchQuery)!!
                }
                updateRecycler(filteredList)
                return true
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        visitList.clear()
        _binding = null
    }

    private fun populateVisits() {
        lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            if (!jwt.contains("."))
                cancel()
            val header="Bearer $jwt"
            val visits=getVisits(header)
            activeVisits(visits)
            updateRecycler(visitList)
        }
    }
    private fun activeVisits(visits: List<AppVisit>) {
        val preferences =PreferenceHelper.defaultPrefs(requireContext())
        val email=preferences["email", ""]
        val filteredVisits = visits.filter { visit ->
            visit.estadoVisita == "ABIERTA" && visit.integrantes!!.filter { integrante ->
                integrante.email == email
            }.isNotEmpty()
        }
        visitList.addAll(filteredVisits)
    }

    private fun updatePreferences(visits: List<AppVisit>){
        val currentDate= Date().time
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val gson= Gson()
        val visitsGson=gson.toJson(visits)
        preferences["LIST_VISITS"] = visitsGson
        preferences["LAST_UPDATE"] = currentDate
    }

    private fun getPreferences(): List<AppVisit>{
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val gson= Gson()
        val visitsGson=preferences["LIST_VISITS", ""]
        if (visitsGson=="")
            return emptyList()
        val type = object : TypeToken<List<AppVisit>>() {}.type
        return gson.fromJson(visitsGson, type)
    }

    private suspend fun getVisits(header:String):List<AppVisit>{
        var visits:List<AppVisit> = emptyList()
        val lastUpdate=PreferenceHelper.defaultPrefs(requireContext())["LAST_UPDATE", 0L]
        val currentDate= Date().time

        if (currentDate-lastUpdate<300000){// 5mins
            Log.i("SPGUNLP_TAG", "getVisits: last update less than 5 mins")
            visits=getPreferences()
            return visits
        }

        try{
            val response = visitService.getVisits(header)
            val body=response.body()
            if (response.isSuccessful && body!=null){
                visits = body
                updatePreferences(visits)
                Log.i("SPGUNLP_TAG", "getVisits: made api call and was successful")
            } else if(response.code()==401 || response.code()==403){
                visits=getPreferences()
            }
        } catch (e: Exception){
            Log.e("SPGUNLP_TAG", e.message.toString())
            visits=getPreferences()
        }
        return visits
    }


    private fun updateRecycler(list: List<AppVisit>){
        binding.activeList.apply{
            layoutManager= LinearLayoutManager(activity)
            adapter = VisitAdapter(list,this@ActiveFragment)
        }
    }

    override fun onClick(visit: AppVisit) {
        val intent = Intent(requireActivity(), VisitActivity::class.java)
        val gson= Gson()
        val visitGson=gson.toJson(visit)
        intent.putExtra(VISIT_ITEM, visitGson)
        startActivity(intent)
    }
}