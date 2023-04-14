package com.avilesnorling.avilesnorling.clases

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.avilesnorling.avilesnorling.PantallaAnuncioIndividual
import com.avilesnorling.avilesnorling.R

class AnuncioRecyclerAdapter (private val anuncios : List<Anuncio>) : RecyclerView.Adapter<AnuncioRecyclerAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout : ConstraintLayout = view.findViewById(R.id.recyclerLayout)
        val foto : ImageView = view.findViewById(R.id.imagenAnuncio)
        val precioAnuncio : TextView = view.findViewById(R.id.precioAnuncio)
        val referenciaAnuncio : TextView = view.findViewById(R.id.referenciaAnuncio)
        val numeroPersonas : TextView = view.findViewById(R.id.numeroPersonas)
        val superficie : TextView = view.findViewById(R.id.numeroSuperficie)
        val habitaciones : TextView = view.findViewById(R.id.numeroHabitaciones)
        val banos : TextView = view.findViewById(R.id.numeroBanos)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnuncioRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_recycler_anuncios, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnuncioRecyclerAdapter.ViewHolder, position: Int) {
        val anuncio = anuncios[position]
        holder.referenciaAnuncio.text = "Ref: " + anuncio.referencia
        //TODO el resto de datos se sacan de BD
        holder.layout.setOnClickListener {
            val url = anuncio.url
            val intent : Intent = Intent(holder.layout.context, PantallaAnuncioIndividual :: class.java)
            intent.putExtra("urlAnuncio", url)
            holder.layout.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return anuncios.size
    }



}