package com.example.olx.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.olx.R
import com.example.olx.Util.GetMask
import com.example.olx.model.Anuncio
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_anuncio.view.*

class AdapterAnuncio(
    private var anuncioList: List<Anuncio>,
    private var clickListener: OnClickListener,
    private var activity: Activity
) : RecyclerView.Adapter<AdapterAnuncio.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_anuncio, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val anuncio = anuncioList[position]

        Picasso.get().load(anuncio.urlFotos[0]).into(holder.itemView.imagemAnuncio)
        holder.textTitulo.text = anuncio.titulo
        holder.textPreco.text = activity.getString(R.string.valor_anuncio, GetMask.getValor(anuncio.preco))
        holder.textData.text = activity.getString(R.string.publicacao_anuncio,
            anuncio.local.bairro, GetMask.getDate(anuncio.dataCadastro, GetMask.DIA_MES))

        holder.itemView.setOnClickListener { clickListener.onItemClick(anuncio) }

    }

    override fun getItemCount() = anuncioList.size

    interface OnClickListener{
        fun onItemClick(anuncio: Anuncio)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imagemAnuncio = itemView.imagemAnuncio
        val textTitulo = itemView.textTitulo
        val textPreco = itemView.textPreco
        val textData = itemView.textData
    }

}