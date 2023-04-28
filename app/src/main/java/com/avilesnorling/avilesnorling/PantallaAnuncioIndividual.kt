package com.avilesnorling.avilesnorling

import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avilesnorling.avilesnorling.clases.FotoRecyclerAdapter
import com.avilesnorling.avilesnorling.clases.Helper
import com.avilesnorling.avilesnorling.clases.IdAnuncio
import com.bumptech.glide.Glide
import com.google.gson.Gson
import org.jdom2.DefaultJDOMFactory
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import java.net.URL
import java.util.*

class PantallaAnuncioIndividual : AppCompatActivity() {
    val categoriaUbicacion : TextView by lazy {findViewById<TextView>(R.id.categoriaUbicacion)}
    val referencia : TextView by lazy {findViewById<TextView>(R.id.referenciaInteriorAnuncio)}
    val descripcion : TextView by lazy {findViewById<TextView>(R.id.txtDescripcion)}
    val btnReserva : Button by lazy {findViewById<Button>(R.id.btnReserva)}
    val titulo : TextView by lazy {findViewById<TextView>(R.id.tituloAnuncio)}
    val generales : TextView by lazy {findViewById<TextView>(R.id.generales)}
    val superficies : TextView by lazy {findViewById<TextView>(R.id.superficies)}
    val equipamientos : TextView by lazy {findViewById<TextView>(R.id.equipamientos)}
    val calidades : TextView by lazy {findViewById<TextView>(R.id.calidades)}
    val situacion : TextView by lazy {findViewById<TextView>(R.id.situacion)}
    val cercaDe : TextView by lazy {findViewById<TextView>(R.id.cercaDe)}
    val comunicaciones : TextView by lazy {findViewById<TextView>(R.id.comunicaciones)}
    val precioTexto : TextView by lazy {findViewById<TextView>(R.id.precioAnuncioIndividual)}
    val nombreContacto : TextView by lazy {findViewById<TextView>(R.id.nombreContacto)}
    val emailContacto : TextView by lazy {findViewById<TextView>(R.id.emailContacto)}
    val telefonoContacto : TextView by lazy {findViewById<TextView>(R.id.telefonoContacto)}
    val recyclerFotos : RecyclerView by lazy {findViewById<RecyclerView>(R.id.recyclerFotos)}
    val scrollView : ScrollView by lazy {findViewById<ScrollView>(R.id.scroll)}
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
    //private var idioma : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_anuncio_individual)

        val urlAnuncio : String? = intent.getStringExtra("urlAnuncio")
        idiomaActual = intent.getStringExtra("idioma").toString()

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_cargando)
        val imagen : ImageView = dialog.findViewById(R.id.cargando)
        Glide.with(this).load(R.drawable.loading_gif).into(imagen)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)

        dialog.show()
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

        //Cojo datos del anuncio de la base de datos
        val helper = Helper(this)
        val querier : SQLiteDatabase = helper.writableDatabase
        val cursor : Cursor = querier.query("propiedades", null, "url = ?", arrayOf(urlAnuncio), null, null, null)
        cursor.moveToFirst()
        //Descodifico el tipo de inmueble
        val inmueble : String = when (cursor.getInt(cursor.getColumnIndexOrThrow("tipoInmueble"))) {
            1 -> getString(R.string.estudio)
            2 -> getString(R.string.apartamento)
            4 -> getString(R.string.piso)
            8 -> getString(R.string.duplex)
            16 -> getString(R.string.casa)
            64 -> getString(R.string.chalet)
            128 -> getString(R.string.villa)
            512 -> getString(R.string.local)
            else -> getString(R.string.propiedad)
        }
        val ubicacion : String = cursor.getString(cursor.getColumnIndexOrThrow("localidad"))
        val ref : String = cursor.getString(cursor.getColumnIndexOrThrow("referencia"))
        categoriaUbicacion.text = inmueble + " " + getString(R.string.en) + " " + ubicacion
        referencia.text = "Ref: " + ref
        //Texto de la descripción según el idioma
        if (idiomaActual == "es") {
            descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionEs")).trim()
        }
        else if (idiomaActual == "en") {
            descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionEn")).trim()
        }
        else if (idiomaActual == "fr") {
            descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionFr")).trim()
        }
        else if (idiomaActual == "de") {
            descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionDe")).trim()
        }
        else if (idiomaActual == "sv") {
            descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionSv")).trim()
        }
        else {
            descripcion.text = cursor.getString(cursor.getColumnIndexOrThrow("descripcionEs")).trim()
        }

        val esVenta : Boolean = cursor.getInt(cursor.getColumnIndexOrThrow("tipoOferta")) == 1
        if (esVenta) {
            btnReserva.visibility = View.GONE
        }

        cursor.close()

        //Quito el botón de reserva si el scroll está abajo del todo
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val margen = scrollView.getChildAt(0).height - (scrollY + scrollView.height)
            if (margen <= 10) {
                btnReserva.visibility = View.INVISIBLE
            }
            else {
                btnReserva.visibility = View.VISIBLE
            }
        }

        //Saco los datos del XML porque no quiero engordar aún más la clase Anuncio
        sacarElemento(urlAnuncio) {elemento ->
            runOnUiThread {
                if (elemento != null) {
                    titulo.text = elemento.getChild("extensionInmoenter").getChild("listaTitulos")
                        .getChild("titulo").getChildText("texto")
                    val precio : String? = elemento.getChildText("precio")
                    if (precio != null && precio != "0") {
                        precioTexto.text = precio + " €"
                    }
                    else {
                        precioTexto.visibility = View.GONE
                    }
                    val listaImagenes = elemento.getChild("listaImagenes").getChildren("imagen")
                    val imagenes : ArrayList<String?> = arrayListOf()
                    for (i in 0 until listaImagenes.size) {
                        imagenes.add(listaImagenes[i].getChildText("url"))
                    }
                    recyclerFotos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    val recyclerAdapter = FotoRecyclerAdapter(imagenes)
                    recyclerFotos.adapter = recyclerAdapter
                    var textoGeneral = ""
                    val registroTurismo : String? = elemento.getChildText("registroTurismo")
                    val ascensor : String? = elemento.getChildText("ascensor")
                    val salones : String? = elemento.getChild("extensionInmoenter").getChildText("salones")
                    val dormitorios : String? = elemento.getChildText("dormitorios")
                    val banos : String? = elemento.getChildText("baños")
                    val empotrados : String? = elemento.getChildText("armariosEmpotrados")
                    val terrazas : String? = elemento.getChildText("terrazas")
                    val superficieTerrazas : String? = elemento.getChildText("superficieTerrazas")
                    val planta : String? = elemento.getChild("extensionInmoenter").getChildText("nplantas")
                    val lavadero : String? = elemento.getChild("extensionInmoenter").getChildText("lavadero")
                    if (registroTurismo != null) {
                        textoGeneral += "\n- "+getString(R.string.codigoTuristico)+": " + registroTurismo
                    }
                    if (planta != null) {
                        textoGeneral += "\n- " + planta + getString(R.string.planta)
                    }
                    if (ascensor != null) {
                        textoGeneral += "\n- " + getString(R.string.ascensor)
                    }
                    if (salones != null) {
                        textoGeneral += "\n- " + salones + " " + getString(R.string.salones)
                    }
                    if (dormitorios != null) {
                        textoGeneral += "\n- " + dormitorios + " " + getString(R.string.dormitorios)
                    }
                    if (banos != null) {
                        textoGeneral += "\n- " + banos + " " + getString(R.string.banos)
                    }
                    if (empotrados != null) {
                        textoGeneral += "\n- " + empotrados + " " + getString(R.string.empotrados)
                    }
                    if (terrazas != null) {
                        textoGeneral += "\n- " + terrazas + " " + getString(R.string.terrazas)
                        if (superficieTerrazas != null) {
                            textoGeneral += " (" + superficieTerrazas + " m2)"
                        }
                    }
                    if (lavadero != null) {
                        textoGeneral += "\n- " + getString(R.string.lavadero)
                    }
                    generales.text = textoGeneral + "\n"

                    val construido : String? = elemento.getChildText("superficieConstruida")
                    val util : String? = elemento.getChildText("superficieUtil")
                    var textoSuperficies = ""
                    if (construido != null) {
                        textoSuperficies +="\n- " + getString(R.string.construido) + ": " + construido + " m2"
                    }
                    if (util != null) {
                        textoSuperficies +="\n- " + getString(R.string.util) + ": " + util + " m2"
                    }
                    superficies.text = textoSuperficies + "\n"

                    val piscina : String? = elemento.getChildText("piscina")
                    val jardines : String? = elemento.getChildText("jardines")
                    val cocinaAmueblada : String? = elemento.getChildText("cocinaAmueblada")
                    val electrodomesticos : String? = elemento.getChildText("electrodomesticos")
                    val portero : String? = elemento.getChildText("tipoPortero")
                    val tipoCocina : String? = elemento.getChild("extensionInmoenter").getChildText("tipoCocina")
                    val aireAcondicionado : String? = elemento.getChildText("tipoAireAcondicionado")
                    var textoEquipamientos = ""
                    if (piscina != null) {
                        textoEquipamientos +="\n- " + getString(R.string.piscina)
                    }
                    if (jardines != null) {
                        textoEquipamientos +="\n- " + getString(R.string.jardines)
                    }
                    if (tipoCocina != null) {
                        if (tipoCocina == "1") {
                            textoEquipamientos += "\n- " + getString(R.string.cocinaIndependiente)
                        }
                        else if (tipoCocina == "2") {
                            textoEquipamientos += "\n- " + getString(R.string.cocinaAmericana)
                        }
                        else {
                            textoEquipamientos += "\n- " + getString(R.string.cocinaAmueblada)
                        }
                    }
                    if (cocinaAmueblada != null) {
                        textoEquipamientos +="\n- " + getString(R.string.cocinaAmueblada)
                    }
                    if (electrodomesticos != null) {
                        textoEquipamientos +="\n- " + getString(R.string.electrodomesticos)
                    }
                    if (aireAcondicionado != null) {
                        textoEquipamientos += if (aireAcondicionado == "2") {
                            "\n- " + getString(R.string.instalacion)
                        } else if (aireAcondicionado == "3") {
                            "\n- " + getString(R.string.climatizador)
                        } else {
                            if (elemento.getChildText("tipoConservacion") == "6") {
                                "\n- " + getString(R.string.preinstalacion)
                            } else {
                                "\n- " + getString(R.string.central)
                            }
                        }
                    }
                    if (portero != null) {
                        textoEquipamientos += if (portero == "1") {
                            "\n- " + getString(R.string.portero)
                        } else {
                            "\n- " + getString(R.string.videoportero)
                        }
                    }
                    equipamientos.text = textoEquipamientos + "\n"

                    var textoCalidades = ""
                    val soleria : String? = elemento.getChildText("tipoSoleria")
                    if (soleria != null) {
                        textoCalidades += if (soleria == "5") {
                            "\n- " + getString(R.string.soleriaCeramica)
                        } else if (soleria == "1") {
                            "\n- " + getString(R.string.soleriaParquet)
                        } else if (soleria == "6") {
                            "\n- " + getString(R.string.tarima)
                        } else {
                            "\n- " + getString(R.string.soleriaMarmol)
                        }
                    }
                    calidades.text = textoCalidades + "\n"

                    var textoSituacion = ""
                    val zona : String? = elemento.getChildText("tipoZona")
                    val playa : String? = elemento.getChildText("tipoPlaya")
                    val orientacion : String? = elemento.getChildText("tipoOrientacion")
                    if (zona != null) {
                        if (zona == "1") {
                            textoSituacion += "\n- " + getString(R.string.zonaUrbana)
                        }
                        else {
                            textoSituacion += "\n- " + getString(R.string.urbanizacion)
                        }
                    }
                    if (playa != null) {
                        if (playa == "2") {
                            textoSituacion += "\n- " + getString(R.string.metrosPlaya)
                        }
                        else if (playa == "4") {
                            textoSituacion += "\n- " + getString(R.string.segundaLinea)
                        }
                        else if (playa == "3") {
                            textoSituacion += "\n- " + getString(R.string.zonaCostera)
                        }
                        else {
                            textoSituacion += "\n- " + getString(R.string.primeraLinea)
                        }
                    }
                    if (orientacion != null) {
                        if (orientacion == "3") {
                            textoSituacion += "\n- " + getString(R.string.orientacionEste)
                        }
                        else if (orientacion == "4") {
                            textoSituacion += "\n- " + getString(R.string.orientacionOeste)
                        }
                        else if (orientacion == "2") {
                            textoSituacion += "\n- " + getString(R.string.orientacionSur)
                        }
                        else if (orientacion == "8") {
                            textoSituacion += "\n- " + getString(R.string.orientacionSuroste)
                        }
                        else if (orientacion == "7") {
                            textoSituacion += "\n- " + getString(R.string.orientacionSureste)
                        }
                        else {
                            textoSituacion += "\n- " + getString(R.string.orientacionNoroste)
                        }
                    }

                    situacion.text = textoSituacion + "\n"

                    var textoCercaDe = ""
                    val escuelas : String? = elemento.getChildText("centrosEscolares")
                    val deporte : String? = elemento.getChildText("instalacionesDeportivas")
                    val verde : String? = elemento.getChildText("espaciosVerdes")
                    if (escuelas != null) {
                        textoCercaDe += "\n- " + getString(R.string.escuelas)
                    }
                    if (deporte != null) {
                        textoCercaDe += "\n- " + getString(R.string.zonasDeportivas)
                    }
                    if (verde != null) {
                        textoCercaDe += "\n- " + getString(R.string.zonasVerdes)
                    }

                    cercaDe.text = textoCercaDe + "\n"

                    var textoComunicaciones = ""
                    val bus : String? = elemento.getChildText("autobuses")
                    if (bus != null) {
                        textoComunicaciones += "\n- " + getString(R.string.bus)
                    }

                    comunicaciones.text = textoComunicaciones + "\n"
                    nombreContacto.text = elemento.getChild("extensionInmoenter").getChildText("nombreContacto")
                    emailContacto.text = elemento.getChild("extensionInmoenter").getChildText("emailContacto")
                    telefonoContacto.text = elemento.getChild("extensionInmoenter").getChildText("telefonoContacto")

                    if (textoSituacion == "") {
                        situacion.visibility = View.GONE
                        findViewById<TextView>(R.id.tituloSituacion).visibility = View.GONE
                    }

                    if (textoCercaDe == "") {
                        cercaDe.visibility = View.GONE
                        findViewById<TextView>(R.id.tituloCercaDe).visibility = View.GONE
                    }

                    if (textoComunicaciones == "") {
                        comunicaciones.visibility = View.GONE
                        findViewById<TextView>(R.id.tituloComunicaciones).visibility = View.GONE
                    }

                    if (textoGeneral == "") {
                        generales.visibility = View.GONE
                        findViewById<TextView>(R.id.tituloGenerales).visibility = View.GONE
                    }
                    if (textoSuperficies == "") {
                        superficies.visibility = View.GONE
                        findViewById<TextView>(R.id.tituloSuperficies).visibility = View.GONE
                    }
                    if (textoEquipamientos == "") {
                        equipamientos.visibility = View.GONE
                        findViewById<TextView>(R.id.tituloEquipamiento).visibility = View.GONE
                    }
                    if (textoCalidades == "") {
                        calidades.visibility = View.GONE
                        findViewById<TextView>(R.id.tituloCalidades).visibility = View.GONE
                    }

                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this,
                        "No se pudieron obtener los datos. Volviendo atrás",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(this, PantallaAnuncios::class.java)
                    startActivity(intent)
                }
            }
        }

        //Leo el json de propiedades para generar el enlace de Avaibook cuando haga falta
        val inputStream = this.assets.open("propiedades.json")
        val json = inputStream.bufferedReader().use { it.readText() }
        val gson = Gson()
        val propiedades = gson.fromJson(json, Array<IdAnuncio>::class.java)
        val idAvail : String?
        val idOpinan : String?
        try {
            for (i in propiedades.indices) {
                if (propiedades[i].cod_an == ref) {
                    idAvail = propiedades[i].id_avail
                    idOpinan = propiedades[i].id_opinan
                    btnReserva.setOnClickListener {
                        abrirWeb("https://www.avaibook.com/reservas/nueva_reserva.php?idw=" + idAvail + "&cod_propietario=89412&cod_alojamiento=" + idOpinan + "&lang=" + idiomaActual)
                    }
                    break
                } else {
                    continue
                }

            }
        }
        catch (e: Exception) {
            btnReserva.visibility = View.GONE
            Log.e("Error", e.message, e)
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
            val intentCasa = Intent(this, MenuPrincipal::class.java)
            startActivity(intentCasa)
        }
    }

    //Función para abrir la web que toque
    private fun abrirWeb (url : String) {
        val abrirPagina = Intent(Intent.ACTION_VIEW)
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

    //Función para sacar un elemento del XML
    fun sacarElemento (url : String?, callback : (Element?) -> Unit ) {
        Thread {
            try {
                val xml = URL("https://avilesnorling.inmoenter.com/export/all/xcp.xml")
                val builder = SAXBuilder()
                val factory = DefaultJDOMFactory()
                builder.jdomFactory = factory
                val document = builder.build(xml)
                val root = document.rootElement
                val elementosPosibles = root.getChild("listaPropiedades").getChildren("propiedad")
                var elemento: Element? = null

                for (i in 0 until elementosPosibles.size) {
                    if (elementosPosibles[i].getChildText("url") == url) {
                        elemento = elementosPosibles[i]
                        break
                    }
                }
                runOnUiThread {
                    callback(elemento)
                }
            }
            catch (e : Exception) {
                e.message?.let { Log.e("Error", it) }
                runOnUiThread {
                    callback(null)
                }
            }
        }.start()
    }
}