package com.avilesnorling.avilesnorling.clases

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.avilesnorling.avilesnorling.UpdateDatabase

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent) {
        val wakeLock = (p0.getSystemService(Context.POWER_SERVICE) as PowerManager)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "app:MyWakeLockTag")
        wakeLock.acquire(10*60*1000L /*10 minutes*/)
        UpdateDatabase.startService(p0)
        wakeLock.release()
    }

}