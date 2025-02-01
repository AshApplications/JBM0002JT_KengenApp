package com.water.alkaline.kengen.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gms.ads.AdLoader
import com.google.gms.ads.MyApp
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel
import com.water.alkaline.kengen.databinding.ActivityDownloadBinding
import com.water.alkaline.kengen.model.DownloadEntity
import com.water.alkaline.kengen.ui.adapter.DownloadAdapter
import com.water.alkaline.kengen.ui.base.BaseActivity
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.delayTask
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadActivity : BaseActivity() {

    private val binding by lazy {
        ActivityDownloadBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]
    }
    var list: MutableList<DownloadEntity> = mutableListOf()
    private var adapter: DownloadAdapter? = null

    @SuppressLint("MissingSuperCall")
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

    private fun refreshFragment() {
        if (list.isNotEmpty() && adapter != null) {
            adapter!!.refreshAdapter(list)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.includedToolbar.ivBack.setOnClickListener { onBackPressed() }
        setAdapter()
        refreshActivity()
    }

    fun setAdapter() {
        adapter = DownloadAdapter(this, list) { _: Int, item: DownloadEntity ->
            uiController.gotoIntent(
                this,
                Intent(
                    this@DownloadActivity,
                    if (item.type == Constant.TYPE_PDF) PdfActivity::class.java else ImageActivity::class.java
                ).putExtra(
                    "mpath",
                    if (item.type == Constant.TYPE_PDF) item.filePath else item.url
                ),
                true,
                false
            )
        }
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
        binding.rvDownloads.layoutManager = manager
        binding.rvDownloads.adapter = adapter
        binding.rvDownloads.setItemViewCacheSize(100)
    }

    private fun refreshActivity() {
        if (binding.rvDownloads.adapter!!.itemCount <= 0) {
            delayTask(500) {
                adapter!!.refreshAdapter(viewModel.allDownloads)
                binding.includedProgress.progress.visibility = View.GONE
                checkData()
            }
        }
    }

    private fun checkData() {
        if (binding.rvDownloads.adapter != null && binding.rvDownloads.adapter!!.itemCount > 0) {
            binding.includedProgress.llError.visibility = View.GONE
        } else {
            binding.includedProgress.llError.visibility = View.VISIBLE
        }
    }
}