package com.example.spgunlp.ui.visit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spgunlp.databinding.FragmentPrinciplesBinding
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import kotlinx.coroutines.launch
import java.util.logging.Level
import java.util.logging.Logger

class PrinciplesFragment: BaseFragment(), PrincipleClickListener {
    private val visitService: VisitService by lazy {
        VisitService.create()
    }

    private var _binding: FragmentPrinciplesBinding? = null

    private val binding get() = _binding!!
    private val principlesList = mutableListOf<AppVisitParameters.Principle>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPrinciplesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populatePrinciples()

        /*
        binding.btnPrinciples.setOnClickListener() {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(this.id, ParametersFragment())
                .commit()
        }

         */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populatePrinciples() {
        lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            val header="Bearer $jwt"
            val response = visitService.getPrinciples(header)
            Logger.getGlobal().log(Level.INFO, "Response: $response")
            val principles = response.body()
            Logger.getGlobal().log(Level.SEVERE, principles.toString())
            if (principles != null) {
                activePrinciples(principles)
            }
            updateRecycler(principlesList)
        }
    }

    private fun activePrinciples(principles: List<AppVisitParameters.Principle>) {
        val filteredPrinciples = principles.filter {
            it.habilitado == true
        }
        principlesList.addAll(filteredPrinciples)
    }

    private fun updateRecycler(list: List<AppVisitParameters.Principle>){
        binding.principlesList.apply{
            layoutManager = LinearLayoutManager(activity)
            adapter = PrinciplesAdapter(list,this@PrinciplesFragment)
        }
    }

    override fun onClick(principle: AppVisitParameters.Principle) {
        //TODO

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(this.id, ParametersFragment())
            .commit()
    }
}
