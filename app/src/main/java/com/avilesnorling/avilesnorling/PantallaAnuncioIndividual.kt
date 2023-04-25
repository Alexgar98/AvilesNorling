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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avilesnorling.avilesnorling.clases.FotoRecyclerAdapter
import com.avilesnorling.avilesnorling.clases.Helper
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jdom2.DefaultJDOMFactory
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import java.net.URL
import java.util.*
import javax.xml.parsers.SAXParserFactory

class PantallaAnuncioIndividual : AppCompatActivity() {
    //val imagenPrincipal : ImageView by lazy {findViewById<ImageView>(R.id.imagenPrincipal)}
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
    val recyclerFotos : RecyclerView by lazy {findViewById<RecyclerView>(R.id.recyclerFotos)}
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

        //Saco los datos del XML porque no quiero engordar aún más la clase Anuncio
        sacarElemento(urlAnuncio) {elemento ->
            runOnUiThread {
                if (elemento != null) {
                    titulo.text = elemento!!.getChild("extensionInmoenter").getChild("listaTitulos")
                        .getChild("titulo").getChildText("texto")
                    val precio : String? = elemento!!.getChildText("precio")
                    if (precio != null && precio != "0") {
                        precioTexto.text = precio + " €"
                    }
                    else {
                        precioTexto.visibility = View.GONE
                    }
                    val listaImagenes = elemento!!.getChild("listaImagenes").getChildren("imagen")
                    val imagenes : ArrayList<String?> = arrayListOf()
                    for (i in 0 until listaImagenes.size) {
                        imagenes.add(listaImagenes[i].getChildText("url"))
                    } //TODO Recycler para meter el resto de imágenes
                    //Picasso.get().load(imagenes[0]).into(imagenPrincipal)
                    recyclerFotos.layoutManager = LinearLayoutManager(this)
                    val recyclerAdapter = FotoRecyclerAdapter(imagenes)
                    recyclerFotos.adapter = recyclerAdapter
                    var textoGeneral : String = ""
                    val registroTurismo : String? = elemento!!.getChildText("registroTurismo")
                    val ascensor : String? = elemento!!.getChildText("ascensor")
                    val salones : String? = elemento!!.getChild("extensionInmoenter").getChildText("salones")
                    val dormitorios : String? = elemento!!.getChildText("dormitorios")
                    val banos : String? = elemento!!.getChildText("baños")
                    val empotrados : String? = elemento!!.getChildText("armariosEmpotrados")
                    val terrazas : String? = elemento!!.getChildText("terrazas")
                    val superficieTerrazas : String? = elemento!!.getChildText("superficieTerrazas")
                    val planta : String? = elemento!!.getChild("extensionInmoenter").getChildText("nplantas")
                    val lavadero : String? = elemento!!.getChild("extensionInmoenter").getChildText("lavadero")
                    if (registroTurismo != null) {
                        textoGeneral += "\n- Código Turístico: " + registroTurismo
                    }
                    if (planta != null) {
                        textoGeneral += "\n- " + planta + "ª planta"
                    }
                    if (ascensor != null) {
                        textoGeneral += "\n- Ascensor"
                    }
                    if (salones != null) {
                        textoGeneral += "\n- " + salones + " salones"
                    }
                    if (dormitorios != null) {
                        textoGeneral += "\n- " + dormitorios + " dormitorios"
                    }
                    if (banos != null) {
                        textoGeneral += "\n- " + banos + " baños"
                    }
                    if (empotrados != null) {
                        textoGeneral += "\n- " + empotrados + " armarios empotrados"
                    }
                    if (terrazas != null) {
                        textoGeneral += "\n- " + terrazas
                        if (superficieTerrazas != null) {
                            textoGeneral += " (" + superficieTerrazas + " m2)"
                        }
                    }
                    if (lavadero != null) {
                        textoGeneral += "\n- Lavadero"
                    }
                    generales.text = textoGeneral + "\n"

                    val construido : String? = elemento!!.getChildText("superficieConstruida")
                    val util : String? = elemento!!.getChildText("superficieUtil")
                    var textoSuperficies : String = ""
                    if (construido != null) {
                        textoSuperficies +="\n- Constr.: " + construido + " m2"
                    }
                    if (util != null) {
                        textoSuperficies +="\n- Útil: " + util + " m2"
                    }
                    superficies.text = textoSuperficies + "\n"

                    val piscina : String? = elemento!!.getChildText("piscina")
                    val jardines : String? = elemento!!.getChildText("jardines")
                    val cocinaAmueblada : String? = elemento!!.getChildText("cocinaAmueblada")
                    val electrodomesticos : String? = elemento!!.getChildText("electrodomesticos")
                    val portero : String? = elemento!!.getChildText("tipoPortero")
                    val tipoCocina : String? = elemento!!.getChild("extensionInmoenter").getChildText("tipoCocina")
                    val aireAcondicionado : String? = elemento!!.getChildText("tipoAireAcondicionado")
                    var textoEquipamientos : String = ""
                    if (piscina != null) {
                        textoEquipamientos +="\n- Piscina"
                    }
                    if (jardines != null) {
                        textoEquipamientos +="\n- Jardines"
                    }
                    if (tipoCocina != null) {
                        if (tipoCocina == "1") {
                            textoEquipamientos += "\n- Cocina independiente"
                        }
                        else if (tipoCocina == "2") {
                            textoEquipamientos += "\n- Cocina americana"
                        }
                        else {
                            textoEquipamientos += "\n- Cocina amueblada"
                        }
                    }
                    if (cocinaAmueblada != null) {
                        textoEquipamientos +="\n- Cocina amueblada"
                    }
                    if (electrodomesticos != null) {
                        textoEquipamientos +="\n- Electrodomésticos"
                    }
                    if (aireAcondicionado != null) {
                        if (aireAcondicionado == "2") {
                            textoEquipamientos += "\n- Aire Acondicionado / Instalación"
                        }
                        else if (aireAcondicionado == "3") {
                            textoEquipamientos += "\n- A/C Climatizador"
                        }
                        else {
                            if (elemento!!.getChildText("tipoConservacion") == "6") {
                                textoEquipamientos += "\n- Aire Acondicionado / Preinstalación"
                            }
                            else {
                                textoEquipamientos += "\n- Aire acondicionado central"
                            }
                        }
                    }
                    if (portero != null) {
                        if (portero == "1") {
                            textoEquipamientos +="\n- Portero automático"
                        }
                        else {
                            textoEquipamientos +="\n- Video-portero"
                        }
                    }
                    equipamientos.text = textoEquipamientos + "\n"

                    var textoCalidades : String = ""
                    val soleria : String? = elemento!!.getChildText("tipoSoleria")
                    if (soleria != null) {
                        if (soleria == "5") {
                            textoCalidades += "\n- Solería de cerámica"
                        }
                        else if (soleria == "1") {
                            textoCalidades += "\n- Solería de parquet"
                        }
                        else if (soleria == "6") {
                            textoCalidades += "\n- Tarima"
                        }
                        else {
                            textoCalidades += "\n- Solería de mármol"
                        }
                    }
                    calidades.text = textoCalidades + "\n"

                    var textoSituacion : String = ""
                    val zona : String? = elemento!!.getChildText("tipoZona")
                    val playa : String? = elemento!!.getChildText("tipoPlaya")
                    val orientacion : String? = elemento!!.getChildText("tipoOrientacion")
                    if (zona != null) {
                        if (zona == "1") {
                            textoSituacion += "\n- Zona urbana"
                        }
                        else {
                            textoSituacion += "\n- Urbanización"
                        }
                    }
                    if (playa != null) {
                        if (playa == "2") {
                            textoSituacion += "\n- A 500 metros de la playa"
                        }
                        else if (playa == "4") {
                            textoSituacion += "\n- 2ª Línea de playa"
                        }
                        else if (playa == "3") {
                            textoSituacion += "\n- En zona costera"
                        }
                        else {
                            textoSituacion += "\n- 1ª Línea de playa"
                        }
                    }
                    if (orientacion != null) {
                        if (orientacion == "3") {
                            textoSituacion += "\n- Orientación este"
                        }
                        else if (orientacion == "4") {
                            textoSituacion += "\n- Orientación oeste"
                        }
                        else if (orientacion == "2") {
                            textoSituacion += "\n- Orientación sur"
                        }
                        else if (orientacion == "8") {
                            textoSituacion += "\n- Orientación suroeste"
                        }
                        else if (orientacion == "7") {
                            textoSituacion += "\n- Orientación sureste"
                        }
                        else {
                            textoSituacion += "\n- Orientación noroeste"
                        }
                    }

                    situacion.text = textoSituacion + "\n"

                    var textoCercaDe : String = ""
                    val escuelas : String? = elemento!!.getChildText("centrosEscolares")
                    val deporte : String? = elemento!!.getChildText("instalacionesDeportivas")
                    val verde : String? = elemento!!.getChildText("espaciosVerdes")
                    if (escuelas != null) {
                        textoCercaDe += "\n- Escuelas"
                    }
                    if (deporte != null) {
                        textoCercaDe += "\n- Zonas deportivas"
                    }
                    if (verde != null) {
                        textoCercaDe += "\n- Zonas verdes"
                    }

                    cercaDe.text = textoCercaDe + "\n"

                    var textoComunicaciones : String = ""
                    val bus : String? = elemento!!.getChildText("autobuses")
                    if (bus != null) {
                        textoComunicaciones += "\n- Bus"
                    }

                    comunicaciones.text = textoComunicaciones + "\n"

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
                } else {
                    Toast.makeText(
                        this,
                        "No se pudieron obtener los datos. Volviendo atrás",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent: Intent = Intent(this, PantallaAnuncios::class.java)
                    startActivity(intent)
                }
            }
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
                e.message?.let { android.util.Log.e("Error", it) }
                runOnUiThread {
                    callback(null)
                }
            }
        }.start()
    }
}