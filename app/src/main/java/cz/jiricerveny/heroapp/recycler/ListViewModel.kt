package cz.jiricerveny.heroapp.recycler

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListViewModel : ViewModel() {

    private val _visitedCities = MutableLiveData<List<ListItem>>()
    val visitedCities: LiveData<List<ListItem>>
        get() {
            Log.i("ListViewModel", "get visited cities")
            return _visitedCities
        }

    init {
        Log.i("ListViewModel", "ViewModel created")
        resetList()
    }

    private fun resetList() {
        Log.i("ListViewModel", "resetList")
        _visitedCities.value = listOf<ListItem>(
            ListItem("Austria", "Vienna"),
            ListItem("Czech Republic", "Prague"),
            ListItem("Czech Republic", "Brno"),
            ListItem("Czech Republic", "Plzen"),
            ListItem("Germany", "Berlin"),
            ListItem("France", "Lyon"),
            ListItem("France", "Marseilles"),
            ListItem("France", "Bordeaux"),
            ListItem("Italy", "Rome"),
            ListItem("Italy", "Milano"),
            ListItem("Italy", "Pisa"),
            ListItem("Belgium", "Brussels"),
            ListItem("Belgium", "Bruggy"),
            ListItem("Belgium", "Gent"),
            ListItem("Great Britain", "London"),
            ListItem("Great Britain", "Oxford"),
            ListItem("Great Britain", "Edinburgh"),
            ListItem("Hungary", "Budapest"),
            ListItem("Serbia", "Belgrade"),
            ListItem("Slovenia", "Maribor"),
            ListItem("Slovenia", "Ljubljana")
        )
    }

    fun deleteItem(item: ListItem) {
        val newList = createMutableCopy()
        newList.remove(item)
        _visitedCities.value = newList
    }

    fun addItem(item: ListItem) {
        val newList = createMutableCopy()
        newList.add(0, item)
        _visitedCities.value = newList
    }

    fun addNote(index: Int, note: String) {
        _visitedCities.value?.get(index)?.note = note
    }

    private fun createMutableCopy(): MutableList<ListItem> {
        return _visitedCities.value?.toMutableList() ?: mutableListOf()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("ListViewModel", "ListViewModel destroyed!")
    }
}