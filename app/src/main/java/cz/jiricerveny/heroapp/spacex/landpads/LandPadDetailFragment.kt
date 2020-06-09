package cz.jiricerveny.heroapp.spacex.landpads

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import cz.jiricerveny.heroapp.databinding.LandpadDetailLayoutBinding


class LandPadDetailFragment(private val index: Int) : Fragment() {
    private lateinit var binding: LandpadDetailLayoutBinding
    private lateinit var viewModel: LandPadViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LandpadDetailLayoutBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(LandPadViewModel::class.java)

        binding.itemName.text = viewModel.landPads.value?.get(index)?.full_name ?: ""
        binding.itemId.text = viewModel.landPads.value?.get(index)?.id ?: ""
        binding.itemStatus.text = viewModel.landPads.value?.get(index)?.status ?: ""
        val successful = viewModel.landPads.value?.get(index)?.successful_landings ?: 0
        val total = viewModel.landPads.value?.get(index)?.attempted_landings ?: 0
        binding.itemSuccessful.text = successful.toString()
        binding.itemLandings.text = total.toString()
        if (successful == 0) binding.itemPercentage.text = "0"
        else binding.itemPercentage.text = ((successful * 100) / total).toString()
        binding.itemLocation.text = viewModel.landPads.value?.get(index)?.location?.name ?: ""
        binding.itemRegion.text = viewModel.landPads.value?.get(index)?.location?.region ?: ""
        binding.itemDetails.text = viewModel.landPads.value?.get(index)?.details ?: ""
        binding.itemButton.setOnClickListener { parentFragmentManager.popBackStack() }
        return binding.root
    }
}
