package com.example.spgunlp.ui.inactive

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.spgunlp.databinding.ActivityMainBinding
import com.example.spgunlp.databinding.FragmentActiveBinding
import com.example.spgunlp.databinding.FragmentInactiveBinding
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.IS_ACTIVE
import com.example.spgunlp.model.VISIT_ITEM
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.active.VisitClickListener
import com.example.spgunlp.ui.visit.VisitActivity
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.getVisits
import com.example.spgunlp.util.updateRecycler
import com.google.gson.Gson
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class InactiveFragment : BaseFragment(), VisitClickListener {
    private val visitService: VisitService by lazy {
        VisitService.create()
    }

    val visitList = mutableListOf<AppVisit>()

    private var _binding: FragmentInactiveBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var visitLayout: FragmentActiveBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inactiveViewModel =
            ViewModelProvider(this).get(InactiveViewModel::class.java)

        _binding = FragmentInactiveBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textInactive
        //inactiveViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}
        visitLayout = binding.layoutVisits
        visitLayout.titleActive.text = "Historial de visitas"

        populateVisits()

        visitLayout.searchView.clearFocus()

        visitLayout.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    visitLayout.activeList, filteredList,
                    activity, this@InactiveFragment
                )
                return true
            }
        })
        return root
    }

    private fun populateVisits() {
        lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            if (!jwt.contains("."))
                cancel()
            val header = "Bearer $jwt"
            val visits = getVisits(header, requireContext(), visitService)
            inactiveVisits(visits)
            updateRecycler(
                visitLayout.activeList, visitList, activity, this@InactiveFragment
            )
        }
    }

    private fun inactiveVisits(visits: List<AppVisit>) {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val email = preferences["email", ""]
        val filteredVisits = visits.filter { visit ->
            visit.estadoVisita == "CERRADA"
        }
        visitList.addAll(filteredVisits)
    }

    override fun onClick(visit: AppVisit) {
        val intent = Intent(requireActivity(), VisitActivity::class.java)
        val gson = Gson()
        val visitGson = gson.toJson(visit)
        intent.putExtra(VISIT_ITEM, visitGson)
        intent.putExtra(IS_ACTIVE, false)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        visitList.clear()
        _binding = null
    }
}