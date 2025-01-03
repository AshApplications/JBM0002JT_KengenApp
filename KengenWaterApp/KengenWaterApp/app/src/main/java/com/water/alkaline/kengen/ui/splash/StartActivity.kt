package com.water.alkaline.kengen.ui.splash

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gms.ads.AdLoader
import com.google.gms.ads.AdUtils
import com.google.gms.ads.MyApp
import com.google.gms.ads.MyApp.OnShowAdCompleteListener
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onesignal.OneSignal
import com.preference.PowerPreference
import com.water.alkaline.kengen.BuildConfig
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt
import com.water.alkaline.kengen.MyApplication
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel
import com.water.alkaline.kengen.data.network.RetroClient
import com.water.alkaline.kengen.databinding.ActivityStartBinding
import com.water.alkaline.kengen.databinding.DialogInternetBinding
import com.water.alkaline.kengen.library.ViewAnimator.ViewAnimator
import com.water.alkaline.kengen.model.main.MainResponse
import com.water.alkaline.kengen.model.update.UpdateResponse
import com.water.alkaline.kengen.ui.activity.HomeActivity
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.uiController
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StartActivity() : AppCompatActivity() {
    var viewModel: AppViewModel? = null
    var dialog: Dialog? = null

    var binding: ActivityStartBinding? = null
    var VERSION: Int = 0

    fun setBG() {
        viewModel = ViewModelProvider(this)[AppViewModel::class.java]
    }

    fun dismiss_dialog() {
        if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
    }

    fun network_dialog(text: String?): DialogInternetBinding {
        dialog = Dialog(this, R.style.NormalDialog)
        val binding = DialogInternetBinding.inflate(
            layoutInflater
        )
        dialog!!.setContentView(binding.root)
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog!!.show()
        binding.txtError.text = text
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        activity = this

        MyApplication.className = StartActivity::class.java.name

        PowerPreference.getDefaultFile().putBoolean(Constant.mIsLoaded, false)
        PowerPreference.getDefaultFile().putInt(Constant.mIsDuration, 1)
        PowerPreference.getDefaultFile().putBoolean(Constant.isRunning, true)

        ViewAnimator.animate(binding!!.ivLogo)
            .scale(0f, 1f)
            .andAnimate(binding!!.llText)
            .alpha(0f, 1f)
            .duration(1000)
            .onStop {
                binding!!.progress.visibility = View.VISIBLE
                setBG()
                startApp()
            }.start()
    }

    fun startApp() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (Constant.isVpnConnected()) {
                network_dialog(getString(R.string.kk_error_vpn))
                    .txtRetry.setOnClickListener {
                        dismiss_dialog()
                        startApp()
                    }
            } else if (MyApplication.getSub(this@StartActivity).equals("", ignoreCase = true)) {
                network_dialog("Something Went Wrong\nPlease Try Again Later !")
                    .txtRetry.setOnClickListener { dismiss_dialog() }
            } else {
                callAPI()
            }
        }, 1000)
    }

    fun callAPI() {
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
                updateAPI()
            }
        } else {
            handler.sendEmptyMessage(999)
        }
    }

    fun updateAPI() {
        if (Constant.checkInternet(this@StartActivity)) {
            @SuppressLint("HardwareIds") val deviceId = Settings.Secure.getString(
                contentResolver, Settings.Secure.ANDROID_ID
            )
            val token = PowerPreference.getDefaultFile().getString(Constant.Token, "123abc")

            val manager = packageManager
            var info: PackageInfo? = null

            try {
                info = manager.getPackageInfo(packageName, 0)
                VERSION = info.versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                Constant.showLog(e.toString())
                VERSION = BuildConfig.VERSION_CODE
            }

            RetroClient.getInstance(this).api.updateApi(
                DecryptEncrypt.EncryptStr(
                    this, MyApplication.updateApi(
                        this, deviceId, token, packageName, VERSION.toString(), ""
                    )
                )
            )
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        try {
                            val updateResponse = GsonBuilder().create().fromJson(
                                (DecryptEncrypt.DecryptStr(
                                    this@StartActivity, response.body()!!
                                        .string()
                                )), UpdateResponse::class.java
                            )

                            if (updateResponse != null) {
                                if (updateResponse.feedbacks != null) {
                                    PowerPreference.getDefaultFile().putString(
                                        Constant.mFeeds,
                                        Gson().toJson(updateResponse.feedbacks)
                                    )
                                }


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
                                    binding!!.txtName.text = appInfo.title
                                    binding!!.txtName.visibility = View.VISIBLE
                                }
                                if (appInfo.description != "") {
                                    binding!!.txtDes.text = appInfo.description
                                    binding!!.txtDes.visibility = View.VISIBLE
                                }
                                if (appInfo.buttonName != "") {
                                    binding!!.txtUpdate.text = appInfo.buttonName
                                }
                                if (appInfo.buttonSkip != "") {
                                    binding!!.txtSkip.text = appInfo.buttonSkip
                                }

                                val flag = appInfo.flag
                                var flagCheck = true
                                if ((flag == "NORMAL")) {
                                } else if ((flag == "SKIP")) {
                                    if (VERSION < appInfo.version.toInt()) {
                                        binding!!.cvUpdate.visibility = View.VISIBLE
                                        binding!!.txtUpdate.visibility = View.VISIBLE
                                        binding!!.txtSkip.visibility = View.VISIBLE
                                        binding!!.cvSplash.visibility = View.GONE
                                        flagCheck = false
                                    } else {
                                        binding!!.cvUpdate.visibility = View.GONE
                                        binding!!.cvSplash.visibility = View.VISIBLE
                                        flagCheck = true
                                    }
                                } else if ((flag == "MOVE")) {
                                    binding!!.cvUpdate.visibility = View.VISIBLE
                                    binding!!.txtSkip.visibility = View.GONE
                                    binding!!.txtUpdate.visibility = View.VISIBLE
                                    binding!!.cvSplash.visibility = View.GONE
                                    flagCheck = false
                                } else if ((flag == "FORCE")) {
                                    if (VERSION < appInfo.version.toInt()) {
                                        binding!!.cvUpdate.visibility = View.VISIBLE
                                        binding!!.txtSkip.visibility = View.GONE
                                        binding!!.txtUpdate.visibility = View.VISIBLE
                                        binding!!.cvSplash.visibility = View.GONE
                                        flagCheck = false
                                    } else {
                                        binding!!.cvUpdate.visibility = View.GONE
                                        binding!!.cvSplash.visibility = View.VISIBLE
                                    }
                                }

                                binding!!.txtUpdate.setOnClickListener(object :
                                    View.OnClickListener {
                                    override fun onClick(v: View) {
                                        startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(appInfo.link)
                                            )
                                        )
                                    }
                                })

                                binding!!.txtSkip.setOnClickListener(object : View.OnClickListener {
                                    override fun onClick(v: View) {
                                        binding!!.cvUpdate.visibility = View.GONE
                                        binding!!.cvSplash.visibility = View.VISIBLE
                                        mainAPI()
                                    }
                                })

                                if (flagCheck) {
                                    mainAPI()
                                }
                            } else {
                                handler.sendEmptyMessage(1000)
                            }
                        } catch (e: Exception) {
                            Constant.showLog(e.toString())
                            e.printStackTrace()
                            Constant.showToast(this@StartActivity, "Something went Wrong")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Constant.showLog(t.message)
                        handler.sendEmptyMessage(1000)
                    }
                })
        } else {
            handler.sendEmptyMessage(1000)
        }
    }

    fun loadAds() {
        try {
            val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            ai.metaData.putString(
                "com.google.android.gms.ads.APPLICATION_ID",
                MyApplication.getAdModel().adsAppId
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MobileAds.initialize(this, { initializationStatus: InitializationStatus? -> })
        AdLoader.getInstance().loadNativeAdPreload(this)
        AdLoader.getInstance().loadNativeListAds(this)
        AdLoader.getInstance().loadInterstitialAds(this)
    }

    fun mainAPI() {
        if (Constant.checkInternet(this@StartActivity)) {
            RetroClient.getInstance(this).api.dataApi(
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
            ).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    try {
                        val mainResponse = GsonBuilder().create().fromJson(
                            (DecryptEncrypt.DecryptStr(
                                this@StartActivity, response.body()!!
                                    .string()
                            )), MainResponse::class.java
                        )

                        viewModel!!.deleteAllCategory()
                        viewModel!!.deleteAllSubCategory()
                        viewModel!!.deleteAllBanner()
                        viewModel!!.deleteAllChannel()
                        viewModel!!.deleteAllPdf()

                        viewModel!!.insertCategory(mainResponse.data.categorys)
                        viewModel!!.insertSubCategory(mainResponse.data.subcategorys)
                        viewModel!!.insertChannel(mainResponse.data.channels)
                        viewModel!!.insertPdf(mainResponse.data.pdfs)
                        viewModel!!.insertBanner(mainResponse.data.banners)

                        nextActivity()
                    } catch (e: Exception) {
                        Constant.showLog(e.toString())
                        e.printStackTrace()
                        Constant.showToast(this@StartActivity, "Something went Wrong")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    handler.sendEmptyMessage(1001)
                }
            })
        } else {
            handler.sendEmptyMessage(1001)
        }
    }


    fun nextActivity() {
        Handler().postDelayed({
            if (MyApplication.getAdModel().getAdsSplash().equals("Yes", ignoreCase = true)) {
                MyApplication.getInstance().showAdIfAvailable(this, OnShowAdCompleteListener {
                    uiController.gotoActivity(
                        this, HomeActivity::class.java, false, true
                    )
                })
            } else {
                uiController.gotoActivity(this, HomeActivity::class.java, false, true)
            }
        }, 2000)
    }

    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (!this@StartActivity.isFinishing) {
                network_dialog(resources.getString(R.string.kk_error_no_internet)).txtRetry.setOnClickListener(
                     {
                        dismiss_dialog()
                        if (Constant.checkInternet(this@StartActivity)) {
                            if (msg.what == 1000) {
                                updateAPI()
                            } else if (msg.what == 1001) {
                                mainAPI()
                            } else {
                                callAPI()
                            }
                        } else dialog!!.show()
                    })
            } else {
                Log.e("TAG", "Something went wrong")
            }
        }
    }

    companion object {
        var activity: StartActivity? = null
    }
}