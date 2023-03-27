package com.avilesnorling.avilesnorling

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import com.avilesnorling.avilesnorling.clases.Idiomas
import com.avilesnorling.avilesnorling.clases.IdiomasArrayAdapter

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_menu_principal)

        spinnerIdiomas.adapter = IdiomasArrayAdapter(
            this, listOf(
                Idiomas(R.drawable.espana, R.string.espanol.toString()),
                Idiomas(R.drawable.uk, R.string.ingles.toString()),
                Idiomas(R.drawable.alemania, R.string.aleman.toString()),
                Idiomas(R.drawable.francia, R.string.frances.toString()),
                Idiomas(R.drawable.suecia, R.string.sueco.toString())
            )
        )

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
            cambiarPantalla("Venta")
        }
        btnAlquiler.setOnClickListener {
            cambiarPantalla("Alquiler")
        }
        btnVacaciones.setOnClickListener {
            cambiarPantalla("Vacaciones")
        }




    }

    private fun abrirWeb (url : String) {
        val abrirPagina : Intent = Intent(android.content.Intent.ACTION_VIEW)
        abrirPagina.data = Uri.parse(url)
        startActivity(abrirPagina)
    }

    fun cambiarPantalla (tipoAnuncio : String) {
        val intent : Intent = Intent(this, PantallaAnuncios::class.java)
        intent.putExtra("tipoAnuncio", tipoAnuncio)
        //TODO lanzar el popup de la ubicación
        startActivity(intent)

    }
}