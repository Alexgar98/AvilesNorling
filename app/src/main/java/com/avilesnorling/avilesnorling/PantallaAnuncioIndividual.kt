package com.avilesnorling.avilesnorling

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.avilesnorling.avilesnorling.clases.Helper
import java.util.*

class PantallaAnuncioIndividual : AppCompatActivity() {
    var imagenPrincipal : ImageView = findViewById<ImageView>(R.id.imagenPrincipal)
    var categoriaUbicacion : TextView = findViewById<TextView>(R.id.categoriaUbicacion)
    var referencia : TextView = findViewById<TextView>(R.id.referenciaInteriorAnuncio)
    var descripcion : TextView = findViewById<TextView>(R.id.txtDescripcion)
    val btnReserva : Button by lazy {findViewById<Button>(R.id.btnReserva)}
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_anuncio_individual)

        val urlAnuncio : String? = intent.getStringExtra("urlAnuncio")
        val helper : Helper = Helper(this)
        var querier : SQLiteDatabase = helper.writableDatabase
        val cursor : Cursor = querier.query("propiedades", null, "url = ?", arrayOf(urlAnuncio), null, null, null)
        cursor.moveToFirst()
        //TODO descodificar tipoInmueble
        referencia.text = "Ref: " + cursor.getString(cursor.getColumnIndexOrThrow("referencia"))
        descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionEs"))
        val esVenta : Boolean = cursor.getInt(cursor.getColumnIndexOrThrow("tipoOferta")) == 1
        if (esVenta) {
            btnReserva.visibility = View.GONE
        }
        //TODO web scraping y obtener las cosas de base de datos

        btnReserva.setOnClickListener {
            //TODO Averiguar cómo hace la conversión Avaibook para entrar a la reserva por ahí. Es inviable hacerlo con cada anuncio
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
        when (idiomaActual) {
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
        }

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
            val dm = res.displayMetrics
            val conf = res.configuration
            //Esto está deprecated, preguntar si eso porque me tiene hasta las narices (?)
            conf.locale = locale
            res.updateConfiguration(conf, dm)
            val refresh = Intent(this, PantallaAnuncioIndividual::class.java)
            refresh.putExtra(idioma, localeName)
            startActivity(refresh)
        }

    }
}