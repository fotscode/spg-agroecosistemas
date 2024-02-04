package com.example.spgunlp.ui.active

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.spgunlp.MainActivity
import com.example.spgunlp.databinding.FragmentActiveBinding
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.VISIT_ITEM
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.visit.VisitActivity
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PrinciplesViewModel
import com.example.spgunlp.util.calendar
import com.example.spgunlp.util.getPrinciples
import com.example.spgunlp.util.updateRecycler
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ActiveFragment : BaseFragment(), VisitClickListener {
    private val visitService: VisitService by lazy {
        VisitService.create()
    }

    private var _binding: FragmentActiveBinding? = null
    private val activeViewModel: ActiveViewModel by activityViewModels()
    private val principlesViewModel: PrinciplesViewModel by activityViewModels()
    val visitList = mutableListOf<AppVisit>()
    private lateinit var jobToKill: Job
    private val binding get() = _binding!!

    private lateinit var listenerPreferences: SharedPreferences.OnSharedPreferenceChangeListener
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        // observes the jwt changes
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        listenerPreferences=SharedPreferences.OnSharedPreferenceChangeListener{ sharedPreferences, key ->
            if (key == "jwt") {
                val jwt = sharedPreferences.getString(key, "")
                if (jwt != null && jwt.contains(".")) {
                    Log.i("ActiveFragment", "jwt changed")
                    populateVisits()
                }
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listenerPreferences)

        if (activeViewModel.isActiveVisitListEmpty()) {
            visitList.clear()
            populateVisits()
        }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        activeViewModel.saveActiveVisits(this.visitList)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            activeViewModel.getActiveVisits()?.let { this.visitList.addAll(it) }
            updateRecycler(
                binding.activeList, visitList,
                activity, this@ActiveFragment
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::jobToKill.isInitialized)
            jobToKill.cancel()
        visitList.clear()
        _binding = null
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        preferences.unregisterOnSharedPreferenceChangeListener(listenerPreferences)
    }

    override fun onResume() {
        super.onResume()
        populateVisits()
    }

    private fun populateVisits() {
        jobToKill = lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            if (!jwt.contains("."))
                cancel()
            val header = "Bearer $jwt"
            val visits =
                (activity as MainActivity).getVisits(header, requireContext(), visitService, true)
            getPrinciples(header, requireContext(), visitService, true, principlesViewModel)
            activeVisits(visits)
            if (_binding != null) {
                updateRecycler(
                    binding.activeList, visitList,
                    activity, this@ActiveFragment
                )
            }
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
        visitList.addAll(filteredVisits.sortedBy { it.fechaActualizacion }.reversed())
    }

    override fun onClick(visit: AppVisit) {
        val intent = Intent(requireActivity(), VisitActivity::class.java)
        intent.putExtra(VISIT_ITEM, visit)
        startActivity(intent)
    }
}