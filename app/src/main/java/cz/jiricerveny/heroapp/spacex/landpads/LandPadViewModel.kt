package cz.jiricerveny.heroapp.spacex.landpads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.jiricerveny.heroapp.spacex.LandPadData

class LandPadViewModel : ViewModel() {
    private val _landPads = MutableLiveData<List<LandPadData>>()
    val landPads: LiveData<List<LandPadData>>
        get() = _landPads

    fun update(list: List<LandPadData>) {
        _landPads.value = list
    }
}