package cz.jiricerveny.heroapp.spacex.launches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.jiricerveny.heroapp.spacex.SpaceXEndpoints
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabaseDao

class LaunchesViewModelFactory(
    private val dataSource: LaunchDatabaseDao,
    private val service: SpaceXEndpoints
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LaunchesViewModel::class.java)) {
            return LaunchesViewModel(dataSource, service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
