package com.example.spgunlp.ui.visit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spgunlp.databinding.FragmentParametersBinding
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import kotlinx.coroutines.launch
import java.util.logging.Level
import java.util.logging.Logger

class ParametersFragment: BaseFragment(), ParameterClickListener {
    private val visitService: VisitService by lazy {
        VisitService.create()
    }

    private var _binding: FragmentParametersBinding? = null

    private val binding get() = _binding!!
    private val parametersList = mutableListOf<AppVisitParameters>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentParametersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateParameters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populateParameters() {
        lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            val header="Bearer $jwt"
            val response = visitService.getParameters(header)
            Logger.getGlobal().log(Level.INFO, "Response: $response")
            val parameters = response.body()
            Logger.getGlobal().log(Level.SEVERE, parameters.toString())
            updateRecycler(parametersList)
        }
    }

    private fun updateRecycler(list: List<AppVisitParameters>){
        binding.parametersList.apply{
            layoutManager = LinearLayoutManager(activity)
            adapter = ParametersAdapter(list,this@ParametersFragment)
        }
    }

    override fun onClick(principle: AppVisitParameters.Principle) {
        //TODO
    }
}
