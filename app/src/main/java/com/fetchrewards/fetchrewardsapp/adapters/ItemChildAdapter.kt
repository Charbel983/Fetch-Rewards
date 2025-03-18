package com.fetchrewards.fetchrewardsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fetchrewards.fetchrewardsapp.databinding.ItemTextBinding
import com.fetchrewards.fetchrewardsapp.models.Item

class ItemChildAdapter(private val context : Context, private val items : List<Item>) : RecyclerView.Adapter<ItemChildAdapter.Holder>() {

    class Holder(inflater : ItemTextBinding) : RecyclerView.ViewHolder(inflater.root) {
        val itemText = inflater.itemText
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : Holder {
        val view = ItemTextBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(view)
    }

    override fun getItemCount() : Int {
        return items.size
    }

    override fun onBindViewHolder(holder : Holder, position : Int) {
        val item = items[position]
        holder.itemText.text = item.name
    }
}