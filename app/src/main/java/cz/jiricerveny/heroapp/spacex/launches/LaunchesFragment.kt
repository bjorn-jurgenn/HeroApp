package cz.jiricerveny.heroapp.spacex.launches

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cz.jiricerveny.heroapp.HeroApp
import cz.jiricerveny.heroapp.MainActivity
import cz.jiricerveny.heroapp.NotificationReceiver
import cz.jiricerveny.heroapp.R
import cz.jiricerveny.heroapp.databinding.FragmentDialogLaunchesBinding
import cz.jiricerveny.heroapp.databinding.FragmentLaunchesBinding


class LaunchesFragment : Fragment() {
    private lateinit var binding: FragmentLaunchesBinding
    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(
            requireContext()
        )
    }
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
        val noDisplayableDialog = buildNoDisplayableDialog()

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
//            it?.let {
//                adapter.submitList(it)
//            }
//            if (it.isEmpty()) viewModel.nothingToDisplay()
            // TODO better
            if (it.isNullOrEmpty()) viewModel.nothingToDisplay() else adapter.submitList(it)
        })

        viewModel.progressBarVisible.observe(viewLifecycleOwner, Observer {
            // TODO does not look good, use parentheses for multiline if/else
            if (it) binding.launchesProgressBar.visibility = View.VISIBLE
            else {
                binding.launchesProgressBar.visibility = View.GONE
                sendOnChannel1()
            }
        })
        val app = requireContext().applicationContext as HeroApp
        // TODO this should not be necessary. You can simply observe the livedata from room
        app.newData.observe(viewLifecycleOwner, Observer {
            if (it) {
                adapter.notifyDataSetChanged()
                app.newData.value = false
            }
        })


        // TODO you are observing a lot of livedata, a better solution might be something like described in this article
        // https://zsmb.co/designing-and-working-with-single-view-states-on-android/
        viewModel.failure.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(activity, viewModel.message.value, Toast.LENGTH_LONG).show()
                viewModel.onFailureEnded()
                reloadDialog.show()
            }
        })
        viewModel.anyDisplayable.observe(viewLifecycleOwner, Observer {
            if (it == false) noDisplayableDialog.show()
        })

        return binding.root
    }

    /** podle zvolených checkboxů v dialogu vyfiltruje výsledky*/
    private fun dialogButtonAction(binding: FragmentDialogLaunchesBinding) {
//        var year: Int? = null
//        try {
//            val string = binding.launchesDialogYear.text.toString()
//            year = string.toInt()
//        } catch (e: NumberFormatException) {
//        }
        // TODO better
        val year =  binding.launchesDialogYear.text.toString().toIntOrNull()

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
        filterDialogBinding.launchesDialogSuccessful.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setChecked(isChecked)
        }

        viewModel.checked.observe(viewLifecycleOwner, Observer {
            filterDialogBinding.launchesDialogSuccessfulSwitch.isEnabled = it
        })

        val dialog = builder.create()
        filterDialogBinding.launchesDialogButton.setOnClickListener {
            dialogButtonAction(filterDialogBinding)
            dialog.dismiss()
            sendOnChannel2()
        }
        return dialog
    }

    private fun buildNoDisplayableDialog(): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("No displayable data")
            .setMessage("You can display all launches from database or get all data from SpaceX API")
            .setPositiveButton("Display all") { _, _ ->
                viewModel.setDisplayable()
            }
            .setNeutralButton("Get from API") { _, _ ->
                viewModel.loadDataFromApi()
            }
        return builder.create()
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

    private fun sendOnChannel1() {

        val activityIntent = Intent(requireContext(), MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(requireContext(), 0, activityIntent, 0)
        val broadcastIntent = Intent(requireContext(), NotificationReceiver::class.java)
        broadcastIntent.putExtra("toastMessage", "Loading completed")
        val actionIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            broadcastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(
            requireContext(),
            (requireActivity().application as HeroApp).CHANNEL_1_ID
        )
            .setSmallIcon(R.drawable.ic_one)
            .setContentTitle("Completed")
            .setContentText("Loading completed")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(contentIntent)
            .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, notification)
    }

    private fun sendOnChannel2() {
        val notification = NotificationCompat.Builder(
            requireContext(),
            (requireActivity().application as HeroApp).CHANNEL_2_ID
        )
            .setSmallIcon(R.drawable.ic_two)
            .setContentTitle("Settings changed")
            .setContentText("Now displaying filtered result")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        notificationManager.notify(2, notification)
    }
}