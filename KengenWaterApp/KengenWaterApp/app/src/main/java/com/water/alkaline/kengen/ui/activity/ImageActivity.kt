package com.water.alkaline.kengen.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gms.ads.AdLoader
import com.google.gms.ads.MyApp
import com.preference.PowerPreference
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel
import com.water.alkaline.kengen.databinding.ActivityImageBinding
import com.water.alkaline.kengen.library.downloader.Error
import com.water.alkaline.kengen.library.downloader.OnDownloadListener
import com.water.alkaline.kengen.library.downloader.PRDownloader
import com.water.alkaline.kengen.model.DownloadEntity
import com.water.alkaline.kengen.model.main.Banner
import com.water.alkaline.kengen.ui.base.BaseActivity
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ImageActivity : BaseActivity() {

    private val binding by lazy {
        ActivityImageBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]
    }
    
    var mPath: String = ""
    var banner: Banner? = null
    var entity: DownloadEntity? = null
    
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

    override fun onBackPressed() {
        uiController.onBackPressed(this)
    }

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDownloadDialog()
        binding.includedToolbar.ivBack.setOnClickListener { onBackPressed() }
        mPath = intent.extras!!.getString("mpath", "")
        if (mPath.startsWith("http")) {
            banner = null
            if (viewModel.getBannerByUrl(mPath).isNotEmpty()) {
                banner = viewModel.getBannerByUrl(mPath)[0]
                Glide.with(this).load(banner!!.url).diskCacheStrategy(DiskCacheStrategy.ALL).into(
                    binding.ivImage
                )
            }
        } else {
            entity = null
            if (viewModel.getDownloadByUrl(mPath).isNotEmpty()) {
                entity = viewModel.getDownloadByUrl(mPath)[0]
                Glide.with(this).load(entity!!.filePath).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(
                        binding.ivImage
                    )
            }
        }

        checkDownload()
        listener()
    }

    fun checkDownload() {
        if (banner != null) {
            var entity: DownloadEntity? = null
            if (viewModel.getDownloadByUrl(banner!!.url).isNotEmpty()) entity =
                viewModel.getDownloadByUrl(
                    banner!!.url
                )[0]

            if (entity != null) {
                binding.ivDownload.visibility = View.GONE
                binding.ivShare.visibility = View.VISIBLE
            } else {
                binding.ivDownload.visibility = View.VISIBLE
                binding.ivShare.visibility = View.GONE
            }
        } else {
            binding.ivDownload.visibility = View.GONE
            binding.ivShare.visibility = View.VISIBLE
        }
    }


    private fun listener() {
        binding.llmenuDownload.setOnClickListener {
            if (banner != null) {
                var entity: DownloadEntity? = null
                if (viewModel.getDownloadByUrl(banner!!.url).isNotEmpty()) entity =
                    viewModel.getDownloadByUrl(
                        banner!!.url
                    )[0]

                if (entity != null) {
                    if (File(entity.filePath).exists()) {
                        shareImage(entity.filePath)
                    } else {
                        downloadBanner(true)
                    }
                } else {
                    downloadBanner(true)
                }
            } else {
                if (entity != null) {
                    if (File(entity!!.filePath).exists()) {
                        shareImage(entity!!.filePath)
                    } else {
                        downloadBanner(true)
                    }
                } else {
                    downloadBanner(true)
                }
            }
        }
        binding.llmenuDownload.setOnClickListener { v: View? ->
            if (banner != null) {
                var entity: DownloadEntity? = null
                if (viewModel.getDownloadByUrl(banner!!.url).size > 0) entity =
                    viewModel.getDownloadByUrl(
                        banner!!.url
                    )[0]

                if (entity != null) {
                    if (File(entity!!.filePath).exists()) {
                        shareImage(entity!!.filePath)
                    } else {
                        downloadBanner(false)
                    }
                } else downloadBanner(false)
            } else {
                Constant.showToast(
                    this@ImageActivity,
                    "File Already downloaded at " + entity!!.filePath
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun downloadBanner(isShare: Boolean) {
        if (!Constant.checkPermissions()) {
            Constant.getPermissions(this)
            return
        }
        showDownloadDialog()
        val filename = "file" + System.currentTimeMillis() + ".jpg"
        PRDownloader.download(banner!!.url, Constant.getImagedisc(), filename)
            .setTag(banner!!.id.toInt())
            .build()
            .setOnProgressListener { progress ->
                downloadBinding.txtvlu.text =
                    "Downloading " + ((progress.currentBytes.toDouble() / progress.totalBytes) * 100.0).toInt() + " %"
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    hideDownloadDialog()
                    Constant.showToast(this@ImageActivity, "Download Completed")
                    val entity = DownloadEntity(
                        banner!!.name,
                        Constant.getImagedisc() + "/" + filename,
                        Constant.getImagedisc() + "/" + filename,
                        banner!!.url,
                        Constant.TYPE_IMAGE
                    )
                    viewModel.insertDownloads(entity)
                    checkDownload()
                    if (isShare) {
                        shareImage(Constant.getImagedisc() + "/" + filename)
                    }
                }

                override fun onError(error: Error) {
                    Log.e("TAG", error.toString())
                    Constant.showToast(this@ImageActivity, "Something went wrong")
                    hideDownloadDialog()

                }
            })

        downloadBinding.txtCancel.setOnClickListener {
            PRDownloader.cancel(banner!!.id.toInt())
            hideDownloadDialog()
        }
    }

    fun shareImage(mPath: String?) {
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.setType("image/*")
            i.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))

            var sAux = PowerPreference.getDefaultFile().getString(Constant.vidShareMsg, "")
            val sAux2 = "https://play.google.com/store/apps/details?id=$packageName"

            sAux = """
                $sAux
                
                $sAux2
                """.trimIndent()
            val fileUri = FileProvider.getUriForFile(
                applicationContext,
                "$packageName.fileprovider", File(mPath)
            )
            i.putExtra(Intent.EXTRA_TEXT, sAux)
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            i.putExtra(Intent.EXTRA_STREAM, fileUri)

            startActivity(Intent.createChooser(i, "Choose One"))
        } catch (e: Exception) {
            e.printStackTrace()
            Constant.showToast(this@ImageActivity, "Something went wrong")
        }
    }
}