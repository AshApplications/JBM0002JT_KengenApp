package com.water.alkaline.kengen.ui.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.gms.ads.AdLoader
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.databinding.DialogDownloadBinding
import com.water.alkaline.kengen.databinding.DialogLoadingBinding

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var loaderDialog: Dialog
    private lateinit var downloadDialog: Dialog
    public lateinit var downloadBinding: DialogDownloadBinding

    private fun initDialog() {
        loaderDialog = Dialog(this, R.style.NormalDialog)
        val loadingBinding: DialogLoadingBinding = DialogLoadingBinding.inflate(layoutInflater)
        loaderDialog.setContentView(loadingBinding.getRoot())
        loaderDialog.setCancelable(false)
        loaderDialog.setCanceledOnTouchOutside(false)
        loaderDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loaderDialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    fun showLoadingDialog() {
        try {
            loaderDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideLoadingDialog() {
        try {
            if (loaderDialog != null && loaderDialog.isShowing) {
                loaderDialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initDownloadDialog() {
        downloadDialog = Dialog(this, R.style.NormalDialog)
        downloadBinding = DialogDownloadBinding.inflate(
            layoutInflater
        )
        downloadDialog.setContentView(downloadBinding.root)
        downloadDialog.setCancelable(false)
        downloadDialog.setCanceledOnTouchOutside(false)
        downloadDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        downloadDialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        downloadDialog.setOnShowListener {
            AdLoader.getInstance().showNativeDialog(
                this, downloadBinding.includedAd
            )
        }
    }

    fun showDownloadDialog() {
        try {
            downloadDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideDownloadDialog() {
        try {
            if (downloadDialog != null && downloadDialog.isShowing) {
                downloadDialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialog()
    }
}