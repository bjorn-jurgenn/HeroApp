package cz.jiricerveny.heroapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import cz.jiricerveny.heroapp.spacex.ServiceBuilder
import cz.jiricerveny.heroapp.spacex.SpaceXEndpoints
import cz.jiricerveny.heroapp.spacex.launches.LaunchesBroadcastReceiver
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabase

//import cz.jiricerveny.heroapp.spacex.launches.LaunchesWifiBroadcastReceiver


class HeroApp : Application() {
    val CHANNEL_1_ID = "channel1"
    val CHANNEL_2_ID = "channel2"

    //    var isWifiConnected: Boolean? = false
    val newData = MutableLiveData(false)

    private val cm by lazy { getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }


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
    val service = ServiceBuilder.buildService(SpaceXEndpoints::class.java).getLaunches(null, null)
    val serviceFail =
        ServiceBuilder.buildService(SpaceXEndpoints::class.java).getLaunches(null, false)
    val serviceSuccess =
        ServiceBuilder.buildService(SpaceXEndpoints::class.java).getLaunches(null, true)
    val service2015 =
        ServiceBuilder.buildService(SpaceXEndpoints::class.java).getLaunches(2015, null)

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
