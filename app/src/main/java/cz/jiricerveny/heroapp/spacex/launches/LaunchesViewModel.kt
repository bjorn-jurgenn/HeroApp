package cz.jiricerveny.heroapp.spacex.launches

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabaseDao
import kotlinx.coroutines.*

class LaunchesViewModel(val database: LaunchDatabaseDao, application: Application) :
    AndroidViewModel(application) {
    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val launches = database.getAll()

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
}
