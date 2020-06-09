package cz.jiricerveny.heroapp.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class ListAdapter(private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ListViewHolder>() {
    private val items = mutableListOf<ListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        Log.i("ListAdapter", "onCreateViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        return ListViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        Log.i("ListAdapter", "onBindViewHolder position $position")
        val item: ListItem = items[position]
        holder.bind(item, itemClickListener)
    }

    override fun getItemCount(): Int = items.size

    fun setData(newList: List<ListItem>) {
        val diffCallback = ListDiffCallback(items, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}

interface OnItemClickListener {
    fun onItemClicked(item: ListItem)
    fun onButtonClicked(item: ListItem)
}
