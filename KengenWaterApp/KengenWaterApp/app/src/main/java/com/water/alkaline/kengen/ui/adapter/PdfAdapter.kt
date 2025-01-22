package com.water.alkaline.kengen.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gms.ads.AdLoader
import com.google.gms.ads.MyApp
import com.google.gms.ads.databinding.LayoutAdUniversalBinding
import com.water.alkaline.kengen.MyApplication
import com.water.alkaline.kengen.databinding.ItemImageBinding
import com.water.alkaline.kengen.model.main.Banner
import com.water.alkaline.kengen.model.main.Pdf
import com.water.alkaline.kengen.ui.listener.OnPdfListener
import com.water.alkaline.kengen.utils.Constant

class PdfAdapter(var activity: Context, var  arrayList: MutableList<Pdf>, var listener: OnPdfListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    var hashMap: HashMap<Int, Int> = HashMap()

    init {
        setAds(false)
    }

    class ViewHolder(var binding: ItemImageBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    private fun setAds(isAds: Boolean) {
        val PARTICLE_AD_DISPLAY_COUNT = MyApp.getAdModel().adsListViewCount

        if (PARTICLE_AD_DISPLAY_COUNT > 0 && MyApp.getAdModel().adsOnOff.equals(
                "Yes",
                ignoreCase = true
            )
        ) {
            arrayList.removeAll {
                it.id.equals(Constant.defaultId)
            }
            val tempArr = mutableListOf<Pdf>()

            if (arrayList.isNotEmpty()) {
                tempArr.add(Pdf().apply {
                    id = Constant.defaultId
                })
            }

            for (i in arrayList.indices) {
                if (arrayList.size > PARTICLE_AD_DISPLAY_COUNT) {
                    if (i != 0 && i % PARTICLE_AD_DISPLAY_COUNT == 0) {
                        tempArr.add(Pdf().apply {
                            id = Constant.defaultId
                        })
                    }
                    tempArr.add(arrayList[i])
                } else {
                    tempArr.add(arrayList[i])
                }
            }

            this.arrayList = tempArr
        }

        if (isAds) notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (arrayList[position].id.equals(Constant.defaultId)) Constant.AD_TYPE
        else Constant.STORE_TYPE
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constant.AD_TYPE) AdHolder(
            LayoutAdUniversalBinding.inflate(
                LayoutInflater.from(
                    activity
                ), parent, false
            )
        )
        else ViewHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(
                    activity
                ), parent, false
            )
        )
    }

    fun refreshAdapter(arrayList: MutableList<Pdf>) {
        this.arrayList = arrayList
        setAds(true)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AdHolder) {
            val adHolder = holder
            if (adHolder.binding.flAd.childCount <= 0) {
                AdLoader.getInstance().showNativeList(activity, adHolder.binding)
            }
        } else {
            val viewHolder = holder as ViewHolder
            Glide.with(activity).load(arrayList[position]!!.image)
                .placeholder(MyApplication.getPlaceHolder())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.binding.imgVideo)
            viewHolder.binding.txtVideoTitle.text = arrayList[position]!!.name
            viewHolder.binding.txtVideoTitle.isSelected = true
            viewHolder.itemView.setOnClickListener {
                listener.onItemClick(
                    holder.bindingAdapterPosition,
                    arrayList[holder.bindingAdapterPosition]
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}
