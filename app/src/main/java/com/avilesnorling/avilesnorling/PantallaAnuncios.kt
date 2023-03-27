package com.avilesnorling.avilesnorling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView

class PantallaAnuncios : AppCompatActivity() {
    val txtreferencia : EditText by lazy {findViewById<EditText>(R.id.referencia)}
    val txtsuperficie : EditText by lazy {findViewById<EditText>(R.id.superficie)}
    val txtprecioDesde : EditText by lazy {findViewById<EditText>(R.id.precioDesde)}
    val txtprecioHasta : EditText by lazy {findViewById<EditText>(R.id.precioHasta)}
    val tipoAnuncio : Spinner by lazy {findViewById<Spinner>(R.id.tipoAnuncio)}
    val tipoInmueble : Spinner by lazy {findViewById<Spinner>(R.id.tipoInmueble)}
    val ubicacion : Spinner by lazy {findViewById<Spinner>(R.id.ubicacion)}
    val dormitorios : Spinner by lazy {findViewById<Spinner>(R.id.dormitorios)}
    val btnBuscar : Button by lazy {findViewById<Button>(R.id.btnBuscar)}
    var recyclerAnuncios : RecyclerView = findViewById<RecyclerView>(R.id.recyclerAnuncios)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_pantalla_anuncios)

        var referencia : String? = txtreferencia.text.toString()
        var superficie : String? = txtsuperficie.text.toString()
        var precioDesde : String? = txtprecioDesde.text.toString()
        var precioHasta : String? = txtprecioHasta.text.toString()
        var anuncio : String? = tipoAnuncio.selectedItem.toString()
        var inmueble : String? = tipoInmueble.selectedItem.toString()
        var ubicacionElegida : String? = ubicacion.selectedItem.toString()
        var numeroDormitorios : String? = dormitorios.selectedItem.toString()

        btnBuscar.setOnClickListener {
            //TODO Buscar en base de datos según las variables de arriba y cargar el recycler con lo que haya
        }
    }
}