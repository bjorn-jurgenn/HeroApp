
package cz.jiricerveny.heroapp.spacex.launches

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
class LaunchesViewModel(
    private val database: LaunchDatabaseDao,
    private val call: Call<List<Launch>>
) : ViewModel() {
    private val mainHandler = Handler()

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

    fun setChecked(isChecked: Boolean) {
        _checked.value = isChecked
    }

    fun onFailureEnded() {
        _failure.value = false
    }

    fun setDisplayable() {
        val runnable = Runnable {
            val list = database.getList()
            mainHandler.post {
                _displayableLaunches.value = list
            }
        }
        Thread(runnable).start()
    }

    private fun addLaunch(launchItem: Launch) {
        val runnable = Runnable {
            database.insert(launchItem)
        }
        Thread(runnable).start()
    }

    fun getFromYear(year: Int) {
        val runnable = Runnable {
            val list = database.getFromYear(year)
            mainHandler.post {
                _displayableLaunches.value = list
            }
        }
        Thread(runnable).start()
    }

    fun getBySuccess(success: Boolean) {
        val runnable = Runnable {
            val list = database.getBySuccess(success)
            mainHandler.post {
                _displayableLaunches.value = list
            }
        }
        Thread(runnable).start()
    }

    fun getBySuccessFromYear(success: Boolean, year: Int) {
        val runnable = Runnable {
            val list = database.getBySuccessFromYear(success, year)
            mainHandler.post {
                _displayableLaunches.value = list
            }
        }
        Thread(runnable).start()
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
                _progressBarVisible.value = false
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
