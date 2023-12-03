package com.example.spgunlp.ui.active

import androidx.recyclerview.widget.RecyclerView
import com.example.spgunlp.databinding.ListVisitElementBinding
import com.example.spgunlp.model.Visit

class VisitViewHolder(private val cardCellBinding:ListVisitElementBinding): RecyclerView.ViewHolder(cardCellBinding.root)
{

    fun findVisit(visit: Visit)
    {
        cardCellBinding.iconImageView.setImageResource(visit.cover!!) //TODO(Add images support)
        cardCellBinding.visitName.text = visit.name
    }
}