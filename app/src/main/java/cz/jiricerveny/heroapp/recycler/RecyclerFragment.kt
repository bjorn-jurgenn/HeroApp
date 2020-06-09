package cz.jiricerveny.heroapp.recycler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cz.jiricerveny.heroapp.R
import cz.jiricerveny.heroapp.databinding.FragmentDialogTwoEditTextBinding
import cz.jiricerveny.heroapp.databinding.FragmentRecyclerInnerBinding


class RecyclerFragment : Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentRecyclerInnerBinding
    private lateinit var viewModel: ListViewModel

    private val new = listOf(
        ListItem("Moravia", "Brno"),
        ListItem("Czech Republic", "Plzen"),
        ListItem("Germany", "Berlin"),
        ListItem("France", "Lyon"),
        ListItem("Singapore", "Singapore"),
        ListItem("Great Britain", "London"),
        ListItem("Great Britain", "Manchester"),
        ListItem("Great Britain", "Edinburgh"),
        ListItem("Hungary", "Pest"),
        ListItem("Serbia", "Belgrade"),
        ListItem("Slovenia", "Maribor"),
        ListItem("Slovenia", "Ljubljana")
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("RecyclerFragment", "onCreateView")
        binding = FragmentRecyclerInnerBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ListViewModel::class.java)

        val adapter = ListAdapter(this)
        binding.apply {
            listRecyclerView.layoutManager = LinearLayoutManager(activity)
            listRecyclerView.adapter = adapter
        }

        viewModel.visitedCities.observe(viewLifecycleOwner, Observer { newList ->
            adapter.setData(newList)
        })
        adapter.setData(viewModel.visitedCities.value ?: listOf())

        val fab = binding.fab
        fab.setOnClickListener {
            fabClickAction(adapter)
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onItemClicked(item: ListItem) {
        parentFragmentManager.beginTransaction().apply {
            val index = viewModel.visitedCities.value?.indexOf(item) ?: -1
            val detailFragment = RecyclerDetailFragment(index)
            replace(R.id.main_container, detailFragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun onButtonClicked(item: ListItem) {
        val message =
            "${item.city} (${item.country})"
        viewModel.deleteItem(item)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun fabClickAction(adapter: ListAdapter) {
        val builder = AlertDialog.Builder(requireContext())
        val layout = layoutInflater.inflate(R.layout.fragment_dialog_two_edit_text, null)
        builder.setView(layout)
        val dialogBinding = FragmentDialogTwoEditTextBinding.bind(layout)
        val dialog = builder.create()
        dialogBinding.buttonTwoEditText.setOnClickListener {
            val item = ListItem(
                dialogBinding.editTextSecond.text.toString(),
                dialogBinding.editTextFirst.text.toString()
            )
            viewModel.addItem(item)
            dialog.dismiss()
        }
        dialog.show()
    }
}
