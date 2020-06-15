package cz.jiricerveny.heroapp

import android.app.Application
import androidx.room.Room
import cz.jiricerveny.heroapp.spacex.ServiceBuilder
import cz.jiricerveny.heroapp.spacex.SpaceXEndpoints
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabase

class HeroApp : Application() {
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
}