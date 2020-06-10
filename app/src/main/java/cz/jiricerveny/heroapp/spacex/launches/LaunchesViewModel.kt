
package cz.jiricerveny.heroapp.spacex.launches

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabaseDao
import kotlinx.coroutines.*

class LaunchesViewModel(private val database: LaunchDatabaseDao, application: Application) :
    AndroidViewModel(application) {
    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _displayableLaunches = MutableLiveData<List<Launch>>()
    val displayableLaunches: LiveData<List<Launch>>
        get() = _displayableLaunches

    init {
        _displayableLaunches.value = listOf()
    }

    fun setDisplayable() {
        uiScope.launch {
            val result = displayAll()
            Log.i("setDisplayable", result.toString())
            _displayableLaunches.value = result
        }
    }

    private suspend fun displayAll(): List<Launch> {
        return withContext(Dispatchers.IO) {
            database.getList()
        }
    }

    private suspend fun insert(launch: Launch) {
        return withContext(Dispatchers.IO) {
            database.insert(launch)
        }
    }

    fun addLaunch(launch: Launch) {
        uiScope.launch {
            insert(launch)
        }
        Log.i("addLaunch", launch.toString())
    }

    private suspend fun displayDataFromYear(year: Int): List<Launch> {
        return withContext(Dispatchers.IO) {
            database.getFromYear(year)
        }
    }

    fun getFromYear(year: Int) {
        uiScope.launch {
            val result = displayDataFromYear(year)
            _displayableLaunches.value = result
        }
    }

    private suspend fun displayDataBySuccess(success: Boolean): List<Launch> {
        return withContext(Dispatchers.IO) {
            database.getBySuccess(success)
        }
    }

    fun getBySuccess(success: Boolean) {
        uiScope.launch {
            val result = displayDataBySuccess(success)
            _displayableLaunches.value = result
        }
    }

    private suspend fun displayDataBySuccessFromYear(success: Boolean, year: Int): List<Launch> {
        return withContext(Dispatchers.IO) {
            database.getBySuccessFromYear(success, year)
        }
    }

    fun getBySuccessFromYear(success: Boolean, year: Int) {
        uiScope.launch {
            val result = displayDataBySuccessFromYear(success, year)
            _displayableLaunches.value = result
        }
    }
}
