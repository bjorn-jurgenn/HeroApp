package cz.jiricerveny.heroapp.spacex.landpads

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.jiricerveny.heroapp.R
import cz.jiricerveny.heroapp.databinding.LandpadItemLayoutBinding
import cz.jiricerveny.heroapp.spacex.LandPadData

class LandpadAdapter(private val listener: OnLandPadClickListener) :
    RecyclerView.Adapter<LandPadsViewHolder>() {
    private val landPads = mutableListOf<LandPadData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandPadsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.landpad_item_layout, parent, false)
        return LandPadsViewHolder(view)
    }

    override fun getItemCount(): Int = landPads.size

    override fun onBindViewHolder(holder: LandPadsViewHolder, position: Int) {
        holder.bind(landPads[position], listener)
    }

    fun update(list: List<LandPadData>) {
        landPads.clear()
        landPads.addAll(list)
        notifyDataSetChanged()
    }
}


// TODO make inner class of launch adapter
class LandPadsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = LandpadItemLayoutBinding.bind(itemView)

    fun bind(landPad: LandPadData, clickListener: OnLandPadClickListener) {

        // TODO binding apply
        binding.itemName.text = landPad.full_name
        binding.itemId.text = landPad.id
        binding.itemStatus.text = landPad.status
        binding.itemSuccessful.text = landPad.successful_landings.toString()
        binding.itemLandings.text = landPad.attempted_landings.toString()
        if (landPad.attempted_landings == 0) binding.itemPercentage.text = "0"
        else binding.itemPercentage.text =
            ((landPad.successful_landings * 100) / landPad.attempted_landings).toString()
        binding.itemLocation.text = landPad.location.name
        binding.itemLocation.setOnClickListener {
            clickListener.onLocationClicked(
                landPad.location.latitude, landPad.location.longitude
            )
        }
        binding.itemRegion.text = landPad.location.region
        binding.itemLocation.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.itemLayout.setOnClickListener { clickListener.onItemClicked(landPad) }
//        binding.itemDetails.text = landPad.details
    }
}

interface OnLandPadClickListener {
    fun onItemClicked(landPad: LandPadData)
    fun onLocationClicked(lat: String, long: String)
}