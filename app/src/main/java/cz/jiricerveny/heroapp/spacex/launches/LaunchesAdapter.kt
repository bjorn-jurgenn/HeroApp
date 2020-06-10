package cz.jiricerveny.heroapp.spacex.launches

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.jiricerveny.heroapp.R
import cz.jiricerveny.heroapp.databinding.LaunchItemLayoutBinding
import cz.jiricerveny.heroapp.spacex.LaunchesData
import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class LaunchesAdapter : RecyclerView.Adapter<LaunchesViewHolder>() {
    private val launches = mutableListOf<Launch>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.launch_item_layout, parent, false)
        return LaunchesViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaunchesViewHolder, position: Int) {
        return holder.bind(launches[position])
    }

    override fun getItemCount(): Int = launches.size

    fun update(list: List<Launch>) {
        launches.clear()
        launches.addAll(list)
        notifyDataSetChanged()
    }

}

// TODO Should be inner class of adapter as it is not likely to be shared with other adapter
class LaunchesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = LaunchItemLayoutBinding.bind(itemView)

    fun bind(launch: Launch) {
        binding.itemName.text = launch.missionName
        binding.itemFlight.text = launch.flight_number.toString()
        binding.itemYear.text = launch.launchYear.toString()
        when {
            launch.upcoming -> binding.itemStatus.text = "upcoming"
            launch.launchSuccess == true -> binding.itemStatus.text = "successful"
            else -> binding.itemStatus.text = "failure"
        }
        binding.itemRocketName.text = launch.rocketName
        binding.itemSiteName.text = launch.launchSite

        val zonedDateTime = ZonedDateTime.parse(launch.launchDateLocal)
            .withZoneSameInstant(ZoneId.of("Europe/Paris"))
        val localDateTime = zonedDateTime.format(DateTimeFormatter.ofPattern("d.M.Y HH:mm:ss z"))

        binding.itemLaunchDate.text = localDateTime
    }
}