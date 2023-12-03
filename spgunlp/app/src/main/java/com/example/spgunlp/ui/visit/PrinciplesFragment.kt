package com.example.spgunlp.ui.visit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.spgunlp.databinding.FragmentPrinciplesBinding
import com.example.spgunlp.ui.BaseFragment

class PrinciplesFragment: BaseFragment() {

    private var _binding: FragmentPrinciplesBinding? = null

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

        _binding = FragmentPrinciplesBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
