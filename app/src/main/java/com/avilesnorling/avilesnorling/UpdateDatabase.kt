package com.avilesnorling.avilesnorling

import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.avilesnorling.avilesnorling.clases.Anuncio
import com.avilesnorling.avilesnorling.clases.Helper
import org.jdom2.input.SAXBuilder
import java.net.URL
import java.time.LocalDateTime

class UpdateDatabase : Service() {
    companion object {
        const val CHANNEL_ID = "Database Update"

        fun startService(context: Context) {
            val intent = Intent(context, UpdateDatabase::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, UpdateDatabase::class.java)
            context.stopService(stopIntent)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Manejo del XML

        val xml = URL("https://avilesnorling.inmoenter.com/export/all/xcp.xml")
        val builder = SAXBuilder()
        val document = builder.build(xml)
        val root = document.rootElement

        //Base de datos
        val querier : SQLiteDatabase = Helper(this).writableDatabase

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
            }
            catch (e : java.lang.NumberFormatException) {
                tipoInmueble = 0
            }
            var tipoOferta: Int?
            try {
                tipoOferta = elemento.getChildText("tipoOferta").toInt()
            }
            catch (e : java.lang.NumberFormatException) {
                tipoOferta = 0
            }
            var codigoPostal: Int?
            try {
                codigoPostal = elemento.getChildText("codigoPostal").toInt()
            }
            catch (e : java.lang.NumberFormatException) {
                codigoPostal = 0
            }
            var provincia: String =
                elemento.getChildText("provincia")
            if (provincia == null) {
                provincia = ""
            }
            var localidad: String =
                elemento.getChildText("localidad")
            if (localidad == null) {
                localidad = ""
            }
            var direccion: String =
                elemento.getChildText("direccion")
            if (direccion == null) {
                direccion = ""
            }
            var geoLocalizacion: String =
                elemento.getChildText("geoLocalizacion")
            if (geoLocalizacion == null) {
                geoLocalizacion = ""
            }
            var registroTurismo: String? =
                elemento.getChildText("registroTurismo")
            if (registroTurismo == null) {
                registroTurismo = ""
            }

            val descripcion = elemento.getChild("descripcionPrincipal").getChildren("descripcion")
            //Español
            val espaniol = descripcion[0].getChildText("texto")
            //Inglés
            val ingles : String?
            if (descripcion.size >= 2) {
                ingles = descripcion[1].getChildText("texto")
            }
            else {
                ingles = ""
            }
            val frances : String?
            if (descripcion.size >= 3) {
                //Francés
                frances = descripcion[2].getChildText("texto")
            }
            else {
                frances = ""
            }
            //Alemán
            val aleman : String?
            if (descripcion.size >= 5) {
                aleman = descripcion[4].getChildText("texto")
            }
            else {
                aleman = ""
            }
            //Sueco
            val sueco : String?
            if (descripcion.size >= 8) {
                sueco = descripcion[7].getChildText("texto")
            }
            else {
                sueco = ""
            }

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
        var contador : Int = 0
        for (dato in datos) {
            try {
                val sql: String =
                    "INSERT INTO propiedades (referencia, fecha, url, tipoInmueble, tipoOferta, descripcionEs, " +
                            "descripcionEn, descripcionFr, descripcionDe, descripcionSv, codigoPostal, provincia, " +
                            "localidad, direccion, geoLocalizacion, registroTurismo) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
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
                statement.executeInsert()
            }
            catch (e : SQLiteConstraintException) {
                e.message?.let { Log.e("Error", it) }
                contador +=1
                continue

            }
        }
        Log.e("referenciasRepetidas", "" + contador)
        stopSelf()
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}