package cz.jiricerveny.heroapp.spacex.launches.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Launch::class], version = 1, exportSchema = false)
abstract class LaunchDatabase : RoomDatabase() {
    abstract val launchDatabaseDao: LaunchDatabaseDao

    // TODO this seems too complicated. You could've just created the db in Application, which would make it singleton by default. Also, kotlin objects are singletons so maybe could've used that.

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