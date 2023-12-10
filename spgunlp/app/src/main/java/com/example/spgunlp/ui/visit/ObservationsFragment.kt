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

class ObservationsFragment(private val principleId: Int, private val principleName: String, private val email: String): BaseFragment(), MessageClickListener {

    private val messagesViewModel: MessagesViewModel by activityViewModels()

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

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // binding.detailTitle.text = principleName

        populateMessages()

        binding.btnSend.setOnClickListener {

            val data = binding.inputMsg.text.toString()
            binding.inputMsg.setText("")
            (activity as VisitActivity).sendNewMessage(CONTENT_TYPE.TEXT, data, principleId) //TODO send real type and data

            // update recycler with new message
            updateMessagesList()
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

    private fun updateMessagesList(){

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
