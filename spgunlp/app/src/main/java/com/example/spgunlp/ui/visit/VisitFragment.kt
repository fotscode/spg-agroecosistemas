package com.example.spgunlp.ui.visit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.example.spgunlp.databinding.FragmentVisitBinding
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.maps.MapActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class VisitFragment : BaseFragment() {

    private var _binding: FragmentVisitBinding? = null
    private val visitViewModel: VisitViewModel by activityViewModels()
    private val bundleViewModel: BundleViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisitBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var visitId: Int = 0

        visitViewModel.visit.observe(viewLifecycleOwner) { visit ->
            binding.nameProducer.text = visit.quintaResponse?.nombreProductor
            val memberValues = visit.integrantes?.map { it.nombre }
            val members= memberValues?.joinToString(separator=",")
            binding.members.text = members
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val date = LocalDateTime.parse(visit.fechaVisita, formatter)
            val dateFormatted = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            binding.visitDate.text = dateFormatted
            binding.surfaceCountry.text = visit.quintaResponse?.superficieTotalCampo.toString()
            binding.surfaceAgro.text = visit.quintaResponse?.superficieAgroecologiaCampo.toString()
            if (visit.id != null)
                visitId = visit.id
        }


        binding.btnPrinciples.setOnClickListener(){
            bundleViewModel.clearPrinciplesState()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(this.id, PrinciplesFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnMap.setOnClickListener(){
            activity?.let{
                val intent = Intent(it, MapActivity::class.java)
                intent.putExtra("ID_VISIT", visitId.toLong())
                it.startActivity(intent)
            }
        }

        binding.btnDownloadCSV.setOnClickListener(){
            Toast.makeText(requireContext(), "Proximamente...", Toast.LENGTH_SHORT).show()
        }

        binding.btnDownloadPDF.setOnClickListener(){
            Toast.makeText(requireContext(), "Proximamente...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}