package cz.jiricerveny.heroapp.spacex.launches

import android.app.AlertDialog
import android.content.DialogInterface
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

        viewModel.progressBarVisible.observe(viewLifecycleOwner, Observer {
            if (it) binding.launchesProgressBar.visibility = View.VISIBLE
            else binding.launchesProgressBar.visibility = View.GONE
        })

        val filterBuilder = AlertDialog.Builder(requireContext())
        val filterLayout = layoutInflater.inflate(R.layout.fragment_dialog_launches, null)
        filterBuilder.setView(filterLayout)
        val filterDialogBinding = FragmentDialogLaunchesBinding.bind(filterLayout)
        val dialog = filterBuilder.create()
        filterDialogBinding.launchesDialogButton.setOnClickListener {
            dialogButtonAction(filterDialogBinding)
            dialog.dismiss()
        }

        val fab = binding.fab
        fab.setOnClickListener {
            dialog.show()
        }

        val service =
            ServiceBuilder.buildService(SpaceXEndpoints::class.java)
        val call = service.getLaunches(null, null)

        val reloadBuilder = AlertDialog.Builder(requireContext())
        reloadBuilder.setMessage("Reload")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                viewModel.getDataFromApi(call)
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { _, _ ->
                Toast.makeText(requireContext(), "using offline data", Toast.LENGTH_SHORT).show()
            })
        val reloadDialog = reloadBuilder.create()

        viewModel.failure.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(activity, viewModel.message.value, Toast.LENGTH_LONG).show()
                viewModel.onFailureEnded()
                reloadDialog.show()
            }
        })

        viewModel.getDataFromApi(call)

        return binding.root
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


}