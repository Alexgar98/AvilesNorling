package com.avilesnorling.avilesnorling

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avilesnorling.avilesnorling.clases.Anuncio
import com.avilesnorling.avilesnorling.clases.AnuncioRecyclerAdapter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

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
    val precioPersonas : ViewFlipper by lazy {findViewById<ViewFlipper>(R.id.precioPersonas)}
    val precioFecha : ViewFlipper by lazy {findViewById<ViewFlipper>(R.id.precioFecha)}
    val personas : EditText by lazy {findViewById<EditText>(R.id.personas)}
    val fecha : EditText by lazy {findViewById<EditText>(R.id.fecha)}
    val recyclerAnuncios : RecyclerView by lazy {findViewById<RecyclerView>(R.id.recyclerAnuncios)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_pantalla_anuncios)
        //Arrays con los valores de los spinners
        var anuncios = arrayOf<String>(getString(R.string.oferta), getString(R.string.venta), getString(R.string.alquiler), getString(R.string.vacaciones))
        var inmuebles = arrayOf<String>(getString(R.string.tipoInmueble), getString(R.string.pisos), getString(R.string.casas), getString(R.string.locales), getString(R.string.tiendas))
        var ubicaciones = arrayOf<String>("Torre del Mar", "Vélez-Málaga", "Algarrobo", "Almáchar", "Almayate", "Benajarafe", "Benamargosa", "Caleta de Vélez", "Canillas de Aceituno", "Torrox", "Málaga", "Málaga oriental")
        var numerosDormitorios = arrayOf<String>(getString(R.string.dormitorios), "1+", "2+", "3+", "4+", "5+", "6+", "7+", "8+", "9+", "10+")


        //Adapters de los spinners
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

        //Cambio los TextView según si se ha seleccionado Vacaciones o no como tipo de anuncio
        tipoAnuncio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedOption = parent?.getItemAtPosition(position).toString()

                if (selectedOption == getString(R.string.vacaciones)) {
                    precioPersonas.displayedChild = 1
                    precioFecha.displayedChild = 1
                } else {
                    precioPersonas.displayedChild = 0
                    precioFecha.displayedChild = 0
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        //Manejo de la fecha
        var fechaInicio: LocalDate? = null
        var fechaFin: LocalDate? = null
        fecha.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, day ->
                    //El +1 es porque el DatePicker empieza los meses en 0 y el LocalDate en 1
                    val fechaElegida : LocalDate = LocalDate.of(year, month + 1, day)
                    if (fechaInicio == null || (fechaInicio != null && fechaFin != null)) {
                        if (fechaElegida.isBefore(LocalDate.now())) {
                            Toast.makeText(this, getString(R.string.fechaNoValida), Toast.LENGTH_LONG).show()
                        }
                        else {
                            fechaInicio = fechaElegida
                            //Reseteo por si el usuario está poniendo otro rango de fechas
                            fechaFin = null
                            fecha.setText("" + day + "/" + (month+1) + "/" + year)
                        }
                    }
                    else {
                        if (fechaFin == null && fechaElegida.isAfter(fechaInicio)) {
                            fechaFin = fechaElegida
                            fecha.setText("" + fechaInicio!!.dayOfMonth + "/" + fechaInicio!!.monthValue + "/" + fechaInicio!!.year + "-" + day + "/" + (month+1) + "/" + year)

                        }
                        else {
                            Toast.makeText(this, getString(R.string.fechaNoValida), Toast.LENGTH_LONG).show()
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }

        var anunciosBuscados = ArrayList<Anuncio>()

        //TODO Buscar en base de datos y añadir lo que salga a anunciosBuscados

        btnBuscar.setOnClickListener {
            var referencia : String? = txtreferencia.text.toString()
            var superficie : String? = txtsuperficie.text.toString()
            var precioDesde : String? = txtprecioDesde.text.toString()
            var precioHasta : String? = txtprecioHasta.text.toString()
            var anuncio : String? = tipoAnuncio.selectedItem.toString()
            var inmueble : String? = tipoInmueble.selectedItem.toString()
            var ubicacionSpinner : String? = ubicacion.selectedItem.toString()
            var numeroDormitorios : String? = dormitorios.selectedItem.toString()
            var personasElegidas : Int? = personas.text.toString().toInt()
            var fechaElegida : Date?
            //TODO Buscar otra vez en BD

            anunciosBuscados = ArrayList<Anuncio>()


        }

        recyclerAnuncios.layoutManager = LinearLayoutManager(this)
        val recyclerAdapter = AnuncioRecyclerAdapter(anunciosBuscados)
        recyclerAnuncios.adapter = recyclerAdapter

    }
}