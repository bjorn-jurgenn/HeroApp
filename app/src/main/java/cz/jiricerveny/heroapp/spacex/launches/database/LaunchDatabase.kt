package cz.jiricerveny.heroapp.spacex.launches.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Launch::class], version = 1, exportSchema = false)
abstract class LaunchDatabase : RoomDatabase() {
    abstract val launchDatabaseDao: LaunchDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: LaunchDatabase? = null

        fun getInstance(context: Context): LaunchDatabase {
            synchronized(this) {

                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LaunchDatabase::class.java,
                        "launch_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}