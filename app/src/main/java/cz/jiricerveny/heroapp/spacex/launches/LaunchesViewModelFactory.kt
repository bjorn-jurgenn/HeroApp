package cz.jiricerveny.heroapp.spacex.launches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabaseDao
import retrofit2.Call

class LaunchesViewModelFactory(
    private val dataSource: LaunchDatabaseDao,
    private val service: Call<List<Launch>>
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LaunchesViewModel::class.java)) {
            return LaunchesViewModel(dataSource, service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
