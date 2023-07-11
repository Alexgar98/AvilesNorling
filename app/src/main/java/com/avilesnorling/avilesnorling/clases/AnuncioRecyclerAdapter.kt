package com.avilesnorling.avilesnorling.clases

import android.content.Intent
import android.content.res.AssetManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.avilesnorling.avilesnorling.PantallaAnuncioIndividual
import com.avilesnorling.avilesnorling.R
import com.github.doyaaaaaken.kotlincsv.dsl.context.ExcessFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.context.InsufficientFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.squareup.picasso.Picasso

class AnuncioRecyclerAdapter (private val anuncios : List<Anuncio>, private val assets : AssetManager) : RecyclerView.Adapter<AnuncioRecyclerAdapter.ViewHolder>(){

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
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_recycler_anuncios, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val anuncio = anuncios[position]
        holder.referenciaAnuncio.text = "Ref: " + anuncio.referencia
        Picasso.get().load(anuncio.imgPrincipal).into(holder.foto)
        if (anuncio.precio!! > 0) {
            holder.precioAnuncio.text = "" + anuncio.precio + " €"
        }
        else {
            holder.precioAnuncio.text = holder.itemView.context.getString(R.string.consultar)
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
        holder.numeroPersonas.text = "" + anuncio.personas

        if (holder.numeroPersonas.text == "0") {
            holder.imgPersonas.visibility = View.INVISIBLE
            holder.numeroPersonas.visibility = View.INVISIBLE
        }
        holder.layout.setOnClickListener {
            val url = anuncio.url
            val intent = Intent(holder.layout.context, PantallaAnuncioIndividual :: class.java)
            intent.putExtra("urlAnuncio", url)
            holder.layout.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return anuncios.size
    }



}