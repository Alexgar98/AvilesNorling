package com.avilesnorling.avilesnorling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class PantallaAnuncioIndividual : AppCompatActivity() {
    var imagenPrincipal : ImageView = findViewById<ImageView>(R.id.imagenAnuncio)
    var categoriaUbicacion : TextView = findViewById<TextView>(R.id.categoriaUbicacion)
    var referencia : TextView = findViewById<TextView>(R.id.referenciaInteriorAnuncio)
    var descripcion : TextView = findViewById<TextView>(R.id.txtDescripcion)
    val btnReserva : Button by lazy {findViewById<Button>(R.id.btnReserva)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_anuncio_individual)

        //TODO obtener las cosas de base de datos

        btnReserva.setOnClickListener {
            //TODO Averiguar cómo hace la conversión Avaibook para entrar a la reserva por ahí. Es inviable hacerlo con cada anuncio
        }
    }
}