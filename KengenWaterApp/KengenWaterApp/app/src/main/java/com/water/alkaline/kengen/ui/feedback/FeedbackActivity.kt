package com.water.alkaline.kengen.ui.feedback

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gms.ads.AdLoader
import com.google.gms.ads.MyApp
import com.google.gson.GsonBuilder
import com.preference.PowerPreference
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt
import com.water.alkaline.kengen.MyApplication
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.databinding.ActivityFeedbackBinding
import com.water.alkaline.kengen.model.NetworkResult
import com.water.alkaline.kengen.model.feedback.FeedbackResponse
import com.water.alkaline.kengen.ui.adapter.ViewPagerFragmentAdapter
import com.water.alkaline.kengen.ui.base.BaseActivity
import com.water.alkaline.kengen.ui.feedback.fragment.FeedbackFragment
import com.water.alkaline.kengen.ui.feedback.fragment.HistoryFragment
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.FeedBacksEvent
import com.water.alkaline.kengen.utils.showNetworkDialog
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class FeedbackActivity : BaseActivity() {
    private val binding by lazy {
        ActivityFeedbackBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: ViewPagerFragmentAdapter

    private val feedbackViewModel by lazy {
        ViewModelProvider(this)[FeedbackViewModel::class.java]
    }

    override fun onBackPressed() {
        uiController.onBackPressed(this)
    }

    override fun onResume() {
        super.onResume()
        if (MyApp.getAdModel().adsOnOff.equals("Yes", ignoreCase = true)) {
            if (binding.includedAd.flAd.childCount <= 0) {
                AdLoader.getInstance().showUniversalAd(this, binding.includedAd, true)
            }
        } else {
            binding.includedAd.cvAdMain.visibility = View.GONE
            binding.includedAd.flAd.visibility = View.GONE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bindObservers()
        onClick()
        setAdapter()
        fetchFeedbacks()
    }

    fun onClick() {
        binding.ivBack.setOnClickListener { onBackPressed() }
        binding.ivInfo.setOnClickListener { fetchFeedbacks() }
    }

    private fun setAdapter() {
        adapter = ViewPagerFragmentAdapter(supportFragmentManager, lifecycle)
        adapter.addFragment(FeedbackFragment(), "Feedbacks")
        adapter.addFragment(HistoryFragment(), "History")
        binding.vpFeeds.adapter = adapter
        binding.vpFeeds.offscreenPageLimit = 2
        TabLayoutMediator(
            binding.tabHome,
            binding.vpFeeds
        ) { tab: TabLayout.Tab, position: Int ->
            tab.setText(
                adapter.mFragmentTitleList[position]
            )
        }.attach()
        binding.vpFeeds.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (adapter.arrayList[position] is HistoryFragment) {
                    binding.ivInfo.visibility = View.VISIBLE
                } else {
                    binding.ivInfo.visibility = View.GONE
                }
            }
        })
    }

    private fun bindObservers() {
        feedbackViewModel.getFeedData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoadingDialog()
                    try {
                        val response = GsonBuilder().create().fromJson(
                            (DecryptEncrypt.DecryptStr(
                                this@FeedbackActivity, it.data!!.string()
                            )), FeedbackResponse::class.java
                        )
                        if (response != null) {
                            EventBus.getDefault().post(FeedBacksEvent(response.feedbacks))
                            return@observe
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    showNetworkDialog(getString(R.string.kk_error_try_again)) {

                    }
                }

                is NetworkResult.Error -> {
                    hideLoadingDialog()
                    Constant.showLog(it.message)
                    showNetworkDialog(it.message) {

                    }
                }

                is NetworkResult.Loading -> {
                    showLoadingDialog()
                }
            }
        }
    }

    private fun fetchFeedbacks() {
        @SuppressLint("HardwareIds") val deviceId = Settings.Secure.getString(
            contentResolver, Settings.Secure.ANDROID_ID
        )
        feedbackViewModel.fetchData(
            DecryptEncrypt.EncryptStr(
                this@FeedbackActivity, MyApplication.sendFeedApi(
                    this,
                    deviceId,
                    PowerPreference.getDefaultFile().getString(Constant.mToken, "123"),
                    "",
                    ""
                )
            )
        )
    }
}