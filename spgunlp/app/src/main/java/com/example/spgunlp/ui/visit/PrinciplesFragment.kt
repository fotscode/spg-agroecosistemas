package com.example.spgunlp.ui.visit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

class PrinciplesFragment : BaseFragment(), PrincipleClickListener {
    private val visitService: VisitService by lazy {
        VisitService.create()
    }

    private var _binding: FragmentPrinciplesBinding? = null

    private val binding get() = _binding!!
    private val principlesList = mutableListOf<AppVisitParameters.Principle>()
    private val statesList = mutableListOf<Boolean>()
    private val parametersViewModel: ParametersViewModel by activityViewModels()
    private val bundleViewModel: BundleViewModel by activityViewModels()

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

        if (bundleViewModel.isPrinciplesStateEmpty()) {
            principlesList.clear()
            statesList.clear()
            populatePrinciples()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bundleViewModel.savePrinciplesState(principlesList, statesList)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            bundleViewModel.getPrinciplesList()?.let { principlesList.addAll(it) }
            bundleViewModel.getStatesList()?.let { statesList.addAll(it) }
            updateRecycler(principlesList, statesList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populatePrinciples() {
        lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            val header = "Bearer $jwt"
            val principles = getPrinciples(header, visitService)
            activePrinciples(principles)
            parametersViewModel.parameters.observe(viewLifecycleOwner, Observer { value ->
                val parametersMap =
                    value?.groupBy { it?.parametro?.principioAgroecologico?.id }
                principlesList?.forEach { principle ->
                    statesList.add(
                        parametersMap?.get(principle.id)?.all { it?.cumple == true }
                            ?: true)
                }
            })
            updateRecycler(principlesList, statesList)
        }
    }

    private suspend fun getPrinciples(
        header: String,
        visitService: VisitService,
    ): List<AppVisitParameters.Principle> {
        var principles: List<AppVisitParameters.Principle> = emptyList()
        try {
            val response = visitService.getPrinciples(header)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                principles = body
                bundleViewModel.updatePrinciplesList(principles)
                Log.i("SPGUNLP_TAG", "getPrinciples: made api call and was successful")
            } else if (response.code() == 401 || response.code() == 403) {
                principles = bundleViewModel.getPrinciplesList()!!
            }
        } catch (e: Exception) {
            Log.e("SPGUNLP_TAG", e.message.toString())
            principles = bundleViewModel.getPrinciplesList()!!
        }
        return principles
    }

    private fun activePrinciples(principles: List<AppVisitParameters.Principle>) {
        val filteredPrinciples = principles.filter {
            it.habilitado == true
        }
        principlesList.addAll(filteredPrinciples)
    }

    private fun updateRecycler(
        principles: List<AppVisitParameters.Principle>,
        states: List<Boolean>
    ) {
        binding.principlesList.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = PrinciplesAdapter(principles, states, this@PrinciplesFragment)
        }
    }

    override fun onClickChecklist(principle: AppVisitParameters.Principle) {
        bundleViewModel.clearPrinciplesState()
        bundleViewModel.clearParametersState()

        val parametersList = mutableListOf<AppVisitParameters>()
        parametersViewModel.parameters.observe(viewLifecycleOwner) { value ->
            value?.forEach {
                if (it != null && it.parametro?.principioAgroecologico?.id == principle.id) {
                    parametersList.add(it)
                }
            }
        }
        if (parametersList.isNotEmpty()) {
            parametersViewModel.setParametersCurrentPrinciple(parametersList)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(this.id, ParametersFragment())
                .addToBackStack(null)
                .commit()
        } else Toast.makeText(
            requireContext(),
            "El principio seleccionado no dispone de parámetros",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onClickObservations(principle: AppVisitParameters.Principle) {
        bundleViewModel.clearPrinciplesState()
        bundleViewModel.clearObservationsState()
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val name = principle.nombre ?: "Unamed"
        val id = principle.id ?: 0
        (activity as VisitActivity).updateMessagesViewModel(id)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(this.id, ObservationsFragment(id,name,preferences["email"]))
            .addToBackStack(null)
            .commit()
    }
}
