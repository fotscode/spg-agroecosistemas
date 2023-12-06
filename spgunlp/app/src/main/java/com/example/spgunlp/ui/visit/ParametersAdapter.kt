package com.example.spgunlp.ui.visit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spgunlp.databinding.ParameterItemBinding
import com.example.spgunlp.model.AppVisitParameters

class ParametersAdapter(private val parameters: List<AppVisitParameters>, private val clickListener: ParameterClickListener):
    RecyclerView.Adapter<ParametersAdapter.ParametersViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParametersViewHolder {
        val from = LayoutInflater.from(parent.context)
        val cardCellBinding = ParameterItemBinding.inflate(from, parent, false)
        return ParametersViewHolder(cardCellBinding,clickListener)
    }

    override fun onBindViewHolder(viewHolder: ParametersViewHolder, position: Int) {

        val item = parameters[position]
        if (item != null) {
            viewHolder.bind(item)
        }
    }

    override fun getItemCount() = parameters.size

    inner class ParametersViewHolder(private val cardCellBinding: ParameterItemBinding, private val clickListener: ParameterClickListener) : RecyclerView.ViewHolder(cardCellBinding.root) {
        fun bind(parameter: AppVisitParameters) {
            cardCellBinding.parameterTitle.text = parameter.nombre
        }
    }

}