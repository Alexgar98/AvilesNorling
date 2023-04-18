package com.avilesnorling.avilesnorling

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avilesnorling.avilesnorling.clases.Anuncio
import com.avilesnorling.avilesnorling.clases.AnuncioRecyclerAdapter
import com.avilesnorling.avilesnorling.clases.Helper
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
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

    //Barra de arriba
    val spinnerIdiomas : Spinner by lazy{findViewById<Spinner>(R.id.spinnerIdiomas)}
    val imgWhatsapp : ImageView by lazy{findViewById<ImageView>(R.id.imgWhatsapp)}
    val imgFacebook : ImageView by lazy{findViewById<ImageView>(R.id.imgFacebook)}
    val imgTwitter : ImageView by lazy{findViewById<ImageView>(R.id.imgTwitter)}
    val imgLinkedin : ImageView by lazy{findViewById<ImageView>(R.id.imgLinkedin)}
    val imgYoutube : ImageView by lazy{findViewById<ImageView>(R.id.imgYoutube)}
    val imgInstagram : ImageView by lazy{findViewById<ImageView>(R.id.imgInstagram)}
    val imgCasa : ImageView by lazy{findViewById<ImageView>(R.id.imgCasa)}
    lateinit var locale : Locale
    private var idiomaActual = "es"
    private var idioma : String? = null

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_pantalla_anuncios)
        //Arrays con los valores de los spinners
        var anuncios = arrayOf<String>(getString(R.string.oferta), getString(R.string.venta), getString(R.string.alquiler), getString(R.string.vacaciones))
        var inmuebles = arrayOf<String>(getString(R.string.tipoInmueble), getString(R.string.pisos), getString(R.string.casas), getString(R.string.locales), getString(R.string.tiendas))
        var ubicaciones = arrayOf<String>("Torre del Mar", "Vélez-Málaga", "Algarrobo", "Almáchar", "Almayate", "Benajarafe", "Benamargosa", "Caleta de Vélez", "Canillas de Aceituno", "Torrox", "Málaga", "Málaga oriental")
        var numerosDormitorios = arrayOf<String>(getString(R.string.dormitorios), "1+", "2+", "3+", "4+", "5+", "6+", "7+", "8+", "9+", "10+")


        //Adapters de los spinners
        val anunciosAdapter = ArrayAdapter(this, R.layout.layout_spinners, R.id.textoSpinners, anuncios)
        tipoAnuncio.adapter = anunciosAdapter
        var anuncioElegido = intent.getStringExtra("tipoAnuncio")
        tipoAnuncio.setSelection(anunciosAdapter.getPosition(anuncioElegido))

        val inmueblesAdapter = ArrayAdapter(this, R.layout.layout_spinners, R.id.textoSpinners, inmuebles)
        tipoInmueble.adapter = inmueblesAdapter
        tipoInmueble.prompt = getText(R.string.tipoInmueble)
        //tipoInmueble.setSelection(0)

        val ubicacionAdapter = ArrayAdapter(this, R.layout.layout_spinners, R.id.textoSpinners, ubicaciones)
        ubicacion.adapter = ubicacionAdapter
        var ubicacionElegida = intent.getStringExtra("zona")
        ubicacion.setSelection(ubicacionAdapter.getPosition(ubicacionElegida))

        val dormitoriosAdapter = ArrayAdapter(this, R.layout.layout_spinners, R.id.textoSpinners, numerosDormitorios)
        dormitorios.adapter = dormitoriosAdapter
        dormitorios.prompt = getText(R.string.dormitorios)
        //dormitorios.setSelection(0)

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

        //Codifico el tipo de inmueble según el anuncio elegido
        var inmueble : Int = if (anuncioElegido == "Venta") {
            1
        } else if (anuncioElegido == "Alquiler" || anuncioElegido == "Vacaciones") {
            2
        } else {
            0
        }

        //Búsqueda
        var anunciosBuscados = ArrayList<Anuncio>()
        val helper : Helper = Helper(this)
        var querier : SQLiteDatabase = helper.writableDatabase
        var cursor : Cursor = if (inmueble == 0) {
            querier.query("propiedades", null, "localidad = ?", arrayOf(ubicacionElegida), null, null, null)
        } else {
            querier.query(
                "propiedades",
                null,
                "localidad = ? and tipoOferta = ?",
                arrayOf(ubicacionElegida, inmueble.toString()),
                null,
                null,
                null
            )
        }
        cursor.moveToFirst()
        while (cursor.moveToNext()) {
            val anuncioNuevo : Anuncio = devolverAnuncio(cursor)
            anunciosBuscados.add(anuncioNuevo)
        }
        Toast.makeText(this, "Se han encontrado estos resultados: " + anunciosBuscados.size, Toast.LENGTH_LONG).show()



        recyclerAnuncios.layoutManager = LinearLayoutManager(this)
        val recyclerAdapter = AnuncioRecyclerAdapter(anunciosBuscados)
        recyclerAnuncios.adapter = recyclerAdapter

        recyclerAnuncios.setOnClickListener {

        }

        btnBuscar.setOnClickListener {
            var referencia : String? = txtreferencia.text.toString()
            var superficie : String? = txtsuperficie.text.toString()
            var precioDesde : String? = txtprecioDesde.text.toString()
            var precioHasta : String? = txtprecioHasta.text.toString()
            var anuncio : String? = tipoAnuncio.selectedItem.toString()
            var inmueble : String? = tipoInmueble.selectedItem.toString()
            var ubicacionSpinner : String? = ubicacion.selectedItem.toString()
            var numeroDormitorios : String? = dormitorios.selectedItem.toString()
            var personasElegidas : String? = personas.text.toString()
            var fechaElegida : Date?

            anunciosBuscados.clear()
            recyclerAdapter.notifyDataSetChanged()
            recyclerAnuncios.scrollToPosition(0)

            var consulta : String? = ""
            var valores : Array<String> = arrayOf()
            if (referencia != null) {
                consulta += "referencia = ?"
                valores+=referencia
            }
            if (ubicacionSpinner != null) {
                if (consulta == "") {
                    consulta += "localidad = ?"
                }
                else {
                    consulta += " and localidad = ?"
                }
                valores+=ubicacionSpinner
            }
            //TODO codificar tipoAnuncio y tipoInmueble

            if (consulta == "") {
                consulta = null
            }

            cursor = querier.query("propiedades", null, consulta, valores, null, null, null)
            cursor.moveToFirst()
            while (cursor.moveToNext()) {
                val anuncioNuevo : Anuncio = devolverAnuncio(cursor)
                anunciosBuscados.add(anuncioNuevo)
            }

            recyclerAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Se han encontrado estos resultados: " + anunciosBuscados.size, Toast.LENGTH_LONG).show()
        }

        //Barra de arriba

        idiomaActual = intent.getStringExtra(idioma).toString()

        //Array de idiomas que se muestra en el spinner
        val idiomas = arrayOf(
            Pair(getString(R.string.espanol), R.drawable.espana),
            Pair(getString(R.string.ingles), R.drawable.uk),
            Pair(getString(R.string.aleman), R.drawable.alemania),
            Pair(getString(R.string.frances), R.drawable.francia),
            Pair(getString(R.string.sueco), R.drawable.suecia)
        )

        //Adapter del spinner
        val adapter = object : ArrayAdapter<Pair<String, Int>>(this, R.layout.spinner_idiomas, R.id.txtPais, idiomas) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val idioma = idiomas[position]
                val textoIdioma = view.findViewById<TextView>(R.id.txtPais)
                textoIdioma.text = idioma.first
                val imageView = view.findViewById<ImageView>(R.id.imgPais)
                imageView.setImageResource(idioma.second)
                return view
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val idioma = idiomas[position]
                val textoIdioma = view.findViewById<TextView>(R.id.txtPais)
                textoIdioma.text = idioma.first
                val imageView = view.findViewById<ImageView>(R.id.imgPais)
                imageView.setImageResource(idioma.second)
                return view
            }
        }

        //Aquí se aplica el adapter
        spinnerIdiomas.adapter = adapter

        //Setea el spinner según el idioma seleccionado, para que no se vuelva siempre al español
        /*when (idiomaActual) {
            "es" -> spinnerIdiomas.setSelection(0)
            "en" -> spinnerIdiomas.setSelection(1)
            "de" -> spinnerIdiomas.setSelection(2)
            "fr" -> spinnerIdiomas.setSelection(3)
            "sv" -> spinnerIdiomas.setSelection(4)
        }

        //Selección de idioma
        spinnerIdiomas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> setLocale("es")
                    1 -> setLocale("en")
                    2 -> setLocale("de")
                    3 -> setLocale("fr")
                    4 -> setLocale("sv")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }*/

        //Enlaces a redes sociales
        imgWhatsapp.setOnClickListener {
            abrirWeb("https://api.whatsapp.com/send?phone=34643672547")
        }
        imgFacebook.setOnClickListener {
            abrirWeb("https://www.facebook.com/aviles.norling.investment/")
        }
        imgTwitter.setOnClickListener {
            abrirWeb("https://twitter.com/inv_aviles")
        }
        imgLinkedin.setOnClickListener {
            abrirWeb("https://www.linkedin.com/in/carlos-avil%C3%A9s-54a8245a/")
        }
        imgYoutube.setOnClickListener {
            abrirWeb("https://www.youtube.com/channel/UCiKSK6yU5XM82nfbOczOMuQ/videos")
        }
        imgInstagram.setOnClickListener {
            abrirWeb("https://www.instagram.com/avilesnorling2020/")
            //Está caído
        }
        imgCasa.setOnClickListener {
            val intentCasa : Intent = Intent(this, MenuPrincipal::class.java)
            startActivity(intentCasa)
        }

    }

    //Función para abrir la web que toque
    private fun abrirWeb (url : String) {
        val abrirPagina : Intent = Intent(Intent.ACTION_VIEW)
        abrirPagina.data = Uri.parse(url)
        startActivity(abrirPagina)
    }

    fun setLocale(localeName: String) {
        if (localeName != idiomaActual) {
            locale = Locale(localeName)
            val res = resources
            val conf = res.configuration
            conf.setLocales(LocaleList(locale))
            this.createConfigurationContext(conf)
            val refresh = Intent(this, PantallaAnuncios::class.java)
            refresh.putExtra(idioma, localeName)
            startActivity(refresh)
        }

    }

    //Función para devolver un anuncio obtenido de base de datos
    private fun devolverAnuncio(cursor : Cursor) : Anuncio {
        return Anuncio(cursor.getString(cursor.getColumnIndexOrThrow("referencia")),
            LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow("fecha"))),
            cursor.getString(cursor.getColumnIndexOrThrow("url")),
            cursor.getInt(cursor.getColumnIndexOrThrow("tipoInmueble")),
            cursor.getInt(cursor.getColumnIndexOrThrow("tipoOferta")),
            cursor.getString(cursor.getColumnIndexOrThrow("descripcionEs")),
            cursor.getString(cursor.getColumnIndexOrThrow("descripcionEn")),
            cursor.getString(cursor.getColumnIndexOrThrow("descripcionFr")),
            cursor.getString(cursor.getColumnIndexOrThrow("descripcionDe")),
            cursor.getString(cursor.getColumnIndexOrThrow("descripcionSv")),
            cursor.getInt(cursor.getColumnIndexOrThrow("codigoPostal")),
            cursor.getString(cursor.getColumnIndexOrThrow("provincia")),
            cursor.getString(cursor.getColumnIndexOrThrow("localidad")),
            cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
            cursor.getString(cursor.getColumnIndexOrThrow("geoLocalizacion")),
            cursor.getString(cursor.getColumnIndexOrThrow("registroTurismo")))
    }
}