package cz.jiricerveny.heroapp.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.jiricerveny.heroapp.R
import cz.jiricerveny.heroapp.databinding.ListItemBinding

class ListViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)) {
    val binding = ListItemBinding.bind(itemView)
    private var mCountryView = binding.listDescription
    private var mCityView = binding.listTitle


    fun bind(item: ListItem, clickListener: OnItemClickListener) {
        mCityView.text = item.city
        mCountryView.text = item.country
        binding.layoutListItem.setOnClickListener { clickListener.onItemClicked(item) }
        binding.listButton.setOnClickListener { clickListener.onButtonClicked(item) }
    }

}

