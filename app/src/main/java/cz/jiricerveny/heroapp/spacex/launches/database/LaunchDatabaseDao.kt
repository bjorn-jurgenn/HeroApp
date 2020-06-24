package cz.jiricerveny.heroapp.spacex.launches.database

import android.os.Handler
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Dao
interface LaunchDatabaseDao {
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


class DBWrapper(private val dao: LaunchDatabaseDao, private val mainHandler: Handler) {
    private val threadPool = Executors.newFixedThreadPool(4)

    fun insert(launch: Launch) {
        threadPool.execute {
            dao.insert(launch)
        }
    }

    fun getList(callback: (list: List<Launch>) -> Unit) {
        threadPool.execute {
            val list = dao.getList()
            mainHandler.post { callback(list) }
        }
    }

    fun getBySuccess(key: Boolean, callback: (list: List<Launch>) -> Unit) {
        threadPool.execute {
            val list = dao.getBySuccess(key)
            mainHandler.post { callback(list) }
        }
    }

    fun getFromYear(key: Int, callback: (list: List<Launch>) -> Unit) {
        threadPool.execute {
            val list = dao.getFromYear(key)
            mainHandler.post { callback(list) }
        }
    }

    fun getBySuccessFromYear(
        success: Boolean,
        launchYear: Int,
        callback: (list: List<Launch>) -> Unit
    ) {
        threadPool.execute {
            val list = dao.getBySuccessFromYear(success, launchYear)
            mainHandler.post { callback(list) }
        }
    }

    fun clear() {
        threadPool.execute {
            dao.clear()
        }
    }
}