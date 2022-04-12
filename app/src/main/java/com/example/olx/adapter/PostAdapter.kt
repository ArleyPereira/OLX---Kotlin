package com.example.olx.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.olx.R
import com.example.olx.databinding.PostAdapterBinding
import com.example.olx.util.GetMask
import com.example.olx.model.Post
import com.squareup.picasso.Picasso

class PostAdapter(
    private var postList: List<Post>,
    private var context: Context,
    val postSelected: (Post) -> Unit?
) : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            PostAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val post = postList[position]

        Picasso.get().load(post.urlImages[0]).into(holder.binding.imgPost)
        holder.binding.textTitle.text = post.title
        holder.binding.textPrice.text =
            context.getString(R.string.valor_anuncio, GetMask.getValor(post.price))
        holder.binding.textPublication.text = context.getString(
            R.string.place_of_publication,
            post.address?.district,
            if (post.address?.state != null) post.address?.state else "",
            GetMask.getDate(post.registrationDate, GetMask.DIA_MES)
        )

        holder.itemView.setOnClickListener { postSelected(post) }
    }

    override fun getItemCount() = postList.size

    class MyViewHolder(val binding: PostAdapterBinding) : RecyclerView.ViewHolder(binding.root)

}