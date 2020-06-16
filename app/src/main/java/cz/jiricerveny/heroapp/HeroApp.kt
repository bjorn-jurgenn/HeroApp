package cz.jiricerveny.heroapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.room.Room
import cz.jiricerveny.heroapp.spacex.ServiceBuilder
import cz.jiricerveny.heroapp.spacex.SpaceXEndpoints
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabase

class HeroApp : Application() {
    val CHANNEL_1_ID = "channel1"
    val CHANNEL_2_ID = "channel2"

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
    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
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

}