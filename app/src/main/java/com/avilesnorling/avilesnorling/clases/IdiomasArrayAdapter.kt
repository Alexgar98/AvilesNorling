package com.avilesnorling.avilesnorling.clases

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.avilesnorling.avilesnorling.R

class IdiomasArrayAdapter (contexto : Context, idiomas : List<Idiomas>) : ArrayAdapter<Idiomas>(contexto, 0, idiomas) {
    //val imgPais : ImageView by lazy {findViewById<ImageView>(R.id.imgPais)}
    //val txtPais : TextView by lazy {findViewById<TextView>(R.id.txtPais)}
    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {

        val idioma = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.spinner_idiomas,
            parent,
            false
        )

        //view.imgPais.setImageResource(idioma!!.bandera)
        //view.txtPais.text = idioma?.idioma

        return view
    }
}