package com.avilesnorling.avilesnorling.clases

import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.avilesnorling.avilesnorling.PantallaAnuncioIndividual
import com.avilesnorling.avilesnorling.R
import com.squareup.picasso.Picasso
import java.net.URL

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
        val imgPersonas : ImageView = view.findViewById(R.id.imgPersonas)
        val imgDormitorios : ImageView = view.findViewById(R.id.imgDormitorios)
        val imgSuperficie : ImageView = view.findViewById(R.id.imgSuperficie)
        val imgBanos : ImageView = view.findViewById(R.id.imgBanos)

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
        Picasso.get().load(anuncio.imgPrincipal).into(holder.foto)
        if (anuncio.precio!! > 0) {
            holder.precioAnuncio.text = "" + anuncio.precio!! + " €"
        }
        else {
            holder.precioAnuncio.visibility = View.INVISIBLE
        }
        if (anuncio.dormitorios!! > 0 ) {
            holder.habitaciones.text = "" + anuncio.dormitorios
        }
        else {
            holder.habitaciones.visibility = View.INVISIBLE
            holder.imgDormitorios.visibility = View.INVISIBLE
        }
        if (anuncio.banos!! > 0) {
            holder.banos.text = "" + anuncio.banos
        }
        else {
            holder.banos.visibility = View.INVISIBLE
            holder.imgBanos.visibility = View.INVISIBLE
        }
        if (anuncio.superficie!! > 0) {
            holder.superficie.text = "" + anuncio.superficie + " m2"
        }
        else {
            holder.superficie.visibility = View.INVISIBLE
            holder.imgSuperficie.visibility = View.INVISIBLE
        }
        //TODO averiguar el número de personas
        holder.imgPersonas.visibility = View.INVISIBLE
        holder.numeroPersonas.visibility = View.INVISIBLE
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