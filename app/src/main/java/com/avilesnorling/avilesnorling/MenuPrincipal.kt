package com.avilesnorling.avilesnorling

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import com.avilesnorling.avilesnorling.clases.Anuncio
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jdom2.input.SAXBuilder
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.*
import java.net.URL
import java.sql.DriverManager
import java.time.LocalDateTime
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.SAXParserFactory

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
        //Se actualiza la base de datos por si el XML ha cambiado
        GlobalScope.launch {
            updateDatabase()
        }
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
        //Botones
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

    //Función para abrir la web que toque
    private fun abrirWeb (url : String) {
        val abrirPagina : Intent = Intent(android.content.Intent.ACTION_VIEW)
        abrirPagina.data = Uri.parse(url)
        startActivity(abrirPagina)
    }

    //Función para cambiar a la pantalla de turno
    fun cambiarPantalla (tipoAnuncio : String, boton : Button) {
        val intent : Intent = Intent(this, PantallaAnuncios::class.java)
        var zona : String = ""
        intent.putExtra("tipoAnuncio", tipoAnuncio)
        val inflater : LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView : View = inflater.inflate(R.layout.layout_radiogroup, null)
        val popupVentana = PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val radioGroup = popupView.findViewById<RadioGroup>(R.id.groupZonas)
        if (tipoAnuncio == "Venta") {
            val btnAlgarrobo : RadioButton = popupView.findViewById<RadioButton>(R.id.btnAlgarrobo)
            btnAlgarrobo.visibility = View.VISIBLE
            val btnAlmachar : RadioButton = popupView.findViewById<RadioButton>(R.id.btnAlmachar)
            btnAlmachar.visibility = View.VISIBLE
            val btnAlmayate : RadioButton = popupView.findViewById<RadioButton>(R.id.btnAlmayate)
            btnAlmayate.visibility = View.VISIBLE
            val btnBenajarafe : RadioButton = popupView.findViewById<RadioButton>(R.id.btnBenajarafe)
            btnBenajarafe.visibility = View.VISIBLE
            val btnBenamargosa : RadioButton = popupView.findViewById<RadioButton>(R.id.btnBenamargosa)
            btnBenamargosa.visibility = View.VISIBLE
            val btnTorrox : RadioButton = popupView.findViewById<RadioButton>(R.id.btnTorrox)
            btnTorrox.visibility = View.VISIBLE
        }
        if (tipoAnuncio == "Vacaciones") {
            val btnCanillas : RadioButton = popupView.findViewById<RadioButton>(R.id.btnCanillas)
            btnCanillas.visibility = View.GONE
            val btnMalaga : RadioButton = popupView.findViewById<RadioButton>(R.id.btnMalaga)
            btnMalaga.visibility = View.VISIBLE
            val btnMalagaOriental : RadioButton = popupView.findViewById<RadioButton>(R.id.btnMalagaOriental)
            btnMalagaOriental.visibility = View.VISIBLE
        }
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
                R.id.btnMalaga -> zona = "Málaga"
                R.id.btnMalagaOriental -> zona = "Málaga oriental"

            }
            intent.putExtra("zona", zona)
            startActivity(intent)
        }
        popupVentana.showAtLocation(boton, Gravity.CENTER, 0, 0)


    }

    //Función para cambiar el idioma
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


    //Función para coger el XML y actualizar base de datos en SQLite a partir de él
    private fun updateDatabase() {
        //Manejo del XML
        try {
            val xml = URL("https://avilesnorling.inmoenter.com/export/all/xcp.xml")
            //val dbFactory = DocumentBuilderFactory.newInstance()
            //val dBuilder = dbFactory.newDocumentBuilder()
            //var xmlModificado = xml.readText()
            //xmlModificado.replace('ñ', 'n')
            //val inputStream = xml.openStream()
            //val documento = dBuilder.parse(InputSource(StringReader(xmlModificado)))

            val builder = SAXBuilder()
            val document = builder.build(xml)
            val root = document.rootElement

            //Base de datos
            val baseDatos = File("/data/propiedades.db")
            val conexion = DriverManager.getConnection("jdbc:sqlite:${baseDatos.absolutePath}")
            val statement = conexion.createStatement()
            //Borro la tabla si hay para hacerla de nuevo con los datos actualizados del XML
            statement.executeUpdate("DROP TABLE IF EXISTS propiedades")
            statement.executeUpdate(
                "CREATE TABLE propiedades (referencia TEXT PRIMARY KEY, fecha DATE, url TEXT, tipoInmueble INTEGER, tipoOferta INTEGER," +
                        "descripcionEs TEXT, descripcionEn TEXT, descripcionFr TEXT, descripcionDe TEXT, descripcionSv TEXT, codigoPostal INTEGER, " +
                        "provincia TEXT, localidad TEXT, direccion TEXT, geoLocalizacion TEXT, registroTurismo TEXT)"
            ) //Tocar si es necesario

            //Leo el XML y meto los datos en una lista de anuncios
            val datos = mutableListOf<Anuncio>()
            val elementos = root.getChild("listaPropiedades").getChildren("propiedad")

            for (i in 0 until elementos.size) {
                val elemento = elementos[i] as Element
                val referencia: String =
                    elemento.getElementsByTagName("referencia").item(0).textContent
                val fecha: LocalDateTime =
                    LocalDateTime.parse(elemento.getElementsByTagName("fecha").item(0).textContent)
                val url: String = elemento.getElementsByTagName("url").item(0).textContent
                val tipoInmueble: Int =
                    elemento.getElementsByTagName("tipoInmueble").item(0).textContent.toInt()
                val tipoOferta: Int =
                    elemento.getElementsByTagName("tipoOferta").item(0).textContent.toInt()
                val codigoPostal: Int =
                    elemento.getElementsByTagName("codigoPostal").item(0).textContent.toInt()
                val provincia: String =
                    elemento.getElementsByTagName("provincia").item(0).textContent
                val localidad: String =
                    elemento.getElementsByTagName("localidad").item(0).textContent
                val direccion: String =
                    elemento.getElementsByTagName("direccion").item(0).textContent
                val geoLocalizacion: String =
                    elemento.getElementsByTagName("geoLocalizacion").item(0).textContent
                val registroTurismo: String =
                    elemento.getElementsByTagName("registroTurismo").item(0).textContent

                val descripcion = elemento.getElementsByTagName("descripcionPrincipal") as Element
                //Español
                val descripcionEs =
                    descripcion.getElementsByTagName("descripcion").item(0) as Element
                val espaniol = descripcionEs.getElementsByTagName("texto").item(0).textContent
                //Inglés
                val descripcionEn =
                    descripcion.getElementsByTagName("descripcion").item(1) as Element
                val ingles = descripcionEn.getElementsByTagName("texto").item(0).textContent
                //Francés
                val descripcionFr =
                    descripcion.getElementsByTagName("descripcion").item(2) as Element
                val frances = descripcionFr.getElementsByTagName("texto").item(0).textContent
                //Alemán
                val descripcionDe =
                    descripcion.getElementsByTagName("descripcion").item(4) as Element
                val aleman = descripcionDe.getElementsByTagName("texto").item(0).textContent
                //Sueco
                val descripcionSv =
                    descripcion.getElementsByTagName("descripcion").item(7) as Element
                val sueco = descripcionSv.getElementsByTagName("texto").item(0).textContent

                datos.add(
                    Anuncio(
                        referencia,
                        fecha,
                        url,
                        tipoInmueble,
                        tipoOferta,
                        espaniol,
                        ingles,
                        frances,
                        aleman,
                        sueco,
                        codigoPostal,
                        provincia,
                        localidad,
                        direccion,
                        geoLocalizacion,
                        registroTurismo
                    )
                )
            }
            //Uso la lista para meter los datos en BD
            for (dato in datos) {
                statement.executeUpdate(
                    "INSERT INTO propiedades (referencia, fecha, url, tipoInmueble, tipoOferta, descripcionEs, " +
                            "descripcionEn, descripcionFr, descripcion De, descripcionSv, codigoPostal, provincia, " +
                            "localidad, direccion, geoLocalizacion, registroTurismo) VALUES (" +
                            "'${dato.referencia}', '${dato.fecha}', '${dato.url}', '${dato.tipoInmueble}', '${dato.tipoOferta}'" +
                            ", '${dato.descripcionEs}', '${dato.descripcionEn}', '${dato.descripcionFr}', '${dato.descripcionDe}'" +
                            ", '${dato.descripcionSv}', '${dato.codigoPostal}', '${dato.provincia}', '${dato.localidad}'" +
                            ", '${dato.direccion}', '${dato.geoLocalizacion}', '${dato.registroTurismo}')"
                )
            }

            statement.close()
            conexion.close()
        }
        catch (e : IOException) {
            Toast.makeText(this, "No se pudo sincronizar la base de datos", Toast.LENGTH_LONG).show()
        }
    }
}