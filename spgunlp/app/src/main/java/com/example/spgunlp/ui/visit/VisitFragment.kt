package com.example.spgunlp.ui.visit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.spgunlp.databinding.FragmentVisitBinding
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.util.PreferenceHelper

class VisitFragment : BaseFragment() {

    private var _binding: FragmentVisitBinding? = null
    private val visitViewModel: VisitViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisitBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visitViewModel.nameProducer.observe(viewLifecycleOwner, Observer { value ->
            binding.nameProducer.text = value
        })

        visitViewModel.members.observe(viewLifecycleOwner, Observer { value ->
            binding.members.text = value
        })

        visitViewModel.visitDate.observe(viewLifecycleOwner, Observer { value ->
            binding.visitDate.text = value
        })

        visitViewModel.surfaceCountry.observe(viewLifecycleOwner, Observer { value ->
            binding.surfaceCountry.text = value.toString()
        })

        visitViewModel.surfaceAgro.observe(viewLifecycleOwner, Observer { value ->
            binding.surfaceAgro.text = value.toString()
        })

        binding.btnPrinciples.setOnClickListener(){
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(this.id, PrinciplesFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}