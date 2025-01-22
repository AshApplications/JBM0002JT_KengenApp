package com.water.alkaline.kengen.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gms.ads.AdLoader
import com.google.gms.ads.MyApp
import com.google.gson.Gson
import com.preference.PowerPreference
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel
import com.water.alkaline.kengen.databinding.ActivityPreviewBinding
import com.water.alkaline.kengen.databinding.ActivitySaveBinding
import com.water.alkaline.kengen.model.SaveEntity
import com.water.alkaline.kengen.ui.adapter.VideosAdapter
import com.water.alkaline.kengen.ui.base.BaseActivity
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.FeedBacksEvent
import com.water.alkaline.kengen.utils.RefreshSavedEvent
import com.water.alkaline.kengen.utils.delayTask
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class SaveActivity : BaseActivity() {
    private val binding by lazy {
        ActivitySaveBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]
    }
    var list: MutableList<SaveEntity> = ArrayList()
    var adapter: VideosAdapter? = null


    override fun onBackPressed() {
        uiController.onBackPressed(this)
    }

    override fun onResume() {
        super.onResume()
        if (MyApp.getAdModel().adsOnOff.equals("Yes", ignoreCase = true)) {
            if (binding.includedAd.flAd.childCount <= 0) {
                AdLoader.getInstance().showUniversalAd(this, binding.includedAd, false)
            }
        } else {
            binding.includedAd.cvAdMain.visibility = View.GONE
            binding.includedAd.flAd.visibility = View.GONE
            refreshFragment()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        EventBus.getDefault().register(this)
        binding.includedToolbar.ivBack.setOnClickListener { onBackPressed() }
        setAdapter()
        delayTask(500) {
            refreshData()
        }
    }

    private fun setAdapter() {
        val manager = GridLayoutManager(this, 2)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return when (adapter!!.getItemViewType(i)) {
                    Constant.STORE_TYPE -> 1
                    Constant.AD_TYPE -> 2
                    Constant.LOADING -> 1
                    else -> 1

                }
            }
        }
        binding.rvSaves.layoutManager = manager
        adapter = VideosAdapter(this, list, null) { position, item ->
            var pos = position
            for (i in list.indices) {
                if (list[i].videoId.equals(item.videoId, ignoreCase = true)) {
                    pos = i
                    break
                }
            }
            PowerPreference.getDefaultFile().putString(Constant.mList, Gson().toJson(list))
            uiController.gotoIntent(
                this@SaveActivity, Intent(this@SaveActivity, PreviewActivity::class.java).putExtra(
                    Constant.POSITION, pos
                ), true, false
            )
        }
        binding.rvSaves.adapter = adapter
        binding.rvSaves.setItemViewCacheSize(100)
    }

    private fun refreshFragment() {
        if (list.isNotEmpty() && adapter != null) {
            adapter!!.refreshAdapter(list)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshEvent(event: RefreshSavedEvent) {
        refreshData()
    }

    private fun refreshData() {
        list.clear()
        list.addAll(viewModel.allSaves)
        adapter!!.refreshAdapter(list)
        binding.includedProgress.progress.visibility = View.GONE
        checkData()
    }

    private fun checkData() {
        if (binding.rvSaves.adapter != null && binding.rvSaves.adapter!!.itemCount > 0) {
            binding.includedProgress.llError.visibility = View.GONE
        } else {
            binding.includedProgress.llError.visibility = View.VISIBLE
        }
    }
}