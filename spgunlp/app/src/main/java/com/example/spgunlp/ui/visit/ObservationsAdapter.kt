package com.example.spgunlp.ui.visit

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spgunlp.databinding.ObsMeItemBinding
import com.example.spgunlp.databinding.ObsOtherItemBinding
import com.example.spgunlp.model.AppMessage

class ObservationsAdapter(private val messages: List<AppMessage>, private val id: Int, private val clickListener: MessageClickListener):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        if (messages[position].sender?.id == id){
            return 0
        }
            return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> ObservationsMeViewHolder(ObsMeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), clickListener)
            1 -> ObservationsOtherViewHolder(ObsOtherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), clickListener)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        when (viewHolder.itemViewType) {
            0 -> (viewHolder as ObservationsMeViewHolder).bind(messages[position], position)
            1 -> (viewHolder as ObservationsOtherViewHolder).bind(messages[position], position)
        }
    }

    override fun getItemCount() = messages.size

    inner class ObservationsMeViewHolder(private val cardCellBinding: ObsMeItemBinding, private val clickListener: MessageClickListener) : RecyclerView.ViewHolder(cardCellBinding.root) {
        fun bind(message: AppMessage, position: Int) {

        }
    }

    inner class ObservationsOtherViewHolder(private val cardCellBinding: ObsOtherItemBinding, private val clickListener: MessageClickListener) : RecyclerView.ViewHolder(cardCellBinding.root) {
        fun bind(message: AppMessage, position: Int) {

        }
    }

}