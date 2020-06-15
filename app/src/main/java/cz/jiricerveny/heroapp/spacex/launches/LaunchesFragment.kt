package cz.jiricerveny.heroapp.spacex.launches

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cz.jiricerveny.heroapp.HeroApp
import cz.jiricerveny.heroapp.R
import cz.jiricerveny.heroapp.databinding.FragmentDialogLaunchesBinding
import cz.jiricerveny.heroapp.databinding.FragmentLaunchesBinding


class LaunchesFragment : Fragment() {
    private lateinit var binding: FragmentLaunchesBinding
    private val viewModel: LaunchesViewModel by viewModels {
        LaunchesViewModelFactory(
            (requireActivity().application as HeroApp).db.launchDatabaseDao,
            (requireActivity().application as HeroApp).service
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLaunchesBinding.inflate(layoutInflater, container, false)
        val filterDialog = buildFilterDialog()
        val reloadDialog = buildReloadDialog()

        val recyclerView = binding.launchesRecyclerView
        val adapter = LaunchesAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            this.adapter = adapter
        }

        val fab = binding.fab
        fab.setOnClickListener {
            filterDialog.show()
        }

        viewModel.setDisplayable()
        viewModel.displayableLaunches.observe(viewLifecycleOwner, Observer {
            adapter.update(it ?: listOf())
        })

        viewModel.progressBarVisible.observe(viewLifecycleOwner, Observer {
            if (it) binding.launchesProgressBar.visibility = View.VISIBLE
            else binding.launchesProgressBar.visibility = View.GONE
        })

        viewModel.failure.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(activity, viewModel.message.value, Toast.LENGTH_LONG).show()
                viewModel.onFailureEnded()
                reloadDialog.show()
            }
        })
        viewModel.loadDataFromApi()

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

    private fun buildFilterDialog(): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        val layout = layoutInflater.inflate(R.layout.fragment_dialog_launches, null)
        builder.setView(layout)
        val filterDialogBinding = FragmentDialogLaunchesBinding.bind(layout)
        val dialog = builder.create()
        filterDialogBinding.launchesDialogButton.setOnClickListener {
            dialogButtonAction(filterDialogBinding)
            dialog.dismiss()
        }
        return dialog
    }

    private fun buildReloadDialog(): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Reload")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.loadDataFromApi()
            }
            .setNegativeButton("No") { _, _ ->
                Toast.makeText(requireContext(), "using offline data", Toast.LENGTH_SHORT).show()
            }
        return builder.create()
    }


}