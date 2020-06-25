package cz.jiricerveny.heroapp.spacex.launches

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import cz.jiricerveny.heroapp.CHANNEL_SERVICE_ID
import cz.jiricerveny.heroapp.NotificationReceiver
import cz.jiricerveny.heroapp.R

class LaunchesForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val broadcastIntent = Intent(this, NotificationReceiver::class.java)
            .putExtra("page", 6)
            .putExtra("getLaunches", true)

        val actionIntent = PendingIntent.getBroadcast(
            this,
            0,
            broadcastIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_SERVICE_ID)
            .setSmallIcon(R.drawable.ic_loading)
            .setAutoCancel(false)
            .setContentTitle("Launches Foreground Service")
            .setContentText("Load all launches?")
            .addAction(R.mipmap.ic_launcher, "Load", actionIntent)
            .build()

        startForeground(92341, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}