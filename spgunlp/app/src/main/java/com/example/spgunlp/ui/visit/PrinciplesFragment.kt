package com.example.spgunlp.ui.visit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spgunlp.R
import com.example.spgunlp.databinding.FragmentPrinciplesBinding
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.util.PreferenceHelper
import com.example.spgunlp.util.PreferenceHelper.get
import kotlinx.coroutines.launch
import java.util.logging.Level
import java.util.logging.Logger

class PrinciplesFragment: BaseFragment() {

    private var _binding: FragmentPrinciplesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Create and set the adapter
        val items = listOf(AppVisitParameters.Principle(true, 2, "Biodiversidad"), AppVisitParameters.Principle(false, 3,"Nutrición, estructura y vida del suelo"))
        val adapter = PrinciplesAdapter(items)
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
