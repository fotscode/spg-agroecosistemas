package com.example.spgunlp.ui.active

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spgunlp.R
import com.example.spgunlp.databinding.FragmentActiveBinding
import com.example.spgunlp.model.Visit
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.visit.VisitActivity

class ActiveFragment : BaseFragment() {

    val visitList = mutableListOf<Visit>()
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

        //val textView: TextView = binding.textActive
        //activeViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}

        binding.btnVisita.setOnClickListener(){
            val intent = Intent(requireActivity(), VisitActivity::class.java)
            startActivity(intent)
        }

        binding.searchView.clearFocus()

        populateVisits()
        binding.activeList.apply{
            layoutManager= LinearLayoutManager(activity)
            adapter = VisitAdapter(visitList)

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populateVisits() {
        val visit1= Visit(1,"Visita 1", R.drawable.checklist_dark)
        val visit2= Visit(2,"Visita 2", R.drawable.ic_launcher_background)
        val visit3= Visit(3,"Visita 3", R.drawable.done_dark)
        val visit4= Visit(4,"Visita 4", R.drawable.observations_dark)
        visitList.add(visit1)
        visitList.add(visit2)
        visitList.add(visit3)
        visitList.add(visit4)
    }
}