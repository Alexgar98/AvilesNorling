package com.avilesnorling.avilesnorling

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.avilesnorling.avilesnorling.clases.AlarmReceiver
import com.avilesnorling.avilesnorling.clases.Anuncio
import com.avilesnorling.avilesnorling.clases.Helper
import com.bumptech.glide.Glide
import com.github.doyaaaaaken.kotlincsv.dsl.context.ExcessFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.context.InsufficientFieldsRowBehaviour
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jdom2.input.SAXBuilder
import java.net.URL
import java.time.LocalDateTime
import java.util.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.CoroutineScope
import java.io.InputStream
import kotlin.coroutines.CoroutineContext

class PantallaCarga : AppCompatActivity() {
    val imgCarga : ImageView by lazy {findViewById<ImageView>(R.id.imgCarga)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_pantalla_carga)
        Glide.with(this).load(R.drawable.loading_gif).into(imgCarga)

        try {
            //Se actualiza la base de datos por si el XML ha cambiado
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(this, 0, intent, 0)
            }

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 3)
                set(Calendar.MINUTE, 0)
            }

            // set the alarm to go off at 3am every day
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
            )
        }
        catch (e : Exception) {
            Toast.makeText(this, R.string.noSePudoConectar, Toast.LENGTH_LONG).show()
            Log.e("Exception", e.message, e)
        } //TODO Voy a forzar a actualizar para poder probar cosas. Todo este try/catch hay que cambiarlo

        try {
            val contexto : Context = this;
            GlobalScope.launch {
                downloadFile(contexto, "https://app.avantio.com/index.php?module=Compromisos&action=ExportCsv&return_module=Compromisos&return_action=ListView&idGA=3633&estados[0]=UNPAID&estados[1]=CONFIRMADA&estados[2]=CANCELLED&estados[3]=PETICIONES_REVISAR&estados[4]=PETICIONES_DESESTIMADA&estados[5]=BAJOPETICION&estados[6]=PROPIETARIO&estados[7]=UNAVAILABLE&estados[8]=PAID&estados[9]=GARANTIA&estados[10]=CONFLICTED&estados[11]=REQUEST_TO_BOOK&fechaAltaIni=2023-10-25",
                    "Listado de reservas.csv")
                downloadFile(contexto, "https://app.avantio.com/index.php?ga=3633&module=Propiedades&action=Export&return_module=Propiedades&return_action=index",
                "Listado de propiedades.csv")
                updateDatabase()
                launch(Dispatchers.Main) {
                    val intent = Intent(this@PantallaCarga, MenuPrincipal::class.java)
                    startActivity(intent)
                }
            }
        }
        catch (e : Exception) {
            Toast.makeText(this, R.string.noSePudoConectar, Toast.LENGTH_LONG).show()
            Log.e("Exception", e.message, e)
        }

    }

    private fun downloadFile(context: Context, fileUrl: String, filename: String) {
        val url = URL(fileUrl)
        val connection = url.openConnection()
        connection.connect()

        val input : InputStream = connection.getInputStream()
        val output = context.openFileOutput(filename, Context.MODE_PRIVATE)

        val buffer = ByteArray(1024)
        var bytesRead : Int
        while (input.read(buffer).also {bytesRead = it} > 0) {
            output.write(buffer, 0, bytesRead)
        }

        output.close()
        input.close()
    }

    private fun updateDatabase() {
        val xml = URL("https://avilesnorling.inmoenter.com/export/all/xcp.xml")
        val builder = SAXBuilder()
        val document = builder.build(xml)
        val root = document.rootElement

        //Base de datos
        val helper = Helper(this)
        helper.remake() //Dropeo la tabla si hay y se meten los datos de nuevo
        val querier: SQLiteDatabase = Helper(this).writableDatabase

        //Leo el XML y meto los datos en una lista de anuncios
        val datos = mutableListOf<Anuncio>()
        val elementos = root.getChild("listaPropiedades").getChildren("propiedad")

        for (i in 0 until elementos.size) {
            val elemento = elementos[i]
            val referencia: String =
                elemento.getChildText("referencia")
            val fecha: LocalDateTime =
                LocalDateTime.parse(elemento.getChildText("fecha"))
            val url: String = elemento.getChildText("url")
            var tipoInmueble: Int?
            try {
                tipoInmueble = elemento.getChildText("tipoInmueble").toInt()
            } catch (e: java.lang.NumberFormatException) {
                tipoInmueble = 0
            }
            var tipoOferta: Int?
            try {
                tipoOferta = elemento.getChildText("tipoOferta").toInt()
            } catch (e: java.lang.NumberFormatException) {
                tipoOferta = 0
            }
            var codigoPostal: Int?
            try {
                codigoPostal = elemento.getChildText("codigoPostal").toInt()
            } catch (e: java.lang.NumberFormatException) {
                codigoPostal = 0
            }
            val provincia: String =
                elemento.getChildText("provincia")
            val localidad: String =
                elemento.getChildText("localidad")
            val direccion: String =
                elemento.getChildText("direccion")
            val geoLocalizacion: String =
                elemento.getChildText("geoLocalizacion")
            var registroTurismo: String? =
                elemento.getChildText("registroTurismo")
            if (registroTurismo == null) {
                registroTurismo = ""
            }

            val descripcion =
                elemento.getChild("descripcionPrincipal").getChildren("descripcion")
            //Español
            val espaniol = descripcion[0].getChildText("texto")
            //Inglés
            val ingles: String?
            if (descripcion.size >= 2) {
                ingles = descripcion[1].getChildText("texto")
            } else {
                ingles = ""
            }
            val frances: String?
            if (descripcion.size >= 3) {
                //Francés
                frances = descripcion[2].getChildText("texto")
            } else {
                frances = ""
            }
            //Alemán
            val aleman: String?
            if (descripcion.size >= 5) {
                aleman = descripcion[4].getChildText("texto")
            } else {
                aleman = ""
            }
            //Sueco
            val sueco: String?
            if (descripcion.size >= 8) {
                sueco = descripcion[7].getChildText("texto")
            } else {
                sueco = ""
            }
            var imgPrincipal: String? = elemento?.getChild("listaImagenes")?.getChildren("imagen")?.get(0)
                ?.getChildText("url")
            if (imgPrincipal.isNullOrBlank()) {
                imgPrincipal = ""
            }
            var precio: Int?
            try {
                precio = elemento.getChildText("precio").toInt()
            } catch (e: Exception) {
                precio = 0
            }
            var dormitorios: Int?
            try {
                dormitorios = elemento.getChildText("dormitorios").toInt()
            } catch (e: Exception) {
                dormitorios = 0
            }
            var superficie: Int?
            try {
                superficie = elemento.getChildText("superficieConstruida").toInt()
            } catch (e: Exception) {
                superficie = 0
            }
            var banos: Int?
            try {
                banos = elemento.getChildText("baños").toInt()
            } catch (e: Exception) {
                banos = 0
            }
            var vacacional : Boolean
            try {
                vacacional = elemento.getChildText("tipoOfertaExt").equals("16")
            }
            catch (e : Exception) {
                vacacional = false
            }
            var personas = 0
            val tsvReader = csvReader {
                delimiter = ';'
                excessFieldsRowBehaviour = ExcessFieldsRowBehaviour.IGNORE
                insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.IGNORE
            }
            val inputStream = this.openFileInput("Listado de propiedades.csv")
            tsvReader.open(inputStream) {
                readAllWithHeaderAsSequence().forEach { row : Map<String, String> ->
                    if (row.getOrDefault("Alojamiento", "Aquí hay algo mal") == referencia) {
                        personas = try {
                            Integer.parseInt(row.getOrDefault("Capacidad personas", "Aquí hay algo mal"))
                        } catch (e : java.lang.NumberFormatException) {
                            0
                        }
                    }
                }
            }
            inputStream.close()


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
                    registroTurismo,
                    imgPrincipal,
                    precio,
                    dormitorios,
                    superficie,
                    banos,
                    vacacional,
                    personas
                )
            )
        }

        //Uso la lista para meter los datos en BD

        for (dato in datos) {
            try {
                val sql: String =
                    "INSERT INTO propiedades (referencia, fecha, url, tipoInmueble, tipoOferta, descripcionEs, " +
                            "descripcionEn, descripcionFr, descripcionDe, descripcionSv, codigoPostal, provincia, " +
                            "localidad, direccion, geoLocalizacion, registroTurismo, imgPrincipal, precio, dormitorios," +
                            " superficie, banos, vacacional, personas) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                val statement = querier.compileStatement(sql)
                statement.bindString(1, dato.referencia)
                statement.bindString(2, dato.fecha.toString())
                statement.bindString(3, dato.url)
                statement.bindLong(4, dato.tipoInmueble!!.toLong())
                statement.bindLong(5, dato.tipoOferta!!.toLong())
                statement.bindString(6, dato.descripcionEs)
                statement.bindString(7, dato.descripcionEn)
                statement.bindString(8, dato.descripcionFr)
                statement.bindString(9, dato.descripcionDe)
                statement.bindString(10, dato.descripcionSv)
                statement.bindLong(11, dato.codigoPostal!!.toLong())
                statement.bindString(12, dato.provincia)
                statement.bindString(13, dato.localidad)
                statement.bindString(14, dato.direccion)
                statement.bindString(15, dato.geoLocalizacion)
                statement.bindString(16, dato.registroTurismo)
                statement.bindString(17, dato.imgPrincipal)
                statement.bindLong(18, dato.precio!!.toLong())
                statement.bindLong(19, dato.dormitorios!!.toLong())
                statement.bindLong(20, dato.superficie!!.toLong())
                statement.bindLong(21, dato.banos!!.toLong())
                statement.bindString(22, dato.vacacional.toString())
                statement.bindLong(23, dato.personas!!.toLong())
                statement.executeInsert()
            } catch (e: SQLiteConstraintException) {
                e.message?.let { Log.e("Error", it) }
                continue

            }
        }
    }
}