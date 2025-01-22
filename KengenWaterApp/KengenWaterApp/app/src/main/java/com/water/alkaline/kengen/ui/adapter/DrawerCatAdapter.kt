package com.water.alkaline.kengen.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.water.alkaline.kengen.databinding.ItemDrawerCategoryBinding
import com.water.alkaline.kengen.model.main.Category
import com.water.alkaline.kengen.ui.listener.OnDrawerCatListener

class DrawerCatAdapter(
    var activity: Activity,
    var arrayList: List<Category>,
    var listener: OnDrawerCatListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    inner class ViewHolder(var binding: ItemDrawerCategoryBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemDrawerCategoryBinding.inflate(
                LayoutInflater.from(
                    activity
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        viewHolder.binding.txtCategory.text = arrayList[position].name
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}
