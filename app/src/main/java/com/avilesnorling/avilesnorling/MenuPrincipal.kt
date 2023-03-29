package com.avilesnorling.avilesnorling

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import com.avilesnorling.avilesnorling.clases.Idiomas
import com.avilesnorling.avilesnorling.clases.IdiomasArrayAdapter
import java.util.*

class MenuPrincipal : AppCompatActivity() {
    val spinnerIdiomas : Spinner by lazy{findViewById<Spinner>(R.id.spinnerIdiomas)}
    val imgWhatsapp : ImageView by lazy{findViewById<ImageView>(R.id.imgWhatsapp)}
    val imgFacebook : ImageView by lazy{findViewById<ImageView>(R.id.imgFacebook)}
    val imgTwitter : ImageView by lazy{findViewById<ImageView>(R.id.imgTwitter)}
    val imgLinkedin : ImageView by lazy{findViewById<ImageView>(R.id.imgLinkedin)}
    val imgYoutube : ImageView by lazy{findViewById<ImageView>(R.id.imgYoutube)}
    val imgInstagram : ImageView by lazy{findViewById<ImageView>(R.id.imgInstagram)}
    val imgCasa : ImageView by lazy{findViewById<ImageView>(R.id.imgCasa)}
    val btnVenta : Button by lazy{findViewById<Button>(R.id.btnVenta)}
    val btnAlquiler : Button by lazy{findViewById<Button>(R.id.btnAlquiler)}
    val btnVacaciones : Button by lazy{findViewById<Button>(R.id.btnVacaciones)}
    lateinit var locale : Locale
    private var idiomaActual = "es"
    private var idioma : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_menu_principal)
        idiomaActual = intent.getStringExtra(idioma).toString()

        val idiomas = arrayOf(
            Pair(getString(R.string.espanol), R.drawable.espana),
            Pair(getString(R.string.ingles), R.drawable.uk),
            Pair(getString(R.string.aleman), R.drawable.alemania),
            Pair(getString(R.string.frances), R.drawable.francia),
            Pair(getString(R.string.sueco), R.drawable.suecia)
        )

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


        spinnerIdiomas.adapter = adapter

        when (idiomaActual) {
            "es" -> spinnerIdiomas.setSelection(0)
            "en" -> spinnerIdiomas.setSelection(1)
            "de" -> spinnerIdiomas.setSelection(2)
            "fr" -> spinnerIdiomas.setSelection(3)
            "sv" -> spinnerIdiomas.setSelection(4)
        }

        //TODO probar a ver si esto funciona

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
            //TODO volver al menú principal. Esta parte debería estar en un fragment
        }
        btnVenta.setOnClickListener {
            cambiarPantalla("Venta", btnVenta)
        }
        btnAlquiler.setOnClickListener {
            cambiarPantalla("Alquiler", btnAlquiler)
        }
        btnVacaciones.setOnClickListener {
            cambiarPantalla("Vacaciones", btnVacaciones)
        }

    }

    private fun abrirWeb (url : String) {
        val abrirPagina : Intent = Intent(android.content.Intent.ACTION_VIEW)
        abrirPagina.data = Uri.parse(url)
        startActivity(abrirPagina)
    }

    fun cambiarPantalla (tipoAnuncio : String, boton : Button) {
        val intent : Intent = Intent(this, PantallaAnuncios::class.java)
        var zona : String = ""
        intent.putExtra("tipoAnuncio", tipoAnuncio)
        val inflater : LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView : View = inflater.inflate(R.layout.layout_radiogroup, null)
        val popupVentana = PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val radioGroup = popupView.findViewById<RadioGroup>(R.id.groupZonas)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            //Ñapa monumental, cambiar esto más adelante
            when (checkedId) {
                R.id.btnTorre -> zona = "Torre del Mar"
                R.id.btnVelez -> zona = "Vélez-Málaga"
                R.id.btnAlgarrobo -> zona = "Algarrobo"
                R.id.btnAlmachar -> zona = "Almáchar"
                R.id.btnAlmayate -> zona = "Almayate"
                R.id.btnBenajarafe -> zona = "Benajarafe"
                R.id.btnBenamargosa -> zona = "Benamargosa"
                R.id.btnCaleta -> zona = "Caleta de Vélez"
                R.id.btnCanillas -> zona = "Canillas de Aceituno"
                R.id.btnTorrox -> zona = "Torrox"

            }
            intent.putExtra("zona", zona)
            Toast.makeText(this, "Tipo de anuncio: " + tipoAnuncio + ", Zona: " + zona, Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
        popupVentana.showAtLocation(boton, Gravity.CENTER, 0, 0)


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
            val refresh = Intent(this, MenuPrincipal::class.java)
            refresh.putExtra(idioma, localeName)
            startActivity(refresh)
        }

    }
}