package cz.jiricerveny.heroapp.spacex.launches.database

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
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
    fun getList(): LiveData<List<Launch>>

    @Query("SELECT * FROM launch_database WHERE launch_success = :key")
    fun getBySuccess(key: Boolean): LiveData<List<Launch>>

    @Query("SELECT * FROM launch_database WHERE launch_year = :key")
    fun getFromYear(key: Int): LiveData<List<Launch>>

    @Query("SELECT * FROM launch_database WHERE launch_year = :launchYear AND launch_success = :success")
    fun getBySuccessFromYear(success: Boolean, launchYear: Int): LiveData<List<Launch>>

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

    fun getList(callback: (list: LiveData<List<Launch>>) -> Unit) {
        threadPool.execute {
            val list = dao.getList()
            Log.i("LaunchDatabaseDao", "livedata value: ${list.value}")
            mainHandler.post { callback(list) }
        }
    }

    fun getBySuccess(key: Boolean, callback: (list: LiveData<List<Launch>>) -> Unit) {
        threadPool.execute {
            val list = dao.getBySuccess(key)
            Log.i("LaunchDatabaseDao", "livedata value: ${list.value}")
            mainHandler.post { callback(list) }
        }
    }

    fun getFromYear(key: Int, callback: (list: LiveData<List<Launch>>) -> Unit) {
        threadPool.execute {
            val list = dao.getFromYear(key)
            Log.i("LaunchDatabaseDao", "livedata value: ${list.value}")
            mainHandler.post { callback(list) }
        }
    }

    fun getBySuccessFromYear(
        success: Boolean,
        launchYear: Int,
        callback: (list: LiveData<List<Launch>>) -> Unit
    ) {
        threadPool.execute {
            val list = dao.getBySuccessFromYear(success, launchYear)
            Log.i("LaunchDatabaseDao", "livedata value: ${list.value}")
            mainHandler.post { callback(list) }
        }
    }

    fun clear() {
        threadPool.execute {
            dao.clear()
        }
    }
}