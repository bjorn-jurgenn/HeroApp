package cz.jiricerveny.heroapp.spacex.landpads

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cz.jiricerveny.heroapp.R
import cz.jiricerveny.heroapp.databinding.FragmentLandpadsBinding
import cz.jiricerveny.heroapp.spacex.LandPadData
import cz.jiricerveny.heroapp.spacex.SpaceXEndpoints
import cz.jiricerveny.heroapp.spacex.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LandPadsFragment : Fragment() {
    private lateinit var binding: FragmentLandpadsBinding
    private lateinit var viewModel: LandPadViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLandpadsBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(LandPadViewModel::class.java)


        val recyclerView = binding.landpadsRecyclerView
        val adapter =
            LandpadAdapter(
                ::onItemClicked,
                ::onLocationClicked
            )
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            this.adapter = adapter
        }

        val call = ServiceBuilder.buildService(SpaceXEndpoints::class.java).getLandPads()

        call.enqueue(object : Callback<List<LandPadData>> {
            override fun onResponse(
                call: Call<List<LandPadData>>,
                response: Response<List<LandPadData>>
            ) {
                val listOfResults = response.body() ?: listOf()
                viewModel.update(listOfResults)
                adapter.update(viewModel.landPads.value!!)
                binding.landpadsprogressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<LandPadData>>, t: Throwable) {
                Log.d("MainActivity", t.message ?: "")
                Toast.makeText(activity, "${t.message}", Toast.LENGTH_LONG).show()
            }
        })

        return binding.root
    }

    private fun onItemClicked(landPad: LandPadData) {
        parentFragmentManager.beginTransaction().apply {
            val index = viewModel.landPads.value?.indexOf(landPad) ?: -1
            viewModel.index.value = index
            val detailFragment =
                LandPadDetailFragment()
            replace(R.id.main_container, detailFragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun onLocationClicked(lat: String, long: String) {
        val uriString = "geo:$lat,$long?q=$lat,$long&z=6"
        Log.i("LandPadsFragment", uriString)
        val uri = Uri.parse(uriString)
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(mapIntent)
    }
}
