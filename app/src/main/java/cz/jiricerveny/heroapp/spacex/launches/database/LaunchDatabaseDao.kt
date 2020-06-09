package cz.jiricerveny.heroapp.spacex.launches.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LaunchDatabaseDao {
    @Insert
    fun insert(launch: Launch)

    @Query("SELECT * FROM launch_database")
    fun getAll(): LiveData<List<Launch>>

    @Query("SELECT * FROM launch_database WHERE launch_success = :key")
    fun getSuccess(key: Boolean?): List<Launch>

    @Query("DELETE FROM launch_database")
    fun clear()
}