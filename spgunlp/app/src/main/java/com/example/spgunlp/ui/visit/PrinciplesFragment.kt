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
import com.example.spgunlp.R
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

        if (savedInstanceState == null){ //TODO fix
            populatePrinciples()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save your variables or data here
        Log.d("observations", "guarda")
        bundleViewModel.savePrinciplesList(principlesList)
        bundleViewModel.saveStatesList(statesList)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            // Restore your variables or data here
            Log.d("observations", "lee")
            bundleViewModel.getPrinciplesList()?.let { principlesList.addAll(it) }
            bundleViewModel.getStatesList()?.let { statesList.addAll(it) }
            Log.d("observations", principlesList.toString())
            Log.d("observations", statesList.toString())
        }
    }

    private fun populatePrinciples() {
        lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            val header = "Bearer $jwt"
            val response = visitService.getPrinciples(header)
            Logger.getGlobal().log(Level.INFO, "Response: $response")
            val principles = response.body()
            Logger.getGlobal().log(Level.SEVERE, principles.toString())
            if (principles != null) {
                activePrinciples(principles)
            }
            parametersViewModel.parameters.observe(viewLifecycleOwner, Observer { value ->
                val parametersMap = value?.groupBy { it?.parametro?.principioAgroecologico?.id }
                principlesList?.forEach { principle ->
                    statesList.add(parametersMap?.get(principle.id)?.all { it?.cumple == true }
                        ?: true)
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

    override fun onClickChecklist(principle: AppVisitParameters.Principle) {
        bundleViewModel.clearState() //TODO remove
        val name = principle.nombre?: "Unamed"
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(this.id, ParametersFragment(name))
            .addToBackStack(null)
            .commit()
    }

    override fun onClickObservations(principle: AppVisitParameters.Principle) {
        bundleViewModel.clearState() //TODO remove
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
