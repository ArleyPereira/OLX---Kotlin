package com.example.olx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.olx.R
import com.example.olx.model.Category

class CategoriesAdapter(
    private val categoryList: List<Category>,
    val categorySelected: (Category) -> Unit?
) : RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val category = categoryList[position]

        holder.imgCategory.setImageResource(category.image)
        holder.textCategory.text = category.name

        holder.itemView.setOnClickListener { categorySelected(category) }
    }

    override fun getItemCount() = categoryList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCategory: ImageView = itemView.findViewById(R.id.imgCategory)
        val textCategory: TextView = itemView.findViewById(R.id.textCategory)
    }

}