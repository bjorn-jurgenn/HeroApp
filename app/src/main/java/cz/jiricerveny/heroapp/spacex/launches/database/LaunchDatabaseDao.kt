package cz.jiricerveny.heroapp.spacex.launches.database

import android.os.Handler
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor


@Dao
interface LaunchDatabaseDao {
    // TODO a quality of life improvement would be to wrap all of these into callbacks, so you dont have to create new threads all the time


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(launch: Launch)

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


// TODO eg. like this
class DBWrapper(val dao: LaunchDatabaseDao, val mainHandler: Handler) {
    val threadPool = Executors.newFixedThreadPool(4)

    fun insert(launch: Launch, callback: () -> Unit) {
        threadPool.run {
            dao.insert(launch)
            mainHandler.post { callback() }
        }
    }

    fun getList(callback: (list: List<Launch>) -> Unit) {
        threadPool.run {
            val list = dao.getList()
            mainHandler.post { callback(list) }
        }
    }

    fun getBySuccess(key: Boolean): List<Launch> {
        TODO("Not yet implemented")
    }

    fun getFromYear(key: Int): List<Launch> {
        TODO("Not yet implemented")
    }

    fun getBySuccessFromYear(success: Boolean, launchYear: Int): List<Launch> {
        TODO("Not yet implemented")
    }

    fun clear() {
        TODO("Not yet implemented")
    }
}