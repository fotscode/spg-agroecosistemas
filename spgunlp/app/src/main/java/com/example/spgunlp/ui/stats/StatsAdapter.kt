package com.example.spgunlp.ui.stats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spgunlp.databinding.GridPrincipleItemBinding
import com.example.spgunlp.model.AppVisitParameters

class StatsAdapter(private val principles: List<AppVisitParameters.Principle>, private val percentages: List<String>) :
    RecyclerView.Adapter<StatsAdapter.StatsViewHolder>() {

    inner class StatsViewHolder(private val cardCellBinding: GridPrincipleItemBinding) :
        RecyclerView.ViewHolder(cardCellBinding.root) {
        fun bind(principle: AppVisitParameters.Principle, percentage: String) {

            // limit nombre to 20 characters
            val nombre = if (principle.nombre!= null && principle.nombre.length > 50) {
                principle.nombre.substring(0, 50) + "..."
            } else {
                principle.nombre
            }
            cardCellBinding.principleName.text = nombre
            cardCellBinding.principlePercentage.text = percentage
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val from = LayoutInflater.from(parent.context)
        val cardCellBinding = GridPrincipleItemBinding.inflate(from, parent, false)
        return StatsViewHolder(cardCellBinding)
    }

    override fun getItemCount(): Int {
        return principles.size
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        val item = principles[position]
        val percentage = percentages[position]
        holder.bind(item, percentage)
    }
}