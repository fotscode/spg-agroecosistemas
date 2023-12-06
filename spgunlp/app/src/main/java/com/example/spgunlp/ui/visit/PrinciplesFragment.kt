package com.example.spgunlp.ui.visit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
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
    private val statesList = mutableListOf<Boolean>()
    private val parametersViewModel: ParametersViewModel by activityViewModels()

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
            parametersViewModel.parameters.observe(viewLifecycleOwner, Observer { value ->
                val parametersMap = value?.groupBy{ it?.parametro?.principioAgroecologico?.id }
                principlesList?.forEach{ principle ->
                    statesList.add(parametersMap?.get(principle.id)?.all {it?.cumple == true} ?: true)
                }
            })

            updateRecycler(principlesList, statesList)
        }
    }

    private fun activePrinciples(principles: List<AppVisitParameters.Principle>) {
        val filteredPrinciples = principles.filter {
            it.habilitado == true
        }
        principlesList.addAll(filteredPrinciples)
    }

    private fun updateRecycler(principles: List<AppVisitParameters.Principle>, states: List<Boolean>){
        binding.principlesList.apply{
            layoutManager = LinearLayoutManager(activity)
            adapter = PrinciplesAdapter(principles,states, this@PrinciplesFragment)
        }
    }

    override fun onClick(principle: AppVisitParameters.Principle) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(this.id, ParametersFragment())
            .commit()
    }
}
