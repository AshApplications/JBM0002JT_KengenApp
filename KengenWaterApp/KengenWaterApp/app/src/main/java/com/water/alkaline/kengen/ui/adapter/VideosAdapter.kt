package com.water.alkaline.kengen.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gms.ads.AdLoader
import com.google.gms.ads.MyApp
import com.google.gms.ads.databinding.LayoutAdUniversalBinding
import com.water.alkaline.kengen.MyApplication
import com.water.alkaline.kengen.databinding.ItemVideoBinding
import com.water.alkaline.kengen.databinding.LayoutProgressBinding
import com.water.alkaline.kengen.model.SaveEntity
import com.water.alkaline.kengen.ui.activity.PlayerActivity
import com.water.alkaline.kengen.ui.listener.OnLoadMoreListener
import com.water.alkaline.kengen.ui.listener.OnVideoListener
import com.water.alkaline.kengen.utils.Constant


class VideosAdapter(
    var activity: Context,
    var arrayList: MutableList<SaveEntity>,
    var recyclerView: RecyclerView?,
    var listener: OnVideoListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var isLoading = false

    init {
        if (activity !is PlayerActivity) {
            setAds(false)
        }
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)) {
                    if (!isLoading) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener!!.onLoadMore()
                        }
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun setAds(isAds: Boolean) {
        val PARTICLE_AD_DISPLAY_COUNT = MyApp.getAdModel().adsListViewCount
        if (PARTICLE_AD_DISPLAY_COUNT > 0 && MyApp.getAdModel().adsOnOff.equals(
                "Yes",
                ignoreCase = true
            )
        ) {
            arrayList.removeAll {
                it.videoId.equals(Constant.defaultId)
            }
            val tempArr = mutableListOf<SaveEntity>()

            if (arrayList.size > 0) {
                tempArr.add(SaveEntity().apply {
                    videoId = Constant.defaultId
                })
            }
            for (i in arrayList.indices) {
                if (arrayList.size > PARTICLE_AD_DISPLAY_COUNT) {
                    if (i != 0 && i % PARTICLE_AD_DISPLAY_COUNT == 0) {
                        tempArr.add(SaveEntity().apply {
                            videoId = Constant.defaultId
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

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener?) {
        this.onLoadMoreListener = mOnLoadMoreListener
    }


    inner class ViewHolder(var binding: ItemVideoBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun getItemViewType(position: Int): Int {
        return if (arrayList[position].videoId == Constant.defaultId) Constant.AD_TYPE
        else if (arrayList[position].videoId.equals(
                "99999",
                ignoreCase = true
            )
        ) Constant.LOADING
        else Constant.STORE_TYPE
    }


    inner class LoadingView(private var progressBinding: LayoutProgressBinding) : RecyclerView.ViewHolder(
        progressBinding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constant.AD_TYPE) AdHolder(
            LayoutAdUniversalBinding.inflate(
                LayoutInflater.from(
                    activity
                ), parent, false
            )
        )
        else if (viewType == Constant.STORE_TYPE) ViewHolder(
            ItemVideoBinding.inflate(
                LayoutInflater.from(
                    activity
                ), parent, false
            )
        )
        else LoadingView(
            LayoutProgressBinding.inflate(
                LayoutInflater.from(
                    activity
                ), parent, false
            )
        )
    }

    fun refreshAdapter(arrayList: MutableList<SaveEntity>) {
        this.arrayList = arrayList
        isLoading = false
        if (activity !is PlayerActivity) {
            setAds(true)
        } else {
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AdHolder) {
            if (holder.binding.flAd.childCount <= 0) {
                AdLoader.getInstance().showNativeList(activity, holder.binding)
            }
        } else if (holder is ViewHolder) {
            Glide.with(activity).load(arrayList[position].imgUrl)
                .placeholder(MyApplication.getPlaceHolder())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.imgVideo)
            holder.binding.txtVideoTitle.text = Html.fromHtml(arrayList[position].title)
            holder.binding.txtVideoTitle.isSelected = true
            holder.itemView.setOnClickListener {
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


