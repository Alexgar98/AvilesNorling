package com.avilesnorling.avilesnorling.clases

import java.time.LocalDateTime

data class Anuncio (val referencia : String, val fecha : LocalDateTime, val url : String, val tipoInmueble : Int?, val tipoOferta : Int?,
    val descripcionEs : String?, val descripcionEn : String?, val descripcionFr : String?,
    val descripcionDe : String?, val descripcionSv : String?, val codigoPostal : Int?, val provincia : String, val localidad : String, val direccion : String,
    val geoLocalizacion : String, val registroTurismo : String?, val imgPrincipal : String, val precio : Int?, val dormitorios : Int?,
    val superficie : Int?, val banos : Int?, val vacacional : Boolean) {

}