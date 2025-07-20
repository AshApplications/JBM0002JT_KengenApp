package com.water.alkaline.kengen.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.gms.ads.AdLoader
import com.google.gms.ads.MyApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.preference.PowerPreference
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel
import com.water.alkaline.kengen.databinding.ActivityBannerBinding
import com.water.alkaline.kengen.databinding.ActivityFeedbackBinding
import com.water.alkaline.kengen.databinding.DialogDownloadBinding
import com.water.alkaline.kengen.library.downloader.Error
import com.water.alkaline.kengen.library.downloader.OnDownloadListener
import com.water.alkaline.kengen.library.downloader.OnProgressListener
import com.water.alkaline.kengen.library.downloader.PRDownloader
import com.water.alkaline.kengen.library.downloader.Progress
import com.water.alkaline.kengen.model.DownloadEntity
import com.water.alkaline.kengen.model.main.Banner
import com.water.alkaline.kengen.ui.adapter.VpImageAdapter
import com.water.alkaline.kengen.ui.base.BaseActivity
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.delayTask
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class BannerActivity : BaseActivity() {

    private val binding by lazy {
        ActivityBannerBinding.inflate(layoutInflater)
    }

    private var banners: ArrayList<Banner> = ArrayList()

    private var POS: Int = 0
    private var PAGE: String = Constant.LIVE

    private val viewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]
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

    private fun onClick() {
        binding.includedToolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                uiController.onBackPressed(this@BannerActivity)
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initDownloadDialog()
        getIntentData()
        startPager()
    }

    private fun getIntentData() {
        if (intent != null && intent.hasExtra("PAGE")) {
            POS = intent.getIntExtra("POS", 0)
            PAGE = intent.getStringExtra("PAGE").toString()
        }
    }

    private fun startPager() {
        onClick()
        binding.llmenuDownload.setOnClickListener {
            var entity: DownloadEntity? = null
            if (viewModel.getDownloadByUrl(banners[binding.viewpager.currentItem].url).isNotEmpty()
            ) entity = viewModel.getDownloadByUrl(
                banners[binding.viewpager.currentItem].url
            )[0]
            if (entity != null) {
                if (File(entity.filePath).exists()) {
                    shareImage(entity.filePath)
                } else {
                    downloadBanner(banners[binding.viewpager.currentItem], false)
                }
            } else downloadBanner(banners[binding.viewpager.currentItem], false)
        }

        delayTask(500) {
            refresh()
        }
    }

    private fun refresh() {
        try {
            val type = object : TypeToken<List<Banner?>?>() {
            }.type

            banners = Gson().fromJson(
                PowerPreference.getDefaultFile().getString(
                    Constant.mList, Gson().toJson(
                        ArrayList<Banner>()
                    )
                ), type
            )
        } catch (e: Exception) {
            banners = ArrayList()
        }

        if (banners.isNotEmpty()) {
            val adapter = VpImageAdapter(this, banners, { position: Int, item: Banner? -> })
            binding.viewpager.adapter = adapter
            binding.viewpager.setCurrentItem(POS, false)
            checkDownloadIcon(binding.viewpager.currentItem)
            binding.ivMenuLeft.visibility =
                if (binding.viewpager.currentItem == 0) View.GONE else View.VISIBLE
            binding.ivMenuRight.visibility =
                if (binding.viewpager.currentItem == adapter.itemCount - 1) View.GONE else View.VISIBLE
            checkData()
            binding.ivMenuLeft.setOnClickListener {
                binding.viewpager.setCurrentItem(
                    binding.viewpager.currentItem - 1, true
                )
            }
            binding.ivMenuRight.setOnClickListener {
                binding.viewpager.setCurrentItem(
                    binding.viewpager.currentItem + 1, true
                )
            }
            binding.viewpager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    checkDownloadIcon(position)
                    binding.ivMenuLeft.visibility =
                        if (binding.viewpager.currentItem == 0) View.GONE else View.VISIBLE
                    binding.ivMenuRight.visibility =
                        if (binding.viewpager.currentItem == adapter.itemCount - 1) View.GONE else View.VISIBLE
                }
            })
        } else {
            Constant.showToast(this, "Unknown Error Occurred")
            finish()
        }
    }

    private fun checkData() {
        binding.includedProgress.progress.visibility = View.GONE
        if (binding.viewpager.adapter != null && binding.viewpager.adapter!!.itemCount > 0) {
            binding.llPdfMenu.visibility = View.VISIBLE
            binding.includedProgress.llError.visibility = View.GONE
        } else {
            binding.llPdfMenu.visibility = View.GONE
        }
    }

    fun checkDownloadIcon(pos: Int) {
        var entity: DownloadEntity? = null
        if (viewModel.getDownloadByUrl(banners[pos].url).isNotEmpty()) entity =
            viewModel.getDownloadByUrl(
                banners[pos].url
            )[0]
        binding.ivDownload.visibility = if (entity != null) View.GONE else View.VISIBLE
        binding.ivShare.visibility = if (entity != null) View.VISIBLE else View.GONE
    }

    fun shareImage(mPath: String?) {
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.setType("image/*")
            i.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))

            var sAux = PowerPreference.getDefaultFile().getString(Constant.vidShareMsg, "")
            val sAux2 = "https://play.google.com/store/apps/details?id=" + packageName
            sAux = sAux + "\n\n" + sAux2
            val fileUri = FileProvider.getUriForFile(
                applicationContext,
                packageName + ".fileprovider", File(mPath)
            )
            i.putExtra(Intent.EXTRA_TEXT, sAux)
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            i.putExtra(Intent.EXTRA_STREAM, fileUri)

            startActivity(Intent.createChooser(i, "Choose One"))
        } catch (e: Exception) {
            e.printStackTrace()
            Constant.showToast(this@BannerActivity, "Something went wrong")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun downloadBanner(banner: Banner, isShare: Boolean) {
        if (!Constant.checkPermissions()) {
            Constant.getPermissions(this)
            return
        }
        showDownloadDialog()
        val filename = "file" + System.currentTimeMillis() + ".jpg"
        PRDownloader.download(banner.url, Constant.getImagedisc(), filename)
            .setTag(banner.id.toInt())
            .build()
            .setOnProgressListener { progress ->
                downloadBinding.txtvlu.text =
                    "Downloading " + (((progress.currentBytes.toDouble() / progress.totalBytes) * 100.0).toInt()) + " %"
            }.start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    hideDownloadDialog()
                    Constant.showToast(this@BannerActivity, "Download Completed")
                    val entity = DownloadEntity(
                        banner.name,
                        Constant.getImagedisc() + "/" + filename,
                        Constant.getImagedisc() + "/" + filename,
                        banner.url,
                        Constant.TYPE_IMAGE
                    )
                    viewModel.insertDownloads(entity)
                    Constant.scanMedia(this@BannerActivity, entity.filePath)
                    checkDownloadIcon(binding.viewpager.currentItem)
                    if (isShare) {
                        shareImage(Constant.getImagedisc() + "/" + filename)
                    }
                }

                override fun onError(error: Error) {
                    Log.e("TAG", error.toString())
                    Constant.showToast(this@BannerActivity, "Something went wrong")
                    hideDownloadDialog()
                }
            })

        downloadBinding.txtCancel.setOnClickListener {
            PRDownloader.cancel(banner.id.toInt())
            hideDownloadDialog()
        }
    }
}