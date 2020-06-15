
package cz.jiricerveny.heroapp.spacex.launches

import android.os.Handler
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.jiricerveny.heroapp.spacex.LaunchesData
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

/** TODO
sealed class LaunchesState
data class Loaded(val list: List<Launch>): LaunchesState()
object Loading: LaunchesState()
object Failure: LaunchesState()
 */

class LaunchesViewModel(
    private val database: LaunchDatabaseDao, private val call: Call<List<LaunchesData>>
) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = CoroutineScope(Dispatchers.Main + Job()).coroutineContext


    private val _displayableLaunches: MutableLiveData<List<Launch>> = MutableLiveData()
    val displayableLaunches: LiveData<List<Launch>>
        get() = _displayableLaunches

    private val _progressBarVisible = MutableLiveData(true)
    val progressBarVisible: LiveData<Boolean>
        get() = _progressBarVisible

    private val _failure: MutableLiveData<Boolean> = MutableLiveData(false)
    val failure: LiveData<Boolean>
        get() = _failure

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message


    fun onFailureEnded() {
        _failure.value = false
    }

    fun setDisplayable() {
        launch {
            val result = displayAll()
            _displayableLaunches.value = result
        }

    }

    private suspend fun displayAll(): List<Launch> {
        return database.getList()

    }

    private suspend fun insert(launch: Launch) {
        database.insert(launch)
    }

    private fun addLaunch(launchItem: Launch) {
        launch {
            insert(launchItem)
        }
    }

    private suspend fun displayDataFromYear(year: Int): List<Launch> {
        return database.getFromYear(year)
    }

    fun getFromYear(year: Int) {
        launch {
            val result = displayDataFromYear(year)
            _displayableLaunches.value = result
        }
    }

    private suspend fun displayDataBySuccess(success: Boolean): List<Launch> {
        return database.getBySuccess(success)
    }

    fun getBySuccess(success: Boolean) {
        launch {
            val result = displayDataBySuccess(success)
            _displayableLaunches.value = result
        }
    }

    private suspend fun displayDataBySuccessFromYear(success: Boolean, year: Int): List<Launch> {
        return database.getBySuccessFromYear(success, year)
    }

    fun getBySuccessFromYear(success: Boolean, year: Int) {
        launch {
            val result = displayDataBySuccessFromYear(success, year)
            _displayableLaunches.value = result
        }
    }

    /** stáhne data ze SpaceXApi, uloží do databáze*/
    fun loadDataFromApi() {
        _progressBarVisible.value = true
        call.clone().enqueue(object : Callback<List<LaunchesData>> {
            override fun onResponse(
                call: Call<List<LaunchesData>>,
                response: Response<List<LaunchesData>>
            ) {
                val responseListOfLaunches = response.body() ?: listOf()
                /** vytvoří Launch (položka v databázi) z výstupu z retrofitu a uloží do databáze */
                for (launchItem in responseListOfLaunches) {
                    // TODO Couldn't launchItem and and Launch be the same data class? You can use annotations if you don't want to save everything.
                    // I tried but can't handle following...
                    val flightNumber = launchItem.flightNumber.toInt()
                    val missionName = launchItem.missionName
                    val upcoming = launchItem.upcoming
                    val launchYear = launchItem.launchYear
                    val launchDate = launchItem.launchDate
                    val rocket = launchItem.rocket.name // can't get this one directly
                    val success = launchItem.success
                    val launchSite = launchItem.launchSite.name // can't get this one directly
                    val detail = launchItem.detail ?: "-"
                    val wiki = launchItem.wikipedia ?: "-"
                    val launch = Launch(
                        flightNumber,
                        missionName,
                        upcoming,
                        launchYear,
                        launchDate,
                        rocket,
                        success,
                        launchSite,
                        detail,
                        wiki
                    )
                    addLaunch(launch)
                    _progressBarVisible.value = false
                }
            }

            override fun onFailure(call: Call<List<LaunchesData>>, t: Throwable) {
                _message.value = t.message
                _failure.value = true
                _progressBarVisible.value = false
            }
        })

    }

}
