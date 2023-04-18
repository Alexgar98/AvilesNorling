package com.avilesnorling.avilesnorling

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabaseLockedException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.avilesnorling.avilesnorling.clases.AlarmReceiver
import com.avilesnorling.avilesnorling.clases.Anuncio
import com.avilesnorling.avilesnorling.clases.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jdom2.input.SAXBuilder
import java.io.IOException
import java.net.URL
import java.time.LocalDateTime
import java.util.*

class PantallaCarga : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_pantalla_carga)
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
        }

        val intent : Intent = Intent(this@PantallaCarga, MenuPrincipal::class.java)
        startActivity(intent)

    }
}