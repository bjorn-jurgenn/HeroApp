package cz.jiricerveny.heroapp.spacex.landpads

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cz.jiricerveny.heroapp.databinding.LandpadDetailLayoutBinding

class LandPadDetailFragment : Fragment() {
    private lateinit var binding: LandpadDetailLayoutBinding
    private lateinit var viewModel: LandPadViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LandpadDetailLayoutBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(LandPadViewModel::class.java)
        val index = viewModel.index.value ?: -1

        if (index != -1) {
            viewModel.landPads.observe(viewLifecycleOwner, Observer { landpads ->
                binding.apply {
                    itemName.text = landpads[index].name
                    itemId.text = landpads[index].id
                    itemStatus.text = landpads[index].status
                    val successful = landpads[index].successfulLandings
                    val total = landpads[index].totalLandings
                    itemSuccessful.text = successful.toString()
                    itemLandings.text = total.toString()
                    if (successful == 0) itemPercentage.text = "0"
                    else itemPercentage.text = ((successful * 100) / total).toString()
                    itemLocation.text = landpads[index].location.name
                    itemRegion.text = landpads[index].location.region
                    itemDetails.text = landpads[index].details
                    itemButton.setOnClickListener { parentFragmentManager.popBackStack() }
                }

            })
        }
        return binding.root
    }
}
