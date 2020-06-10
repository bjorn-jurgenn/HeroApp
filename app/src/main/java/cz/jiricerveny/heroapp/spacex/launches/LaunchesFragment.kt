package cz.jiricerveny.heroapp.spacex.launches

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cz.jiricerveny.heroapp.R
import cz.jiricerveny.heroapp.databinding.FragmentDialogLaunchesBinding
import cz.jiricerveny.heroapp.databinding.FragmentLaunchesBinding
import cz.jiricerveny.heroapp.spacex.LaunchesData
import cz.jiricerveny.heroapp.spacex.ServiceBuilder
import cz.jiricerveny.heroapp.spacex.SpaceXEndpoints
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import cz.jiricerveny.heroapp.spacex.launches.database.LaunchDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.coroutineContext


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

        val recyclerView = binding.launchesRecyclerView
        val adapter = LaunchesAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            this.adapter = adapter
        }

        viewModel.setDisplayable()
        viewModel.displayableLaunches.observe(viewLifecycleOwner, Observer {
            adapter.update(it ?: listOf())
        })

        val fab = binding.fab
        fab.setOnClickListener {
            fabClickAction()
        }

        getDataFromApi(null, null)

        return binding.root
    }


    private fun fabClickAction() {
        // TODO A slight performance improvement would be to create the dialog only once and just show it on button click
        val builder = AlertDialog.Builder(requireContext())
        val layout = layoutInflater.inflate(R.layout.fragment_dialog_launches, null)
        builder.setView(layout)
        val dialogBinding = FragmentDialogLaunchesBinding.bind(layout)
        val dialog = builder.create()
        dialogBinding.launchesDialogButton.setOnClickListener {
            dialogButtonAction(dialogBinding)
            dialog.dismiss()
        }
        dialog.show()
    }

    /** podle zvolených checkboxů v dialogu vyfiltruje výsledky*/
    private fun dialogButtonAction(binding: FragmentDialogLaunchesBinding) {
        var year: Int? = null
        try {
            val string = binding.launchesDialogYear.text.toString()
            year = string.toInt()
        } catch (e: NumberFormatException) {
        }
        val successful = binding.launchesDialogSuccessfulSwitch.isChecked

        val launchYearChecked = binding.launchesDialogYearCheckbox.isChecked
        val successChecked = binding.launchesDialogSuccessful.isChecked
        when {
            launchYearChecked && year != null && successChecked -> viewModel.getBySuccessFromYear(
                successful,
                year
            )
            launchYearChecked && year != null -> viewModel.getFromYear(year)
            successChecked -> viewModel.getBySuccess(successful)
            else -> viewModel.setDisplayable()
        }

    }

    /** stáhne data ze SpaceXApi, uloží do databáze*/
    // TODO this should be in the viewmodel
    private fun getDataFromApi(launch_year: Int?, launch_success: Boolean?) {
        val request =
            ServiceBuilder.buildService(SpaceXEndpoints::class.java) // TODO We definitely dont want to build the service every time we make an api call, also rename request to service
        val call = request.getLaunches(launch_year, launch_success)
        call.enqueue(object : Callback<List<LaunchesData>> {
            override fun onResponse(
                call: Call<List<LaunchesData>>,
                response: Response<List<LaunchesData>>
            ) {
                val responseListOfLaunches = response.body() ?: listOf()
                /** vytvoří Launch (položka v databázi) z výstupu z retrofitu a uloží do databáze */
                for (launchItem in responseListOfLaunches) {
                    // TODO Couldn't launchItem and and Launch be the same data class? You can use annotations if you don't want to save everything.
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
                    viewModel.addLaunch(launch)
                }

                binding.launchesProgressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<LaunchesData>>, t: Throwable) {
                Toast.makeText(activity, "${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}