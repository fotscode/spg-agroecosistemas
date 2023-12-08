package com.example.spgunlp.ui.visit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spgunlp.databinding.FragmentParametersBinding
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.model.AppVisitUpdate
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ParametersFragment(principleName: String): BaseFragment(), ParameterClickListener {
    private val visitService: VisitService by lazy {
        VisitService.create()
    }

    private val parameterViewModel: ParametersViewModel by activityViewModels()
    private val visitViewModel: VisitViewModel by activityViewModels()

    private var _binding: FragmentParametersBinding? = null

    private val binding get() = _binding!!
    private val parametersList = mutableListOf<AppVisitParameters>()
    private val principleName = principleName

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

        binding.detailTitle.text = principleName

        populateParameters()

        binding.btnSave.setOnClickListener(){
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(binding.btnSave.text)
                .setMessage("¿Está seguro que desea guardar los cambios realizados?")
                .setNegativeButton("Cancelar") { dialog, which ->
                }
                .setPositiveButton("Aceptar") { dialog, which ->

                    updateParameterList()
                    updateVisitParameters(parametersList)

                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(this.id, PrinciplesFragment())
                        .commit()
                }
                .show()

        }
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
            adapter = ParametersAdapter(list, this@ParametersFragment)
        }
    }

    private fun updateParameterList(){

        val adapter = binding.parametersList.adapter as ParametersAdapter
        val newParameters = parametersList.mapIndexed { index, par -> par.copy(cumple = adapter.getCheckedMap()[index]?:false) }

        parametersList.clear()
        parametersList.addAll(newParameters)
    }

    private fun updateVisitParameters(parameters: List<AppVisitParameters>){

        val parametersUpdate = mutableListOf<AppVisitUpdate.ParametersUpdate>()

        parameters.forEach { it ->
            parametersUpdate.add(AppVisitUpdate.ParametersUpdate(it.aspiracionesFamiliares, it.comentarios, it.cumple, it.id, it.sugerencias))
        }

        val idIntegrantes = visitViewModel.membersList.value?.map { it ->
            it.id?:0
        }

        val visitToUpdate = AppVisitUpdate(visitViewModel.unformattedVisitDate.value, idIntegrantes, parametersUpdate, visitViewModel.countryId.value)

        // make the call to the remote API with coroutines
        lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            if (!jwt.contains("."))
                cancel()
            val header="Bearer $jwt"

            val id = visitViewModel.id.value?:0

            val response = visitService.updateVisitById(header, id, visitToUpdate)

            Log.d("TAG", response.code().toString())
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Toast.makeText(requireContext(), "Los cambios han sido guardados con éxito", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error: los cambios no fueron guardados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(parameter: AppVisitParameters) {

    }
}
