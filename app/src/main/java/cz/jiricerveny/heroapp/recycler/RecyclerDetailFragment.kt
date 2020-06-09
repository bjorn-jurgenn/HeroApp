package cz.jiricerveny.heroapp.recycler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cz.jiricerveny.heroapp.databinding.FragmentRecyclerDetailBinding


class RecyclerDetailFragment(private val index: Int) : Fragment() {
    private lateinit var binding: FragmentRecyclerDetailBinding
    private lateinit var viewModel: ListViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("RecyclerFragment", "onCreateView")
        binding = FragmentRecyclerDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ListViewModel::class.java)

        binding.detailTitle.text = viewModel.visitedCities.value?.get(index)?.city ?: ""
        binding.detailDescription.text = viewModel.visitedCities.value?.get(index)?.country ?: ""
        binding.detailNote.text = viewModel.visitedCities.value?.get(index)?.note ?: ""
        binding.detailButton.setOnClickListener {
            viewModel.addNote(index, binding.detailEditText.text.toString())
            parentFragmentManager.popBackStack()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

}
