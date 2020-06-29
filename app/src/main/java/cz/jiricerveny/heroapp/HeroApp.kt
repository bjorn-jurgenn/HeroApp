package cz.jiricerveny.heroapp

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import cz.jiricerveny.heroapp.spacex.ServiceBuilder
import cz.jiricerveny.heroapp.spacex.SpaceXEndpoints
import cz.jiricerveny.heroapp.spacex.launches.LaunchesBroadcastReceiver
import cz.jiricerveny.heroapp.spacex.launches.LaunchesForegroundService
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabase

//import cz.jiricerveny.heroapp.spacex.launches.LaunchesWifiBroadcastReceiver

const val CHANNEL_SERVICE_ID = "serviceChannel"
const val CHANNEL_1_ID = "channel1"
const val CHANNEL_2_ID = "channel2"

class HeroApp : Application() {

    private var activityVisible = false

    fun isActivityVisible(): Boolean {
        return activityVisible
    }

    fun activityStopped() {
        activityVisible = false
    }

    fun activityStarted() {
        activityVisible = true
    }

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
        createNotificationChannels()
        startAlarm()
        startNotificationService()
        //  cm.registerNetworkCallback(builder.build(), networkCallback)
    }

    /*
        override fun onTerminate() {
            super.onTerminate()
            cm.unregisterNetworkCallback(networkCallback)
        }
    */

    fun startNotificationService() {
        val serviceIntent = Intent(this, LaunchesForegroundService::class.java)
        startService(serviceIntent)
    }

    fun sendNotification(loaded: Boolean, msg: String) {
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this)

        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_1_ID
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
        val channel1 = NotificationChannel(
            CHANNEL_1_ID,
            "Channel 1",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel1.description = "This is channel 1"
        val channel2 = NotificationChannel(
            CHANNEL_2_ID,
            "Channel 2",
            NotificationManager.IMPORTANCE_LOW
        )
        channel1.description = "This is channel 2"

        val serviceChannel = NotificationChannel(
            CHANNEL_SERVICE_ID,
            "Service Channel",
            NotificationManager.IMPORTANCE_MIN
        )
        serviceChannel.description = "Service Notification Channel"

        val manager = getSystemService(NotificationManager::class.java)!!
        manager.createNotificationChannel(channel1)
        manager.createNotificationChannel(channel2)
        manager.createNotificationChannel(serviceChannel)
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