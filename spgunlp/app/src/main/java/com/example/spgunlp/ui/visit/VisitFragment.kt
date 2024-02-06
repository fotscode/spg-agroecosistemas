package com.example.spgunlp.ui.visit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.example.spgunlp.databinding.FragmentVisitBinding
import androidx.lifecycle.Observer
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.ui.BaseFragment
import com.example.spgunlp.ui.maps.MapActivity
import com.google.gson.Gson
import java.io.File
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

        visitViewModel.visit.observe(viewLifecycleOwner, Observer {
            binding.nameProducer.text = it.quintaResponse?.nombreProductor
            binding.visitDate.text = it.fechaVisita?.let { isoDate -> getDateFormatted(isoDate) }
            binding.members.text=it.integrantes?.map { it.nombre }?.joinToString(separator=",")
            binding.surfaceAgro.text = it.quintaResponse?.superficieAgroecologiaCampo.toString()
            binding.surfaceCountry.text = it.quintaResponse?.superficieTotalCampo.toString()
        })

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
                intent.putExtra("ID_VISIT", visitViewModel.visit.value?.id?.toLong())
                it.startActivity(intent)
            }
        }

        binding.btnDownloadJson.setOnClickListener(){
            visitViewModel.visit.value?.let { writeJsonFile(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun writeJsonFile(visit: AppVisit) {
        // write json file and then open it
        val gson = Gson()
        val json = gson.toJson(visit)
        val file = File(requireContext().filesDir, "visit.json")
        Log.i("VisitFragment", "writeJsonFile: ${file.absolutePath}")
        file.writeText(json)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val uri = FileProvider.getUriForFile(requireContext(), requireContext().applicationContext.packageName + ".provider",file);
        intent.setDataAndType(uri, "application/json")
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDateFormatted(date: String): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val date = LocalDateTime.parse(date, formatter)
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }
}