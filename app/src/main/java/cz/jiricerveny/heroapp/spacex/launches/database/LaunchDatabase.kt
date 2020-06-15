package cz.jiricerveny.heroapp.spacex.launches.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Launch::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LaunchDatabase : RoomDatabase() {
    abstract val launchDatabaseDao: LaunchDatabaseDao
}