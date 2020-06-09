package cz.jiricerveny.heroapp.recycler

import androidx.recyclerview.widget.DiffUtil
import cz.jiricerveny.heroapp.recycler.ListItem

class ListDiffCallback(private val oldList: List<ListItem>, private val newList: List<ListItem>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].city == newList[newItemPosition].city
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }


}