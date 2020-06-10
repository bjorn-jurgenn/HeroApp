package cz.jiricerveny.heroapp.spacex.launches.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface LaunchDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(launch: Launch)

    // TODO Just keep in mind, that in some cases it could be better to just filter the list programmatically

    @Query("SELECT * FROM launch_database")
    fun getAll(): LiveData<List<Launch>>

    @Query("SELECT * FROM launch_database")
    fun getList(): List<Launch>

    @Query("SELECT * FROM launch_database WHERE launch_success = :key")
    fun getBySuccess(key: Boolean): List<Launch>

    @Query("SELECT * FROM launch_database WHERE launch_year = :key")
    fun getFromYear(key: Int): List<Launch>

    @Query("SELECT * FROM launch_database WHERE launch_year = :launchYear AND launch_success = :success")
    fun getBySuccessFromYear(success: Boolean, launchYear: Int): List<Launch>

    @Query("DELETE FROM launch_database")
    fun clear()
}