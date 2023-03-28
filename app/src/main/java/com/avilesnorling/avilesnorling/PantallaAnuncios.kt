package com.avilesnorling.avilesnorling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class PantallaAnuncios : AppCompatActivity() {
    val txtreferencia : EditText by lazy {findViewById<EditText>(R.id.referencia)}
    val txtsuperficie : EditText by lazy {findViewById<EditText>(R.id.superficie)}
    val txtprecioDesde : EditText by lazy {findViewById<EditText>(R.id.precioDesde)}
    val txtprecioHasta : EditText by lazy {findViewById<EditText>(R.id.precioHasta)}
    val tipoAnuncio : Spinner = findViewById<Spinner>(R.id.tipoAnuncio)
    val tipoInmueble : Spinner by lazy {findViewById<Spinner>(R.id.tipoInmueble)}
    val ubicacion : Spinner = findViewById<Spinner>(R.id.ubicacion)
    val dormitorios : Spinner by lazy {findViewById<Spinner>(R.id.dormitorios)}
    val btnBuscar : Button by lazy {findViewById<Button>(R.id.btnBuscar)}
    var recyclerAnuncios : RecyclerView = findViewById<RecyclerView>(R.id.recyclerAnuncios)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_pantalla_anuncios)
        var anuncios = arrayOf<String>(getString(R.string.oferta), getString(R.string.venta), getString(R.string.alquiler), getString(R.string.vacaciones))
        var inmuebles = arrayOf<String>(getString(R.string.tipoInmueble), getString(R.string.pisos), getString(R.string.casas), getString(R.string.locales), getString(R.string.tiendas))
        var ubicaciones = arrayOf<String>("Torre del Mar", "Vélez-Málaga", "Algarrobo", "Almáchar", "Almayate", "Benajarafe", "Benamargosa", "Caleta de Vélez", "Canillas de Aceituno", "Torrox")
        var numerosDormitorios = arrayOf<String>(getString(R.string.dormitorios), "1+", "2+", "3+", "4+", "5+", "6+", "7+", "8+", "9+", "10+")

        val anunciosAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, anuncios)
        tipoAnuncio.adapter = anunciosAdapter
        var anuncioElegido = intent.getStringExtra("tipoAnuncio")
        tipoAnuncio.setSelection(anunciosAdapter.getPosition(anuncioElegido))

        val inmueblesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, inmuebles)
        tipoInmueble.adapter = inmueblesAdapter
        tipoInmueble.setSelection(0)

        val ubicacionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ubicaciones)
        ubicacion.adapter = ubicacionAdapter
        var ubicacionElegida = intent.getStringExtra("zona")
        ubicacion.setSelection(ubicacionAdapter.getPosition(ubicacionElegida))

        val dormitoriosAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, numerosDormitorios)
        dormitorios.adapter = dormitoriosAdapter
        dormitorios.setSelection(0)


        var referencia : String? = txtreferencia.text.toString()
        var superficie : String? = txtsuperficie.text.toString()
        var precioDesde : String? = txtprecioDesde.text.toString()
        var precioHasta : String? = txtprecioHasta.text.toString()
        var anuncio : String? = tipoAnuncio.selectedItem.toString()
        var inmueble : String? = tipoInmueble.selectedItem.toString()
        var ubicacionSpinner : String? = ubicacion.selectedItem.toString()
        var numeroDormitorios : String? = dormitorios.selectedItem.toString()

        btnBuscar.setOnClickListener {
            //TODO Buscar en base de datos según las variables de arriba y cargar el recycler con lo que haya
        }
    }
}