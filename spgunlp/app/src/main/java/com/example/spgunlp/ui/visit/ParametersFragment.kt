package com.example.spgunlp.ui.visit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spgunlp.databinding.FragmentParametersBinding
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.ui.BaseFragment

class ParametersFragment: BaseFragment(), ParameterClickListener {
    private val visitService: VisitService by lazy {
        VisitService.create()
    }

    private val parameterViewModel: ParametersViewModel by activityViewModels()

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

        parameterViewModel.parameters.observe(viewLifecycleOwner, Observer { value ->
            value?.forEach{ it ->
                if (it != null) {
                    this.parametersList.add(it)
                }
            }
        })

        updateRecycler(parametersList)
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
