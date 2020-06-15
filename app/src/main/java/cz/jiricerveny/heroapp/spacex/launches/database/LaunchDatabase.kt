package cz.jiricerveny.heroapp.spacex.launches.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Launch::class], version = 1, exportSchema = false)
abstract class LaunchDatabase : RoomDatabase() {
    abstract val launchDatabaseDao: LaunchDatabaseDao
}