package com.fetchrewards.fetchrewardsapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fetchrewards.fetchrewardsapp.databinding.ItemCardBinding
import com.fetchrewards.fetchrewardsapp.models.Item
import com.google.android.material.button.MaterialButton

class ItemParentAdapter(private val context : Context, private val items : List<List<Item>>) : RecyclerView.Adapter<ItemParentAdapter.Holder>() {

    class Holder(inflater : ItemCardBinding) : RecyclerView.ViewHolder(inflater.root) {
        val rootView = inflater.root
        val itemRecyclerView = inflater.itemRecyclerView
        val itemShowMore = inflater.itemShowMore
    }

    //Note: I could've added a text to display the list ID of each card but that seemed like irrelevant information for the user to see

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : Holder {
        val view = ItemCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(view)
    }

    override fun getItemCount() : Int {
        return items.size
    }

    override fun onBindViewHolder(holder : Holder, position : Int) {
        val itemsList = items[position]
        holder.itemRecyclerView.layoutManager = LinearLayoutManager(holder.rootView.context, LinearLayoutManager.VERTICAL, false)
        holder.itemRecyclerView.adapter = ItemChildAdapter(holder.rootView.context, itemsList.sortedBy { it.name })
        var isExpended = false
        holder.itemShowMore.setOnClickListener {
            toggleRecyclerView(isExpended, holder.itemRecyclerView, holder.itemShowMore)
            isExpended = !isExpended
        }
    }

    @SuppressLint("SetTextI18n")
    private fun toggleRecyclerView(isExpended : Boolean, recyclerView : RecyclerView, button : MaterialButton) {
        if(!isExpended) {
            val layoutParams = recyclerView.layoutParams
            layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT
            recyclerView.layoutParams = layoutParams
            button.text = "Show Less"
        } else {
            val heightInPixels = context.resources.displayMetrics.density * 300
            val layoutParams = recyclerView.layoutParams
            layoutParams.height = heightInPixels.toInt()
            recyclerView.layoutParams = layoutParams
            button.text = "Show More"
        }
    }

}