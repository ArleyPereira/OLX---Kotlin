package com.example.olx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.olx.R
import com.example.olx.model.State

class StateAdapter(
    private val stateList: List<State>,
    val stateSelected: (State) -> Unit?
) : RecyclerView.Adapter<StateAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_state, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val state = stateList[position]

        holder.textState.text = state.name

        holder.itemView.setOnClickListener { stateSelected(state) }
    }

    override fun getItemCount() = stateList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textState: TextView = itemView.findViewById(R.id.textState)
    }

}