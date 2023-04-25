package com.avilesnorling.avilesnorling.clases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.avilesnorling.avilesnorling.R
import com.squareup.picasso.Picasso

class FotoRecyclerAdapter (private val fotos : List<String?>) : RecyclerView.Adapter<FotoRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout : ConstraintLayout = view.findViewById(R.id.recyclerFotos)
        val foto : ImageView = view.findViewById(R.id.foto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_recycler_fotos, parent, false)
        return FotoRecyclerAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fotos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(fotos[position]).into(holder.foto)
    }

}