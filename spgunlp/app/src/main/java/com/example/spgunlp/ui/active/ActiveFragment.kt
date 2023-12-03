package com.example.spgunlp.ui.active

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.spgunlp.databinding.FragmentActiveBinding
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.visit.VisitActivity

class ActiveFragment : BaseFragment() {

    private var _binding: FragmentActiveBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val activeViewModel =
                ViewModelProvider(this).get(ActiveViewModel::class.java)

        _binding = FragmentActiveBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textActive
        activeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.btnVisita.setOnClickListener(){
            val intent = Intent(requireActivity(), VisitActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}