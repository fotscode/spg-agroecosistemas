package com.example.spgunlp.ui.visit

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spgunlp.databinding.FragmentParametersBinding
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.model.AppVisitUpdate
import com.example.spgunlp.model.MODIFIED_VISIT
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.PreferenceHelper.set
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Date

class ParametersFragment(private val principleName: String) : BaseFragment(),
    ParameterClickListener {
    private val visitService: VisitService by lazy {
        VisitService.create()
    }

    private val parameterViewModel: ParametersViewModel by activityViewModels()
    private val visitViewModel: VisitViewModel by activityViewModels()

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

        binding.detailTitle.text = principleName

        populateParameters()

        binding.btnSave.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(binding.btnSave.text)
                .setMessage("¿Está seguro que desea guardar los cambios realizados?")
                .setNegativeButton("Cancelar") { _, _ ->
                }
                .setPositiveButton("Aceptar") { _, _ ->
                    updateParameterList()
                    updateVisitParameters()
                }
                .show()
        }

        binding.btnCancel.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(binding.btnCancel.text)
                .setMessage("¿Está seguro que desea volver atrás? Los cambios realizados no serán guardados")
                .setNegativeButton("Cancelar") { _, _ ->
                }
                .setPositiveButton("Aceptar") { _, _ ->
                    goToPrincipleFragment()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populateParameters() {

        parameterViewModel.parameters.observe(viewLifecycleOwner) { value ->
            value?.forEach {
                if (it != null) {
                    this.parametersList.add(it)
                }
            }
        }

        updateRecycler(parametersList)
    }

    private fun updateRecycler(list: List<AppVisitParameters>) {
        binding.parametersList.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ParametersAdapter(list, this@ParametersFragment)
        }
    }

    private fun updateParameterList() {

        val adapter = binding.parametersList.adapter as ParametersAdapter
        val newParameters = parametersList.mapIndexed { index, par ->
            par.copy(
                cumple = adapter.getCheckedMap()[index] ?: false
            )
        }

        parametersList.clear()
        parametersList.addAll(newParameters)
    }

    private fun updateVisitParameters() {
        lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            if (!jwt.contains("."))
                cancel()
            val header = "Bearer $jwt"

            try {
                val response = getVisitParametersUpdated(header)
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    Toast.makeText(
                        requireContext(),
                        "Los cambios han sido guardados con éxito",
                        Toast.LENGTH_SHORT
                    ).show()
                    (activity as VisitActivity).updateVisit(body)
                    preferences["COLOR_FAB"] = Color.GREEN
                } else {
                    updatePreferences()
                    preferences["COLOR_FAB"] = Color.RED
                    Toast.makeText(
                        requireContext(),
                        "Error: los cambios no fueron guardados",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                updatePreferences()
                preferences["COLOR_FAB"] = Color.YELLOW
            }

            goToPrincipleFragment()

        }
    }

    private suspend fun getVisitParametersUpdated(header: String): Response<AppVisit> {
        val visitId = visitViewModel.id.value ?: 0
        val visitToUpdate = getAppVisitUpdate()

        return visitService.updateVisitById(header, visitId, visitToUpdate)
    }

    private fun getAppVisitUpdate(): AppVisitUpdate {

        val parametersUpdate = mutableListOf<AppVisitUpdate.ParametersUpdate>()

        parametersList.forEach {
            parametersUpdate.add(
                AppVisitUpdate.ParametersUpdate(
                    it.aspiracionesFamiliares,
                    it.comentarios,
                    it.cumple,
                    it.parametro?.id,
                    it.sugerencias
                )
            )
        }

        val idMembers = visitViewModel.membersList.value?.map {
            it.id ?: 0
        }

        val visitToUpdate = AppVisitUpdate(
            visitViewModel.unformattedVisitDate.value,
            idMembers,
            parametersUpdate,
            visitViewModel.countryId.value
        )
        return visitToUpdate
    }

    fun goToPrincipleFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(this.id, PrinciplesFragment())
            .commit()
    }

    override fun onClick(parameter: AppVisitParameters) {

    }

    fun updatePreferences() {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val visitId = visitViewModel.id.value ?: 0
        val visitToUpdate = getAppVisitUpdate()
        val gson = Gson()
        val visitGson = gson.toJson(visitToUpdate)
        val label = visitId.toString() + "_" + MODIFIED_VISIT
        preferences[label] = visitGson
        preferences["VISIT_IDS"] = visitId.toString()+","+preferences["VISIT_IDS",""]
    }
}

