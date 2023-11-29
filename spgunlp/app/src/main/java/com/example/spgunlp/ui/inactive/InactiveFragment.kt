package com.example.spgunlp.ui.inactive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.spgunlp.databinding.FragmentInactiveBinding
import com.example.spgunlp.ui.BaseFragment

class InactiveFragment : BaseFragment() {

    private var _binding: FragmentInactiveBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val inactiveViewModel =
                ViewModelProvider(this).get(InactiveViewModel::class.java)

        _binding = FragmentInactiveBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textInactive
        inactiveViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}