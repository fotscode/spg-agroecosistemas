package com.example.spgunlp.ui.stats

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spgunlp.MainActivity
import com.example.spgunlp.R
import com.example.spgunlp.databinding.FragmentStatsBinding
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.visit.BundleViewModel
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.getPrinciples
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StatsFragment : BaseFragment() {

    private var _binding: FragmentStatsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val visitService: VisitService by lazy {
        VisitService.create()
    }
    private val bundleViewModel: BundleViewModel by activityViewModels()
    private var principlesList = mutableListOf<AppVisitParameters.Principle>()
    private var percentageList = mutableListOf<Float>()
    private var cumpleList = mutableListOf<Boolean>()
    private var approvedVisitsPercentage = 0f
    private lateinit var jobToKill: Job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardView.setOnClickListener() {
            binding.cardView.invalidate()
            ObjectAnimator.ofArgb(
                binding.cardView,
                "strokeColor",
                getColor(R.color.purple_200),
                getColor(R.color.green),
                getColor(R.color.teal_200),
                getColor(R.color.purple_200),
            ).apply {
                duration = 3000
                start()
            }
        }

        principlesList.clear()
        populatePrinciples()
    }

    private fun getColor(color: Int): Int {
        return ContextCompat.getColor(requireContext(), color)
    }

    private fun populatePrinciples() {
        jobToKill = lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            val header = "Bearer $jwt"
            val principles =
                getPrinciples(header, requireContext(), visitService, false, bundleViewModel)

            activePrinciples(principles)
            percentageList = MutableList(principlesList.size) { 0f }
            // get visits, for each visit, get visitParamRes, for each param get principle and cumple
            val visits =
                (activity as MainActivity).getVisits(header, requireContext(), visitService, false)
            visits.forEach {
                cumpleList = MutableList(principlesList.size) { true }
                if (it.visitaParametrosResponse != null){
                    it.visitaParametrosResponse.forEach{
                        cumpleList[it.parametro?.principioAgroecologico?.id!!-1] = cumpleList[it.parametro?.principioAgroecologico?.id!!-1] && it.cumple!!
                    }
                    cumpleList.forEachIndexed { index,cumple->
                        percentageList[index] = if (cumple) percentageList[index] + 1f/visits.size else percentageList[index]
                    }
                    approvedVisitsPercentage = if (cumpleList.all { it }) approvedVisitsPercentage + 1f/visits.size else approvedVisitsPercentage
                }
            }
            binding.percentageVisitsApproved.text= "${(approvedVisitsPercentage*100).format(2)}%"
            updateRecycler(principlesList,getFormattedPercentages(percentageList))
        }
    }

    private fun Float.format(digits: Int) = "%.${digits}f".format(this)
    private fun activePrinciples(principles: List<AppVisitParameters.Principle>) {
        val filteredPrinciples = principles.filter {
            it.habilitado == true
        }
        principlesList.addAll(filteredPrinciples)
    }

    private fun updateRecycler(
        principles: List<AppVisitParameters.Principle>,
        percentages: List<String>
    ) {
        binding.gridView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = StatsAdapter(principles,percentages)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::jobToKill.isInitialized)
            jobToKill.cancel()
        _binding = null
    }

    private fun getFormattedPercentages(percentages: List<Float>):List<String>{
        val formattedList = mutableListOf<String>()
        percentages.forEach{
            formattedList.add(String.format("%.2f", it*100)+"%")
        }
        return formattedList
    }
}