
package cz.jiricerveny.heroapp.spacex.launches

import android.os.Handler
import android.util.Log
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

sealed class LaunchesState
data class Loaded(val list: LiveData<List<Launch>>) : LaunchesState()
object Inserted : LaunchesState()
object Loading : LaunchesState()
object Failure : LaunchesState()
object Nothing : LaunchesState()

class LaunchesViewModel(
    val database: LaunchDatabaseDao,
    private val service: SpaceXEndpoints
) : ViewModel() {
    private val _state = MutableLiveData<LaunchesState>(Inserted)
    val state: LiveData<LaunchesState>
        get() = _state

    private val _buttonChecked = MutableLiveData(false)
    val buttonChecked: LiveData<Boolean>
        get() = _buttonChecked


    fun setChecked(isChecked: Boolean) {
        _buttonChecked.value = isChecked
    }

    fun setNothing() {
        _state.value = Nothing
    }

    private val dbWrapper: DBWrapper = DBWrapper(database, Handler())

    fun displayAll() {
        _state.value = Loading
        dbWrapper.getList {
            Log.i("Launch", "livedata value: ${it.value}")
            //if (it.value.isNullOrEmpty()) _state.value = Nothing
            _state.value = Loaded(it)
        }
    }

    private fun addLaunch(launchItem: Launch) {
        _state.value = Loading
        dbWrapper.insert(launchItem)
    }

    fun getFromYear(year: Int) {
        _state.value = Loading
        dbWrapper.getFromYear(year) {
            Log.i("Launch", "livedata value: ${it.value}")
            //if (it.value.isNullOrEmpty()) _state.value = Nothing
            _state.value = Loaded(it)
        }
    }

    fun getBySuccess(success: Boolean) {
        _state.value = Loading
        dbWrapper.getBySuccess(success) {
            Log.i("Launch", "livedata value: ${it.value}")
            //if (it.value.isNullOrEmpty()) _state.value = Nothing
            _state.value = Loaded(it)
        }
    }

    fun getBySuccessFromYear(success: Boolean, year: Int) {
        _state.value = Loading
        dbWrapper.getBySuccessFromYear(success, year) {
            Log.i("Launch", "livedata value: ${it.value}")
            //if (it.value.isNullOrEmpty()) _state.value = Nothing
            _state.value = Loaded(it)
        }
    }

    /** stáhne data ze SpaceXApi, uloží do databáze*/
    fun loadDataFromApi() {
        _state.value = Loading
        service.getLaunches(null, null).enqueue(object : Callback<List<Launch>> {
            override fun onResponse(
                call: Call<List<Launch>>,
                response: Response<List<Launch>>
            ) {
                val responseListOfLaunches = response.body() ?: listOf()
                /** vytvoří Launch (položka v databázi) z výstupu z retrofitu a uloží do databáze */
                for (launchItem in responseListOfLaunches) {
                    addLaunch(launchItem)
                }
                _state.value = Inserted
            }

            override fun onFailure(call: Call<List<Launch>>, t: Throwable) {
                _state.value = Failure
            }
        })
    }
}
