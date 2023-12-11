package com.example.spgunlp.ui.visit

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spgunlp.databinding.FragmentObsBinding
import com.example.spgunlp.model.AppMessage
import com.example.spgunlp.model.CONTENT_TYPE
import com.example.spgunlp.ui.BaseFragment

class ObservationsFragment(private var principleId: Int, private var principleName: String, private var email: String): BaseFragment(), MessageClickListener {
    constructor(): this(0, "Principio", "user@mail.com")
    private val messagesViewModel: MessagesViewModel by activityViewModels()
    private val bundleViewModel: BundleViewModel by activityViewModels()

    private var _binding: FragmentObsBinding? = null

    private val binding get() = _binding!!
    private val messagesList = mutableListOf<AppMessage>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentObsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (bundleViewModel.isObservationsStateEmpty()) {
            binding.detailTitle.text = principleName
        }

        populateMessages()

        binding.btnSend.setOnClickListener {

            val data = binding.inputMsg.text.toString()
            binding.inputMsg.setText("")
            (activity as VisitActivity).sendNewMessage(CONTENT_TYPE.TEXT, data, principleId, this) //TODO send real type and data
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bundleViewModel.saveObservationsState(principleId, principleName, email)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            principleName = bundleViewModel.getPrincipleObsName().toString()
            principleId = bundleViewModel.getPrincipleId()?:0
            email = bundleViewModel.getEmail().toString()
            binding.detailTitle.text = principleName
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populateMessages() {

        messagesViewModel.messages.observe(viewLifecycleOwner) { value ->
            this.messagesList.clear()
            value?.forEach {
                if (it != null) {
                    this.messagesList.add(it)
                }
            }
        }

        updateRecycler()
    }

    private fun updateRecycler(){

        binding.messagesList.apply{
            layoutManager = LinearLayoutManager(activity)
            adapter = ObservationsAdapter(messagesList, email, this@ObservationsFragment)
        }
    }

    fun updateMessagesList(){

        val adapter = binding.messagesList.adapter as ObservationsAdapter
        adapter.notifyItemInserted(adapter.itemCount - 1)

    }
    fun goToVisitFragment(){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(this.id, VisitFragment())
            .commit()
    }

    override fun onClick(message: AppMessage) {
        //TODO open if message is image or audio
    }
}
