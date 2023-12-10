package com.example.spgunlp.ui.active

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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
import com.example.spgunlp.util.calendar
import com.example.spgunlp.util.getVisits
import com.example.spgunlp.util.updateRecycler
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

    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activeViewModel =
            ViewModelProvider(this).get(ActiveViewModel::class.java)

        _binding = FragmentActiveBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.searchView.clearFocus()

        binding.btnCalendario.setOnClickListener() {
            calendar(
                parentFragmentManager,
                visitList,
                this@ActiveFragment,
                binding.activeList,
                requireActivity()
            ).onClick(it)
        }

        populateVisits()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = visitList.filter { visit ->
                    val searchQuery = newText?.lowercase() ?: ""
                    val quinta = visit.quintaResponse
                    val quintaName = quinta?.organizacion?.lowercase()
                    val quintaProductor = quinta?.nombreProductor?.lowercase()
                    quintaName?.contains(searchQuery)!! || quintaProductor?.contains(searchQuery)!!
                }
                updateRecycler(
                    binding.activeList, filteredList,
                    activity, this@ActiveFragment
                )
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
    override fun onResume() {
        super.onResume()
        populateVisits()
    }

    private fun populateVisits() {
        lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            if (!jwt.contains("."))
                cancel()
            val header = "Bearer $jwt"
            val visits = getVisits(header, requireContext(), visitService)
            activeVisits(visits)
            updateRecycler(
                binding.activeList, visitList,
                activity, this@ActiveFragment
            )
        }
    }

    private fun activeVisits(visits: List<AppVisit>) {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val email = preferences["email", ""]
        val filteredVisits = visits.filter { visit ->
            visit.estadoVisita == "ABIERTA" && visit.integrantes!!.filter { integrante ->
                integrante.email == email
            }.isNotEmpty()
        }
        visitList.clear()
        visitList.addAll(filteredVisits.sortedBy { it.fechaVisita })
    }

    override fun onClick(visit: AppVisit) {
        val intent = Intent(requireActivity(), VisitActivity::class.java)
        val gson = Gson()
        val visitGson = gson.toJson(visit)
        intent.putExtra(VISIT_ITEM, visitGson)
        startActivity(intent)
    }
}