package com.example.olx.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.olx.R
import com.example.olx.util.GetMask
import com.example.olx.model.Post
import com.squareup.picasso.Picasso

class PostAdapter(
    private var postList: List<Post>,
    private var context: Context,
    val postSelected: (Post) -> Unit?
) : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.post_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val post = postList[position]

        Picasso.get().load(post.urlImages[0]).into(holder.imgPost)
        holder.textTitle.text = post.title
        holder.textPrice.text =
            context.getString(R.string.valor_anuncio, GetMask.getValor(post.price))
        holder.textPublication.text = context.getString(
            R.string.place_of_publication,
            post.address?.district,
            if(post.address?.state != null) post.address?.state else "",
            GetMask.getDate(post.registrationDate, GetMask.DIA_MES)
        )

        holder.itemView.setOnClickListener { postSelected(post) }
    }

    override fun getItemCount() = postList.size

    interface OnClickListener {
        fun onItemClick(post: Post)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPost: ImageView = itemView.findViewById(R.id.imgPost)
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textPrice: TextView = itemView.findViewById(R.id.textPrice)
        val textPublication: TextView = itemView.findViewById(R.id.textPublication)
    }

}