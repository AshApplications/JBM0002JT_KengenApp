package com.water.alkaline.kengen.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.water.alkaline.kengen.MyApplication
import com.water.alkaline.kengen.databinding.ItemVpBinding
import com.water.alkaline.kengen.model.main.Banner
import com.water.alkaline.kengen.ui.listener.OnBannerListerner


class VpImageAdapter(var activity: Activity,var  arrayList: List<Banner>, var listener: OnBannerListerner) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    inner class ViewHolder(var binding: ItemVpBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemVpBinding.inflate(
                LayoutInflater.from(
                    activity
                ), parent, false
            )
        )
    }

    fun refreshAdapter(arrayList: List<Banner>) {
        this.arrayList = arrayList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder

        Glide.with(activity).load(arrayList[position].url)
            .placeholder(MyApplication.getPlaceHolder())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(viewHolder.binding.ivImage)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}

