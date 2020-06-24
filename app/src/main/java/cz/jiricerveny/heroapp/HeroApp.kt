package cz.jiricerveny.heroapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.database.Observable
import android.net.ConnectivityManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.room.Room
import cz.jiricerveny.heroapp.spacex.ServiceBuilder
import cz.jiricerveny.heroapp.spacex.SpaceXEndpoints
import cz.jiricerveny.heroapp.spacex.launches.LaunchesBroadcastReceiver
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabase
import kotlin.properties.ObservableProperty

//import cz.jiricerveny.heroapp.spacex.launches.LaunchesWifiBroadcastReceiver


class HeroApp : Application() {
    val CHANNEL_1_ID = "channel1"
    val CHANNEL_2_ID = "channel2"
    //    var isWifiConnected: Boolean? = false
    //private val cm by lazy { getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }


/*    private val builder: NetworkRequest.Builder = NetworkRequest.Builder()
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            isWifiConnected =
                cm.getNetworkCapabilities(network)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            if (isWifiConnected == true) {
                Toast.makeText(applicationContext, "Wifi Connected", Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, LaunchesWifiBroadcastReceiver::class.java)
                intent.putExtra("STATE", "CONNECTED")
                sendBroadcast(intent)
            }
        }
    }*/


    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            LaunchDatabase::class.java,
            "launch_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    val service = ServiceBuilder.buildService(SpaceXEndpoints::class.java)

    override fun onCreate() {
        super.onCreate()
        //cm.registerNetworkCallback(builder.build(), networkCallback)
        startAlarm()
        createNotificationChannels()
    }

    /*
        override fun onTerminate() {
            super.onTerminate()
            cm.unregisterNetworkCallback(networkCallback)
        }
    */

    fun sendNotification(loaded: Boolean, msg: String) {
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this)

        val notification = NotificationCompat.Builder(
            this,
            (this.applicationContext as HeroApp).CHANNEL_1_ID
        )
            .setContentText(msg)
            .setSmallIcon(R.drawable.ic_one)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
        if (loaded) {
            notification.setContentTitle("New data")

        } else {
            notification.setContentTitle("No Wi-Fi")
        }
        notificationManager.notify(1, notification.build())
    }

    private fun createNotificationChannels() {
        val chanel1 = NotificationChannel(
            CHANNEL_1_ID,
            "Channel 1",
            NotificationManager.IMPORTANCE_HIGH
        )
        chanel1.description = "This is channel 1"
        val chanel2 = NotificationChannel(
            CHANNEL_2_ID,
            "Channel 2",
            NotificationManager.IMPORTANCE_LOW
        )
        chanel1.description = "This is channel 2"

        val manager = getSystemService(NotificationManager::class.java)!!
        manager.createNotificationChannel(chanel1)
        manager.createNotificationChannel(chanel2)
    }

    private fun startAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent =
            Intent(applicationContext, LaunchesBroadcastReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(applicationContext, 0, intent, 0)
            }
        val firstTime = System.currentTimeMillis() + 10000
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, (60 * 1000), alarmIntent)
    }
}
