package com.example.spgunlp.ui.visit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spgunlp.R
import com.example.spgunlp.model.AppVisitParameters.Principle

class PrinciplesAdapter(private val principles: List<Principle>):
    RecyclerView.Adapter<PrinciplesAdapter.PrinciplesViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PrinciplesViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.principle_item, viewGroup, false)

        return PrinciplesViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: PrinciplesViewHolder, position: Int) {

        val item = principles[position]
        viewHolder.bind(item)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = principles.size

    inner class PrinciplesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.principle_title)

        fun bind(principle: Principle) {
            title.text = principle.nombre
        }
    }

}