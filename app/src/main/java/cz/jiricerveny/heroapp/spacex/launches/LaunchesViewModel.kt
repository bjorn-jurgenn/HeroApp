
package cz.jiricerveny.heroapp.spacex.launches

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.jiricerveny.heroapp.spacex.LaunchesData
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabaseDao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/** TODO
sealed class LaunchesState
data class Loaded(val list: List<Launch>): LaunchesState()
object Loading: LaunchesState()
object Failure: LaunchesState()
 */
private const val TAG = "LaunchesViewModel"
class LaunchesViewModel(
    private val database: LaunchDatabaseDao,
    private val call: Call<List<LaunchesData>>,
    private val handlerThread: LaunchesHandlerThread
) : ViewModel() {
    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            _displayableLaunches.value = msg.obj as List<Launch>
        }
    }

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
        handlerThread.getHandler().post {
            val list = database.getList()
            val msg = Message.obtain()
            msg.obj = list
            handler.sendMessage(msg)
        }
    }

    private fun addLaunch(launchItem: Launch) {
        handlerThread.getHandler().post {
            database.insert(launchItem)
        }
    }

    fun getFromYear(year: Int) {
        handlerThread.getHandler().post {
            val list = database.getFromYear(year)
            val msg = Message.obtain()
            msg.obj = list
            handler.sendMessage(msg)
        }
    }

    fun getBySuccess(success: Boolean) {
        handlerThread.getHandler().post {
            val list = database.getBySuccess(success)
            val msg = Message.obtain()
            msg.obj = list
            handler.sendMessage(msg)
        }
    }

    fun getBySuccessFromYear(success: Boolean, year: Int) {
        handlerThread.getHandler().post {
            val list = database.getBySuccessFromYear(success, year)
            val msg = Message.obtain()
            msg.obj = list
            handler.sendMessage(msg)
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
