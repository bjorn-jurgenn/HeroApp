package cz.jiricerveny.heroapp.spacex.launches

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabaseDao
import javax.sql.DataSource

class LaunchesViewModelFactory(
    private val dataSource: LaunchDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LaunchesViewModel::class.java)) {
            return LaunchesViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
