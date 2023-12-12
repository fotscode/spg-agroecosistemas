package com.example.spgunlp.ui.stats

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.spgunlp.R
import com.example.spgunlp.databinding.FragmentStatsBinding
import com.example.spgunlp.ui.BaseFragment

class StatsFragment : BaseFragment() {

    private var _binding: FragmentStatsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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