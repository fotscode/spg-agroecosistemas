package com.example.spgunlp.ui.visit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.spgunlp.R
import com.example.spgunlp.databinding.FragmentVisitBinding
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.active.ActiveFragment

class VisitFragment : BaseFragment() {

    private var _binding: FragmentVisitBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val visitViewModel =
                ViewModelProvider(this).get(VisitViewModel::class.java)

        _binding = FragmentVisitBinding.inflate(inflater, container, false)
        val root: View = binding.root

        visitViewModel.nameProducer.observe(viewLifecycleOwner){
            binding.nameProducer.text = it
        }
        visitViewModel.members.observe(viewLifecycleOwner){
            binding.members.text = it
        }
        visitViewModel.visitDate.observe(viewLifecycleOwner){
            binding.visitDate.text = it
        }
        visitViewModel.surfaceCountry.observe(viewLifecycleOwner){
            binding.surfaceCountry.text = it
        }
        visitViewModel.surfaceAgro.observe(viewLifecycleOwner){
            binding.surfaceAgro.text = it
        }

        binding.btnPrinciples.setOnClickListener(){
            val newFragment= PrinciplesFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_activity_main, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}