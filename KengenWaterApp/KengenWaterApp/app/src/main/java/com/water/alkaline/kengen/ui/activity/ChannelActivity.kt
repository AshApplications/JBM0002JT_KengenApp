package com.water.alkaline.kengen.ui.activity

import android.os.Bundle
import android.view.View
import com.google.gms.ads.AdLoader
import com.google.gms.ads.MyApp
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.databinding.ActivityChannelBinding
import com.water.alkaline.kengen.ui.base.BaseActivity
import com.water.alkaline.kengen.ui.home.fragment.ChannelFragment.Companion.newInstance
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChannelActivity : BaseActivity() {

    private val binding by lazy {
        ActivityChannelBinding.inflate(layoutInflater)
    }
    private var catId: String = Constant.defaultId
    private var isChannel: Boolean = false

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
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getIntentData()
        if (!catId.equals(Constant.defaultId, ignoreCase = true)) {
            binding.includedToolbar.ivBack.setOnClickListener { onBackPressed() }
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.frameLayout,
                newInstance(
                    catId!!,
                    if (intent.hasExtra("isChannel")) "Video" else "Channel",
                    isChannel
                )
            )
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
    private fun getIntentData() {
        if (intent != null && intent.extras != null) {
            catId = intent.extras!!.getString("catId", Constant.defaultId)
            isChannel = intent.extras!!.getBoolean("isChannel", false)
        }
    }
}