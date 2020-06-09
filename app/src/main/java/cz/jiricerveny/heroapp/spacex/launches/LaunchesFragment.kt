package cz.jiricerveny.heroapp.spacex.launches

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import cz.jiricerveny.heroapp.R
import cz.jiricerveny.heroapp.databinding.FragmentDialogLaunchesBinding
import cz.jiricerveny.heroapp.databinding.FragmentLaunchesBinding
import cz.jiricerveny.heroapp.spacex.LaunchesData
import cz.jiricerveny.heroapp.spacex.ServiceBuilder
import cz.jiricerveny.heroapp.spacex.SpaceXEndpoints
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NumberFormatException

class LaunchesFragment : Fragment() {
    private lateinit var binding: FragmentLaunchesBinding
    private lateinit var viewModel: LaunchesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLaunchesBinding.inflate(layoutInflater, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = LaunchDatabase.getInstance(application).launchDatabaseDao
        val viewModelFactory = LaunchesViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LaunchesViewModel::class.java)


        val fab = binding.fab
        val recyclerView = binding.launchesRecyclerView
        val adapter = LaunchesAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            this.adapter = adapter
            adapter.update(viewModel.launches.value ?: listOf())
            Log.i("XXX", viewModel.launches.value.toString())
        }

        getDataToAdapter(recyclerView.adapter as LaunchesAdapter, null, null)

        fab.setOnClickListener {
            fabClickAction(recyclerView.adapter as LaunchesAdapter)
        }
        return binding.root
    }

    private fun fabClickAction(adapter: LaunchesAdapter) {
        Toast.makeText(this.context, viewModel.launches.value.toString(), Toast.LENGTH_LONG).show()
        /* val builder = AlertDialog.Builder(requireContext())
         val layout = layoutInflater.inflate(R.layout.fragment_dialog_launches, null)
         builder.setView(layout)
         val dialogBinding = FragmentDialogLaunchesBinding.bind(layout)
         val dialog = builder.create()
         dialogBinding.launchesDialogButton.setOnClickListener {
             var launchYear: Int? = null
             var launchSuccess: Boolean? = null
             if (dialogBinding.launchesDialogSuccessful.isChecked)
                 launchSuccess = true
             if (dialogBinding.launchesDialogYearCheckbox.isChecked){
                 val string = dialogBinding.launchesDialogYear.text.toString()
                 Log.i("Launch_String",string)
                 try{
                     launchYear = string.toInt()
                 } catch (e: NumberFormatException){}
             }
             Log.i("Launch_Year_out",launchYear.toString())
             getDataToAdapter(adapter, launchYear,launchSuccess)
             dialog.dismiss()
         }
         dialog.show()*/
    }

    /** stáhne data ze SpaceXApi, uloží do databáze a hodí adapteru ke zobrazení*/
    private fun getDataToAdapter(
        adapter: LaunchesAdapter,
        launch_year: Int?,
        launch_success: Boolean?
    ) {
        val request = ServiceBuilder.buildService(SpaceXEndpoints::class.java)
        Log.i("getDataToAdapter", "number-$launch_year")
        val call = request.getLaunches(launch_year, launch_success)

        call.enqueue(object : Callback<List<LaunchesData>> {
            override fun onResponse(
                call: Call<List<LaunchesData>>,
                response: Response<List<LaunchesData>>
            ) {
                val responseListOfLaunches = response.body() ?: listOf()

                /** vytvoří Launch (položka v databázi) z výstupu z retrofitu a uloží do databáze */
                for (launchItem in responseListOfLaunches) {
                    val flightNumber = launchItem.flight_number.toInt()
                    val missionName = launchItem.mission_name
                    val upcoming = launchItem.upcoming
                    val launchYear = launchItem.launch_year
                    val launchDate = launchItem.launch_date_local
                    val rocket = launchItem.rocket.name
                    val success = launchItem.launch_success
                    val launchSite = launchItem.launch_site.name
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
                    Log.i("call", launch.flight_number.toString() + launch.launchSite)
                    viewModel.addLaunch(launch)
                }

                adapter.update(viewModel.launches.value ?: listOf())
                binding.launchesProgressBar.visibility = View.GONE
                Log.i("LaunchesFragment", responseListOfLaunches.toString())
            }

            override fun onFailure(call: Call<List<LaunchesData>>, t: Throwable) {
                Toast.makeText(activity, "${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}