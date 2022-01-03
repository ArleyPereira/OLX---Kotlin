package com.example.olx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.olx.R
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso

class SliderAdapter(private var urlsImagens: List<String>) :
    SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent?.context).inflate(
            R.layout.slider_layout_item,
            null
        )
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {
        val urlImagem = urlsImagens[position]

        if (viewHolder != null) {
            Picasso.get().load(urlImagem).into(viewHolder.imageViewBackground)
        }
    }

    override fun getCount() = urlsImagens.size

    class SliderAdapterVH(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        val imageViewBackground: ImageView = itemView.findViewById(R.id.imageViewBackground)
    }

}