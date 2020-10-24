package com.example.olx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.olx.R
import com.example.olx.model.Categoria
import kotlinx.android.synthetic.main.adapter_categoria.view.*

class AdapterCategoria(
    private val categoriaList: List<Categoria>,
    private var clickListener: OnClickListener
    ) :
    RecyclerView.Adapter<AdapterCategoria.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_categoria, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val categoria = categoriaList[position]

        holder.imagem.setImageResource(categoria.imagem)
        holder.textCategoria.text = categoria.nome

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(categoria)
        }

    }

    override fun getItemCount() = categoriaList.size

    interface OnClickListener {
        fun onItemClick(categoria: Categoria)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagem: ImageView = itemView.imgCategoria
        val textCategoria: TextView = itemView.textCategoria
    }

}