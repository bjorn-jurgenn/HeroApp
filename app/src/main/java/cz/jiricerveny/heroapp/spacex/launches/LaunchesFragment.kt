package cz.jiricerveny.heroapp.spacex.launches

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
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
        }
        viewModel.setDisplayable()
        viewModel.displayableLaunches.observe(viewLifecycleOwner, Observer {
            adapter.update(it ?: listOf())
        })
        getDataFromApi(null, null)
        fab.setOnClickListener {
            fabClickAction()
        }
        return binding.root
    }


    private fun fabClickAction() {
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

    private fun dialogButtonAction(binding: FragmentDialogLaunchesBinding) {
        var launchYear: Int? = null
        var launchSuccess: Boolean? = null
        binding.launchesDialogSuccessful.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.launchesDialogSuccessfulSwitch.isEnabled = isChecked
        }

        if (binding.launchesDialogSuccessful.isChecked) {
            launchSuccess = binding.launchesDialogSuccessfulSwitch.isChecked
        }

        if (binding.launchesDialogYearCheckbox.isChecked) {
            val string = binding.launchesDialogYear.text.toString()
            Log.i("Launch_String", string)
            try {
                launchYear = string.toInt()
            } catch (e: NumberFormatException) {
            }
        }

        if (launchSuccess != null) {
            if (launchYear != null) {
                viewModel.getBySuccessFromYear(launchSuccess, launchYear)
            } else viewModel.getBySuccess(launchSuccess)
        } else if (launchYear != null) {
            viewModel.getFromYear(launchYear)
        } else viewModel.setDisplayable()


    }

    /** stáhne data ze SpaceXApi, uloží do databáze*/
    private fun getDataFromApi(
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
                binding.launchesProgressBar.visibility = View.GONE
                Log.i("LaunchesFragment", responseListOfLaunches.toString())
            }
            override fun onFailure(call: Call<List<LaunchesData>>, t: Throwable) {
                Toast.makeText(activity, "${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}