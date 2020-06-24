
package cz.jiricerveny.heroapp.spacex.launches

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.jiricerveny.heroapp.spacex.SpaceXEndpoints
import cz.jiricerveny.heroapp.spacex.launches.database.DBWrapper
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabaseDao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//TODO
sealed class LaunchesState
data class Loaded(val list: List<Launch>): LaunchesState()
object Loading: LaunchesState()
object Failure: LaunchesState()

class LaunchesViewModel(
    database: LaunchDatabaseDao,
    service: SpaceXEndpoints
) : ViewModel() {

    private val call = service.getLaunches(null, null)
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

    private val _checked = MutableLiveData(false)
    val checked: LiveData<Boolean>
        get() = _checked

    private val _anyDisplayable = MutableLiveData(true)
    val anyDisplayable: LiveData<Boolean>
        get() = _anyDisplayable

    fun setChecked(isChecked: Boolean) {
        _checked.value = isChecked
    }

    fun onFailureEnded() {
        _failure.value = false
    }

    fun nothingToDisplay() {
        _anyDisplayable.value = false
    }

    private val dbWrapper: DBWrapper = DBWrapper(database, Handler())

    fun setDisplayable() {
        _progressBarVisible.value = true
        dbWrapper.getList {
            _displayableLaunches.value = it
            _progressBarVisible.value = false
        }
    }

    private fun addLaunch(launchItem: Launch) {
        _progressBarVisible.value = true
        dbWrapper.insert(launchItem) {
            _progressBarVisible.value = false
        }
    }

    fun getFromYear(year: Int) {
        _progressBarVisible.value = true
        dbWrapper.getFromYear(year) {
            _displayableLaunches.value = it
            _progressBarVisible.value = false
        }
    }

    fun getBySuccess(success: Boolean) {
        _progressBarVisible.value = true
        dbWrapper.getBySuccess(success) {
            _displayableLaunches.value = it
            _progressBarVisible.value = false
        }
    }

    fun getBySuccessFromYear(success: Boolean, year: Int) {
        _progressBarVisible.value = true
        dbWrapper.getBySuccessFromYear(success, year) {
            _displayableLaunches.value = it
            _progressBarVisible.value = false
        }
    }

    /** stáhne data ze SpaceXApi, uloží do databáze*/
    fun loadDataFromApi() {
        _progressBarVisible.value = true
        call.clone().enqueue(object : Callback<List<Launch>> {
            override fun onResponse(
                call: Call<List<Launch>>,
                response: Response<List<Launch>>
            ) {
                val responseListOfLaunches = response.body() ?: listOf()
                /** vytvoří Launch (položka v databázi) z výstupu z retrofitu a uloží do databáze */
                for (launchItem in responseListOfLaunches) {
                    addLaunch(launchItem)
                }
                setDisplayable()
            }

            override fun onFailure(call: Call<List<Launch>>, t: Throwable) {
                _message.value = t.message
                _failure.value = true
                _progressBarVisible.value = false
            }
        })

    }
}
