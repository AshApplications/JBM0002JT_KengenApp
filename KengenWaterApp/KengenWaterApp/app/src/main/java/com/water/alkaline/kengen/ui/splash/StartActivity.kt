package com.water.alkaline.kengen.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.MobileAds
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gms.ads.AdLoader
import com.google.gms.ads.AdUtils
import com.google.gms.ads.MyApp
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onesignal.OneSignal
import com.preference.PowerPreference
import com.water.alkaline.kengen.BuildConfig
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt
import com.water.alkaline.kengen.MyApplication
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel
import com.water.alkaline.kengen.databinding.ActivityStartBinding
import com.water.alkaline.kengen.library.ViewAnimator.ViewAnimator
import com.water.alkaline.kengen.model.NetworkResult
import com.water.alkaline.kengen.model.main.MainResponse
import com.water.alkaline.kengen.model.update.UpdateResponse
import com.water.alkaline.kengen.ui.activity.HomeActivity
import com.water.alkaline.kengen.ui.base.BaseActivity
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.delayTask
import com.water.alkaline.kengen.utils.onSingleClick
import com.water.alkaline.kengen.utils.showNetworkDialog
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartActivity : BaseActivity() {
    private val binding by lazy {
        ActivityStartBinding.inflate(layoutInflater)
    }

    var version: Int = 0
    private lateinit var startViewModel: StartViewModel
    private lateinit var appViewModel: AppViewModel
    private lateinit var deviceId: String
    
    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        activity = this

        MyApplication.className = StartActivity::class.java.name
        bindObservers()

        deviceId = Settings.Secure.getString(
            contentResolver, Settings.Secure.ANDROID_ID
        )

        PowerPreference.getDefaultFile().putBoolean(Constant.mIsLoaded, false)
        PowerPreference.getDefaultFile().putInt(Constant.mIsDuration, 1)
        PowerPreference.getDefaultFile().putBoolean(Constant.isRunning, true)

        ViewAnimator.animate(binding.ivLogo)
            .scale(0f, 1f)
            .andAnimate(binding.llText)
            .alpha(0f, 1f)
            .duration(1000)
            .onStop {
                binding.progress.visibility = View.VISIBLE
                startApp()
            }.start()
    }

    private fun bindObservers() {
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        startViewModel = ViewModelProvider(this)[StartViewModel::class.java]
        startViewModel.updateData.observe(this) {
            if (it is NetworkResult.Success) {
                try {
                    val updateResponse = GsonBuilder().create().fromJson(
                        (DecryptEncrypt.DecryptStr(
                            this@StartActivity, it.data!!.string()
                        )), UpdateResponse::class.java
                    )
                    if (updateResponse != null) {
                        parseUpdateData(updateResponse)
                        return@observe
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                handler.sendMessage(Message().apply {
                    what = 1000
                    data = bundleOf("message" to getString(R.string.kk_error_try_again))
                })
            } else {
                Constant.showLog(it.message)
                handler.sendMessage(Message().apply {
                    what = 1000
                    data = bundleOf("message" to it.message)
                })
            }
        }

        startViewModel.mainData.observe(this) {
            if (it is NetworkResult.Success) {
                try {
                    val mainResponse = GsonBuilder().create().fromJson(
                        (DecryptEncrypt.DecryptStr(
                            this@StartActivity, it.data!!.string()
                        )), MainResponse::class.java
                    )
                    if (mainResponse != null) {
                        parseMainData(mainResponse)
                        return@observe
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                handler.sendMessage(Message().apply {
                    what = 1001
                    data = bundleOf("message" to getString(R.string.kk_error_try_again))
                })
            } else {
                Constant.showLog(it.message)
                handler.sendMessage(Message().apply {
                    what = 1001
                    data = bundleOf("message" to it.message)
                })
            }
        }
    }

    private fun startApp() {
        delayTask(1000) {
            if (Constant.isVpnConnected()) {
                showNetworkDialog(getString(R.string.kk_error_vpn)) {
                    startApp()
                }
            } else if (MyApplication.getSub(this@StartActivity).equals("", ignoreCase = true)) {
                showNetworkDialog(getString(R.string.kk_error_try_later)) { }
            } else {
                callAPI()
            }
        }
    }

    private fun callAPI() {
        if (Constant.checkInternet(this@StartActivity)) {
            val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build()
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener {
                PowerPreference.getDefaultFile()
                    .putString(Constant.apiKey, mFirebaseRemoteConfig.getValue("url").asString())
                fetchUpdateData()
            }
        } else {
            handler.sendEmptyMessage(999)
        }
    }

    private fun fetchUpdateData() {

        val token = PowerPreference.getDefaultFile().getString(Constant.Token, "123abc")

        val manager = packageManager
        var info: PackageInfo? = null

        try {
            info = manager.getPackageInfo(packageName, 0)
            version = info.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            Constant.showLog(e.toString())
            version = BuildConfig.VERSION_CODE
        }

        startViewModel.fetchUpdateData(
            DecryptEncrypt.EncryptStr(
                this, MyApplication.updateApi(
                    this, deviceId, token, packageName, version.toString(), ""
                )
            )
        )
    }
    
    private fun parseUpdateData(updateResponse: UpdateResponse) {

        MyApp.setAdModel(updateResponse.data.adsInfo[0])

        val appInfo = updateResponse.data.appInfo[0]

        PowerPreference.getDefaultFile()
            .putString(Constant.mToken, updateResponse.mtoken)
        PowerPreference.getDefaultFile()
            .putString(AdUtils.QUREKA, appInfo.qureka)

        PowerPreference.getDefaultFile()
            .putString(Constant.mKeyId, appInfo.apiKey)
        PowerPreference.getDefaultFile()
            .putString(Constant.mNotice, appInfo.appNotice)

        PowerPreference.getDefaultFile()
            .putString(Constant.notifyKey, appInfo.notifyKey)

        PowerPreference.getDefaultFile()
            .putString(Constant.appShareMsg, appInfo.appShareMsg)
        PowerPreference.getDefaultFile()
            .putString(Constant.vidShareMsg, appInfo.vidShareMsg)


        if (!PowerPreference.getDefaultFile()
                .getString(Constant.T_DATE, "not")
                .equals(appInfo.todayDate, ignoreCase = true)
        ) {
            PowerPreference.getDefaultFile()
                .putString(Constant.T_DATE, appInfo.todayDate)
            PowerPreference.getDefaultFile()
                .putInt(AdUtils.APP_CLICK_COUNT, 0)
        } else {
            val clickCount2 = PowerPreference.getDefaultFile()
                .getInt(AdUtils.APP_CLICK_COUNT, 0)
            if (clickCount2 >= PowerPreference.getDefaultFile()
                    .getInt(AdUtils.AD_CLICK_COUNT, 3)
            ) {
                AdLoader.disableAds()
            }
        }

        loadAds()

        try {
            OneSignal.setLogLevel(
                OneSignal.LOG_LEVEL.VERBOSE,
                OneSignal.LOG_LEVEL.NONE
            )
            OneSignal.initWithContext(MyApplication.getContext())
            OneSignal.setAppId(appInfo.oneSignalAppId)
            OneSignal.sendTag("deviceId", deviceId)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (appInfo.title != "") {
            binding.txtName.text = appInfo.title
            binding.txtName.visibility = View.VISIBLE
        }
        if (appInfo.description != "") {
            binding.txtDes.text = appInfo.description
            binding.txtDes.visibility = View.VISIBLE
        }
        if (appInfo.buttonName != "") {
            binding.txtUpdate.text = appInfo.buttonName
        }
        if (appInfo.buttonSkip != "") {
            binding.txtSkip.text = appInfo.buttonSkip
        }

        val flag = appInfo.flag
        var flagCheck = true
        if ((flag == "SKIP")) {
            if (version < appInfo.version.toInt()) {
                binding.cvUpdate.visibility = View.VISIBLE
                binding.txtUpdate.visibility = View.VISIBLE
                binding.txtSkip.visibility = View.VISIBLE
                binding.cvSplash.visibility = View.GONE
                flagCheck = false
            } else {
                binding.cvUpdate.visibility = View.GONE
                binding.cvSplash.visibility = View.VISIBLE
                flagCheck = true
            }
        } else if ((flag == "MOVE")) {
            binding.cvUpdate.visibility = View.VISIBLE
            binding.txtSkip.visibility = View.GONE
            binding.txtUpdate.visibility = View.VISIBLE
            binding.cvSplash.visibility = View.GONE
            flagCheck = false
        } else if ((flag == "FORCE")) {
            if (version < appInfo.version.toInt()) {
                binding.cvUpdate.visibility = View.VISIBLE
                binding.txtSkip.visibility = View.GONE
                binding.txtUpdate.visibility = View.VISIBLE
                binding.cvSplash.visibility = View.GONE
                flagCheck = false
            } else {
                binding.cvUpdate.visibility = View.GONE
                binding.cvSplash.visibility = View.VISIBLE
            }
        }

        binding.txtUpdate.onSingleClick {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(appInfo.link)
                )
            )
        }

        binding.txtSkip.onSingleClick {
            binding.cvUpdate.visibility = View.GONE
            binding.cvSplash.visibility = View.VISIBLE
            fetchMainData()
        }

        if (flagCheck) {
            fetchMainData()
        }

    }

    private fun fetchMainData()
    {
        startViewModel.fetchMainData(
            DecryptEncrypt.EncryptStr(
                this@StartActivity, MyApplication.updateApi(
                    this,
                    "",
                    PowerPreference.getDefaultFile().getString(Constant.mToken, "123"),
                    "",
                    "",
                    ""
                )
            )
        )
    }

    private fun parseMainData(mainResponse: MainResponse) {

        appViewModel.deleteAllCategory()
        appViewModel.deleteAllSubCategory()
        appViewModel.deleteAllBanner()
        appViewModel.deleteAllChannel()
        appViewModel.deleteAllPdf()

        appViewModel.insertCategory(mainResponse.data.categorys)
        appViewModel.insertSubCategory(mainResponse.data.subcategorys)
        appViewModel.insertChannel(mainResponse.data.channels)
        appViewModel.insertPdf(mainResponse.data.pdfs)
        appViewModel.insertBanner(mainResponse.data.banners)

        nextActivity()
    }

    private fun loadAds() {
        try {
            val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            ai.metaData.putString(
                "com.google.android.gms.ads.APPLICATION_ID",
                MyApplication.getAdModel().adsAppId
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MobileAds.initialize(this) { }
        AdLoader.getInstance().loadNativeAdPreload(this)
        AdLoader.getInstance().loadNativeListAds(this)
        AdLoader.getInstance().loadInterstitialAds(this)
    }

    private fun nextActivity() {
        delayTask {
            if (MyApplication.getAdModel().adsSplash.equals("Yes", ignoreCase = true)) {
                MyApplication.getInstance().showAdIfAvailable(this) {
                    uiController.gotoActivity(
                        this, HomeActivity::class.java, false, true
                    )
                }
            } else {
                uiController.gotoActivity(this, HomeActivity::class.java, false, true)
            }
        }
    }

    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (!this@StartActivity.isFinishing) {
                showDialog(msg)
            } else {
                Log.e("TAG", "Something went wrong")
            }
        }
    }

    fun showDialog(msg: Message) {
        showNetworkDialog(
            msg.data.getString(
                "message",
                resources.getString(R.string.kk_error_no_internet)
            )
        ) {
            if (Constant.checkInternet(this@StartActivity)) {
                if (msg.what == 1000) {
                    fetchUpdateData()
                } else if (msg.what == 1001) {
                    fetchMainData()
                } else {
                    callAPI()
                }
            } else {
                delayTask {
                    showDialog(msg)
                }
            }
        }
    }

    companion object {
        var activity: StartActivity? = null
    }
}