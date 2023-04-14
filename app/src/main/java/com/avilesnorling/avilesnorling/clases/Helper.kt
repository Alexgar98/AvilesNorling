package com.avilesnorling.avilesnorling.clases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Helper (contexto : Context) : SQLiteOpenHelper(contexto, "propiedades", null, 12) {
    override fun onCreate(p0: SQLiteDatabase?) {
        p0!!.execSQL("drop table if exists propiedades")
        p0!!.execSQL("CREATE TABLE propiedades (referencia TEXT PRIMARY KEY, fecha DATE, url TEXT, tipoInmueble INTEGER, tipoOferta INTEGER," +
                "descripcionEs TEXT, descripcionEn TEXT, descripcionFr TEXT, descripcionDe TEXT, descripcionSv TEXT, codigoPostal INTEGER, " +
                "provincia TEXT, localidad TEXT, direccion TEXT, geoLocalizacion TEXT, registroTurismo TEXT)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

        onCreate(p0)

    }

}