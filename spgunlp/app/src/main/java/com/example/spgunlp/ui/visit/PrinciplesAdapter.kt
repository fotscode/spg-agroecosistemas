package com.example.spgunlp.ui.visit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spgunlp.model.AppVisitParameters.Principle
import com.example.spgunlp.databinding.PrincipleItemBinding

class PrinciplesAdapter(private val principles: List<Principle>, private val clickListener: PrincipleClickListener):
    RecyclerView.Adapter<PrinciplesAdapter.PrinciplesViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrinciplesViewHolder {
        val from = LayoutInflater.from(parent.context)
        val cardCellBinding = PrincipleItemBinding.inflate(from, parent, false)
        return PrinciplesViewHolder(cardCellBinding,clickListener)
    }

    override fun onBindViewHolder(viewHolder: PrinciplesViewHolder, position: Int) {

        val item = principles[position]
        if (item != null) {
            viewHolder.bind(item)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = principles.size

    inner class PrinciplesViewHolder(private val cardCellBinding:PrincipleItemBinding,private val clickListener: PrincipleClickListener) : RecyclerView.ViewHolder(cardCellBinding.root) {
        fun bind(principle: Principle) {
            cardCellBinding.principleTitle.text = principle.nombre
        }
    }

}