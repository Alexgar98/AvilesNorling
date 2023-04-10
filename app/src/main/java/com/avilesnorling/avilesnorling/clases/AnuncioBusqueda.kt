package com.avilesnorling.avilesnorling.clases

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.net.URL

class AnuncioBusqueda (val urlImagen : String, val titulo : String, val urlAnuncio : String, val precio : Int,
                       val ref : String, val superficie : Int, val dormitorios : Int, val banos : Int) {
    init {
        val imagen : Bitmap? = sacarImagen(urlImagen)
    }


}

fun sacarImagen (urlImagen : String) : Bitmap? {
    var imagen : Bitmap? = null
    try {
        val `in` = URL(urlImagen).openStream()
        imagen = BitmapFactory.decodeStream(`in`)
    }
    catch (e: Exception) {
        Log.e("Error Message", e.message.toString())
        e.printStackTrace()
    }

    return imagen
}

