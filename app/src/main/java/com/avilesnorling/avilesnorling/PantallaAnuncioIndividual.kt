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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import java.net.URL
import java.util.*

class PantallaAnuncioIndividual : AppCompatActivity() {
    val imagenPrincipal : ImageView by lazy {findViewById<ImageView>(R.id.imagenPrincipal)}
    val categoriaUbicacion : TextView by lazy {findViewById<TextView>(R.id.categoriaUbicacion)}
    val referencia : TextView by lazy {findViewById<TextView>(R.id.referenciaInteriorAnuncio)}
    val descripcion : TextView by lazy {findViewById<TextView>(R.id.txtDescripcion)}
    val btnReserva : Button by lazy {findViewById<Button>(R.id.btnReserva)}
    val titulo : TextView by lazy {findViewById<TextView>(R.id.tituloAnuncio)}
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
    private var idiomaActual = Locale.getDefault().language.toString()
    private var idioma : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_anuncio_individual)

        val urlAnuncio : String? = intent.getStringExtra("urlAnuncio")
        idiomaActual = intent.getStringExtra("idioma").toString()

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
            else -> spinnerIdiomas.setSelection(0)
        }

        //Selección de idioma
        spinnerIdiomas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> setLocale("es", urlAnuncio)
                    1 -> setLocale("en", urlAnuncio)
                    2 -> setLocale("de", urlAnuncio)
                    3 -> setLocale("fr", urlAnuncio)
                    4 -> setLocale("sv", urlAnuncio)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        val helper : Helper = Helper(this)
        var querier : SQLiteDatabase = helper.writableDatabase
        val cursor : Cursor = querier.query("propiedades", null, "url = ?", arrayOf(urlAnuncio), null, null, null)
        cursor.moveToFirst()
        val tipoInmueble : Int = cursor.getInt(cursor.getColumnIndexOrThrow("tipoInmueble"))
        var inmueble : String
        when (tipoInmueble) {
            1 -> inmueble = getString(R.string.estudio)
            2 -> inmueble = getString(R.string.apartamento)
            4 -> inmueble = getString(R.string.piso)
            8 -> inmueble = getString(R.string.duplex)
            16 -> inmueble = getString(R.string.casa)
            64 -> inmueble = getString(R.string.chalet)
            128 -> inmueble = getString(R.string.villa)
            512 -> inmueble = getString(R.string.local)
            else -> inmueble = getString(R.string.propiedad)
        }
        val ubicacion : String = cursor.getString(cursor.getColumnIndexOrThrow("localidad"))
        categoriaUbicacion.text = inmueble + " " + getString(R.string.en) + " " + ubicacion
        referencia.text = "Ref: " + cursor.getString(cursor.getColumnIndexOrThrow("referencia"))
        if (idiomaActual == "es") {
            descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionEs"))
        }
        else if (idiomaActual == "en") {
            descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionEn"))
        }
        else if (idiomaActual == "fr") {
            descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionFr"))
        }
        else if (idiomaActual == "de") {
            descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionDe"))
        }
        else if (idiomaActual == "sv") {
            descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionSv"))
        }
        else {
            descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionEs"))
        }
        val esVenta : Boolean = cursor.getInt(cursor.getColumnIndexOrThrow("tipoOferta")) == 1
        if (esVenta) {
            btnReserva.visibility = View.GONE
        }

        var elemento : Element? = null
        //Saco los datos del XML porque no quiero engordar aún más la clase Anuncio
        GlobalScope.launch {
            elemento = sacarElemento(urlAnuncio)

        }
        if (elemento != null) {
            titulo.text = elemento!!.getChild("extensionInmoenter").getChild("listaTitulos").getChild("titulo").getChildText("texto")
        }
        else {
            Toast.makeText(this, "No se pudieron obtener los datos. Volviendo atrás", Toast.LENGTH_LONG).show()
            val intent : Intent = Intent(this, PantallaAnuncios::class.java)
            startActivity(intent)
        }

        btnReserva.setOnClickListener {
            //TODO Averiguar cómo hace la conversión Avaibook para entrar a la reserva por ahí. Es inviable hacerlo con cada anuncio
        }

        //Barra de arriba

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

    fun setLocale(localeName: String, url : String?) {
        if (localeName != idiomaActual) {
            locale = Locale(localeName)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration
            //Esto está deprecated, preguntar si eso porque me tiene hasta las narices (?)
            conf.locale = locale
            res.updateConfiguration(conf, dm)
            idiomaActual = localeName
            val refresh = Intent(this, PantallaAnuncioIndividual::class.java)
            refresh.putExtra("idioma", localeName)
            refresh.putExtra("urlAnuncio", url)
            startActivity(refresh)
        }

    }

    fun sacarElemento (url : String?) : Element? {
        val xml = URL("https://avilesnorling.inmoenter.com/export/all/xcp.xml")
        val builder = SAXBuilder()
        val document = builder.build(xml)
        val root = document.rootElement
        val elementosPosibles = root.getChild("Propiedades").getChildren("propiedad")
        var elemento : Element? = null

        for (i in 0 until elementosPosibles.size) {
            if (elementosPosibles[i].getChildText("url") == url) {
                elemento = elementosPosibles[i]
                break
            }
        }

        return elemento
    }
}