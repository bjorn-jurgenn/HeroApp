package cz.jiricerveny.heroapp.spacex.landpads

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.jiricerveny.heroapp.R
import cz.jiricerveny.heroapp.databinding.LandpadItemLayoutBinding
import cz.jiricerveny.heroapp.spacex.LandPadData

class LandpadAdapter(
    private val itemListener: (landsPad: LandPadData) -> Unit,
    private val locationListener: (lat: String, lon: String) -> Unit
) :
    RecyclerView.Adapter<LandpadAdapter.LandPadsViewHolder>() {
    private val landPads = mutableListOf<LandPadData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandPadsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.landpad_item_layout, parent, false)
        return LandPadsViewHolder(view)
    }

    override fun getItemCount(): Int = landPads.size

    override fun onBindViewHolder(holder: LandPadsViewHolder, position: Int) {
        holder.bind(landPads[position])
    }

    fun update(list: List<LandPadData>) {
        landPads.clear()
        landPads.addAll(list)
        notifyDataSetChanged()
    }

    inner class LandPadsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = LandpadItemLayoutBinding.bind(itemView)

        fun bind(landPad: LandPadData) {

            binding.apply {
                itemName.text = landPad.name
                itemId.text = landPad.id
                itemStatus.text = landPad.status
                itemSuccessful.text = landPad.successfulLandings.toString()
                itemLandings.text = landPad.totalLandings.toString()
                if (landPad.totalLandings == 0) itemPercentage.text = "0"
                else itemPercentage.text =
                    ((landPad.totalLandings * 100) / landPad.totalLandings).toString()
                itemLocation.text = landPad.location.name
                itemLocation.setOnClickListener {
                    locationListener(
                        landPad.location.latitude, landPad.location.longitude
                    )
                }
                itemRegion.text = landPad.location.region
                itemLocation.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                itemLayout.setOnClickListener { itemListener(landPad) }
            }
        }
    }
}

