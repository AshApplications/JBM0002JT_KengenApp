package com.water.alkaline.kengen.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gms.ads.AdLoader
import com.google.gms.ads.MyApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.preference.PowerPreference
import com.water.alkaline.kengen.MyApplication
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.databinding.ActivityPreviewBinding
import com.water.alkaline.kengen.library.ActionListeners
import com.water.alkaline.kengen.library.ViewToImage
import com.water.alkaline.kengen.model.SaveEntity
import com.water.alkaline.kengen.ui.base.BaseActivity
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.onSingleClick
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PreviewActivity : BaseActivity() {

    private val binding by lazy {
        ActivityPreviewBinding.inflate(layoutInflater)
    }
    private var mList: ArrayList<SaveEntity> = ArrayList()
    private var pos: Int = 0

    override fun onBackPressed() {
        uiController.onBackPressed(this)
    }

    override fun onResume() {
        super.onResume()
        if (MyApp.getAdModel().adsOnOff.equals("Yes", ignoreCase = true)) {
            if (binding.includedAd.flAd.childCount <= 0) {
                AdLoader.getInstance().showNativeLarge(this, binding.includedAd)
            }
        } else {
            binding.includedAd.cvAdMain.visibility = View.GONE
            binding.includedAd.flAd.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent != null && intent.hasExtra(Constant.POSITION)) {
            pos = intent.getIntExtra(Constant.POSITION, 0)
        }

        try {
            val type = object : TypeToken<List<SaveEntity?>?>() {
            }.type

            mList = Gson().fromJson(
                PowerPreference.getDefaultFile().getString(
                    Constant.mList, Gson().toJson(
                        ArrayList<SaveEntity>()
                    )
                ), type
            )

            if (mList[mList.size - 1].videoId.equals("99999", ignoreCase = true)) mList.removeAt(
                mList.size - 1
            )
        } catch (e: Exception) {
            mList = ArrayList()
        }

        if (mList.isNotEmpty()) {
            Glide.with(this)
                .load(mList[pos].imgUrl)
                .placeholder(MyApplication.getPlaceHolder())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.frameContainer)
            binding.txtTitle.text = Html.fromHtml(mList.get(pos).title)
            binding.includedToolbar.ivBack.setOnClickListener(View.OnClickListener { onBackPressed() })
            binding.btnStart.setOnClickListener {
                uiController.gotoIntent(
                    this,
                    Intent(
                        this@PreviewActivity,
                        PlayerActivity::class.java
                    ).putExtra(Constant.POSITION, pos),
                    true,
                    false
                )
            }
            binding.btnShare.setOnClickListener {
                shareVideo()
            }
        } else {
            Constant.showToast(this, "Unknown Error Occurred")
            finish()
        }
    }


    private fun shareVideo() {
        showLoadingDialog()
        var path: String? = ""
        path = mList[pos].imgUrl
        Glide.with(this).asBitmap().load(path)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    saveBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    hideLoadingDialog()
                }
            })
    }

    fun saveBitmap(bitmap: Bitmap?) {
        ViewToImage(this@PreviewActivity, bitmap, object : ActionListeners {
            override fun convertedWithSuccess(var1: Bitmap, var2: String) {
                hideLoadingDialog()
                shareIImage(var2)
            }

            override fun convertedWithError(var1: String) {
                 Toast.makeText(this@PreviewActivity, "Something went Wrong", Toast.LENGTH_SHORT)
                    .show()
                hideLoadingDialog()
            }
        })
    }

    fun shareIImage(mPath: String?) {
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.setType("image/*")
            i.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))

            val title = mList[pos].title
            var sAux = PowerPreference.getDefaultFile().getString(Constant.vidShareMsg, "")

            val sAux2 = "https://play.google.com/store/apps/details?id=" + packageName
            sAux = title + "\n\n" + sAux + "\n\n" + sAux2

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
            Toast.makeText(this@PreviewActivity, "Something went Wrong", Toast.LENGTH_SHORT).show()
        }
    }
}