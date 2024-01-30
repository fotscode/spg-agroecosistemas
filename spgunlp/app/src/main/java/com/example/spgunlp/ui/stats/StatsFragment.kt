package com.example.spgunlp.ui.stats

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.spgunlp.R
import com.example.spgunlp.databinding.FragmentStatsBinding
import com.example.spgunlp.io.VisitService
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import com.example.spgunlp.util.getPrinciples
import com.example.spgunlp.util.updatePreferencesPrinciple
import kotlinx.coroutines.launch
import java.util.Date

class StatsFragment : BaseFragment() {

    private var _binding: FragmentStatsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val visitService: VisitService by lazy {
        VisitService.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

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

        lifecycleScope.launch {
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            val jwt = preferences["jwt", ""]
            val header = "Bearer $jwt"
            val principles: List<AppVisitParameters.Principle>

            try {
                val response = visitService.getPrinciples(header)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    principles = body
                    //viewModel.updatePrinciplesList(principles)
                    Log.i("SPGUNLP_TAG", "getPrinciples: made api call and was successful")
                    Log.i("SPGUNLP_TAG", "getPrinciples: ${principles.size}")
                } else if (response.code() == 401 || response.code() == 403) {
                    //principles = viewModel.getPrinciplesList()!!
                }
            } catch (e: Exception) {
                Log.e("SPGUNLP_TAG", e.message.toString())
                //principles = viewModel.getPrinciplesList()!!
            }
        }
        return root
    }

    private fun getColor(color: Int): Int {
        return ContextCompat.getColor(requireContext(), color)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}