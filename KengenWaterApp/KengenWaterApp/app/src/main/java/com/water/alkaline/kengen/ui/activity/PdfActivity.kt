package com.water.alkaline.kengen.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.gms.ads.AdLoader
import com.google.gms.ads.MyApp
import com.preference.PowerPreference
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel
import com.water.alkaline.kengen.databinding.ActivityPdfBinding
import com.water.alkaline.kengen.databinding.DialogJumpBinding
import com.water.alkaline.kengen.library.downloader.Error
import com.water.alkaline.kengen.library.downloader.OnDownloadListener
import com.water.alkaline.kengen.library.downloader.PRDownloader
import com.water.alkaline.kengen.library.pdfviewer.scroll.DefaultScrollHandle
import com.water.alkaline.kengen.library.pdfviewer.util.FitPolicy
import com.water.alkaline.kengen.model.DownloadEntity
import com.water.alkaline.kengen.model.main.Pdf
import com.water.alkaline.kengen.ui.base.BaseActivity
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.showNetworkDialog
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@AndroidEntryPoint
class PdfActivity() : BaseActivity() {
    private val binding by lazy {
        ActivityPdfBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]
    }
    private var mPath: String = ""
    var page: Int = 0
    private var totPages: Int = 0
    private var fitPolicy: FitPolicy? = null
    private var isLoaded: Boolean = false
    private var nightMode: Boolean = false
    private var stream: InputStream? = null
    var pdf: Pdf? = null
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
        mPath = intent.extras!!.getString("mpath", "")
        if (mPath.startsWith("http")) {
            pdf = null
            if (viewModel.getByUrl(mPath).isNotEmpty()) pdf = viewModel.getByUrl(mPath)[0]
        } else {
            entity = null
            if (viewModel.getByPath(mPath).isNotEmpty()) entity = viewModel.getByPath(mPath)[0]
        }
        this.fitPolicy = FitPolicy.BOTH
        listener()
        checkDownload()
        if (mPath.startsWith("http")) {
            loadFromUrl()
        } else {
            setPdfFile(nightMode, FitPolicy.BOTH, mPath)
        }
    }


    fun listener() {
        binding.includedToolbar.ivBack.setOnClickListener { onBackPressed() }
        binding.llmenuJump.setOnClickListener { showJumpDialog() }
        binding.llmenuShare.setOnClickListener {
            if (pdf != null) {
                var entity: DownloadEntity? = null
                if (viewModel.getDownloadByUrl(pdf!!.url).size > 0) entity =
                    viewModel.getDownloadByUrl(
                        pdf!!.url
                    )[0]

                if (entity != null) {
                    if (File(entity.filePath).exists()) {
                        sharePDF(entity.filePath)
                    } else {
                        downloadPDF()
                    }
                } else {
                    downloadPDF()
                }
            } else {
                if (entity != null) {
                    if (File(entity!!.filePath).exists()) {
                        sharePDF(entity!!.filePath)
                    } else {
                        downloadPDF()
                    }
                } else {
                    downloadPDF()
                }
            }
        }
        binding.ivMenuLeft.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (page > 0) {
                    binding.pdfview.jumpTo(binding.pdfview.currentPage - 1, true)
                }
            }
        })

        binding.ivMenuRight.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (page < binding.pdfview.pageCount - 1) {
                    binding.pdfview.jumpTo(binding.pdfview.currentPage + 1, true)
                }
            }
        })
    }

    fun checkDownload() {
        if (mPath.startsWith("http")) {
            var entity: DownloadEntity? = null
            if (!viewModel.getDownloadByUrl(pdf!!.url).isEmpty()) entity =
                viewModel.getDownloadByUrl(
                    pdf!!.url
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

    private fun loadFromUrl() {
        if (Constant.checkInternet(this)) {
            CoroutineScope(Dispatchers.IO).launch {
                val url = URL(mPath)
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
                if (urlConnection.responseCode == 200) {
                    stream = BufferedInputStream(urlConnection.inputStream)
                }
                CoroutineScope(Dispatchers.Main).launch {
                    setPdfUrl(nightMode, FitPolicy.BOTH, stream)
                }
            }
        } else {
            showNetworkDialog {
                loadFromUrl()
            }
        }
    }

    fun checkArrow() {
        if (binding.pdfview.currentPage == 0) {
            binding.ivMenuLeft.visibility = View.GONE
        } else {
            binding.ivMenuLeft.visibility = View.VISIBLE
        }

        if (binding.pdfview.currentPage == binding.pdfview.pageCount - 1) {
            binding.ivMenuRight.visibility = View.GONE
        } else {
            binding.ivMenuRight.visibility = View.VISIBLE
        }
    }


    @SuppressLint("SetTextI18n")
    fun showJumpDialog() {
        val jumpBinding = DialogJumpBinding.inflate(
            layoutInflater
        )
        val dialogJump = Dialog(this@PdfActivity, R.style.NormalDialog)
        dialogJump.setContentView(jumpBinding.root)
        dialogJump.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogJump.window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        dialogJump.setCancelable(true)
        dialogJump.show()
        jumpBinding.editJump.setText((binding.pdfview.currentPage + 1).toString() + "")
        jumpBinding.txtJump.setOnClickListener {
            try {
                if (jumpBinding.editJump.getText().toString()
                        .equals("", ignoreCase = true)
                ) jumpBinding.editJump.error = "Enter Page Number"
                else if (jumpBinding.editJump.getText().toString()
                        .toInt() > totPages || jumpBinding.editJump.getText().toString()
                        .toInt() == 0
                ) jumpBinding.editJump.error = "Page Not Available"
                else {
                    dialogJump.dismiss()
                    binding.pdfview.jumpTo(jumpBinding.editJump.getText().toString().toInt() - 1)
                }
            } catch (e: Exception) {
                Log.e("TAG", e.toString())
                Constant.showToast(this@PdfActivity, "Something went wrong")
            }
        }


        jumpBinding.txtCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                dialogJump.dismiss()
            }
        })
    }


    @SuppressLint("SetTextI18n")
    fun setPdfUrl(nightMode: Boolean, fitPolicy: FitPolicy?, stream: InputStream?) {
        isLoaded = false
        binding.pdfview.fromStream(stream)
            .password(null)
            .enableAnnotationRendering(true)
            .pageFitPolicy(fitPolicy)
            .defaultPage(page)
            .swipeHorizontal(true)
            .autoSpacing(true)
            .scrollHandle(DefaultScrollHandle(this@PdfActivity))
            .pageFling(true)
            .pageSnap(false)
            .nightMode(nightMode)
            .onPageChange { page, pageCount ->
                this@PdfActivity.page = page
                binding.txtPageNumbers.text = (page + 1).toString() + " / " + pageCount
                checkArrow()
            }.onLoad { nbPages ->
                isLoaded = true
                totPages = nbPages
                binding.pdfview.visibility = View.VISIBLE
                binding.includedProgress.progress.visibility = View.GONE
                binding.txtPageNumbers.visibility = View.VISIBLE
                binding.includedProgress.llError.visibility = View.GONE
                binding.llPdfMenu.visibility = View.VISIBLE
                checkArrow()
            }.onError { t ->
                Constant.showLog(t.message)
                isLoaded = false
                totPages = 0
                binding.pdfview.visibility = View.GONE
                binding.includedProgress.progress.visibility = View.GONE
                binding.txtPageNumbers.visibility = View.GONE
                binding.includedProgress.llError.visibility = View.VISIBLE
                binding.llPdfMenu.visibility = View.GONE
                Constant.showToast(this@PdfActivity, "Something went wrong")
            }.load()
    }


    @SuppressLint("SetTextI18n")
    private fun setPdfFile(nightMode: Boolean, fitPolicy: FitPolicy?, stream: String) {
        isLoaded = false
        binding.pdfview.fromUri(Uri.parse("file://$stream"))
            .password(null)
            .enableAnnotationRendering(true)
            .pageFitPolicy(fitPolicy)
            .defaultPage(page)
            .autoSpacing(true)
            .swipeHorizontal(true)
            .scrollHandle(DefaultScrollHandle(this@PdfActivity))
            .pageFling(true)
            .pageSnap(false)
            .nightMode(nightMode)
            .onPageChange { page, pageCount ->
                this@PdfActivity.page = page
                binding.txtPageNumbers.text = (page + 1).toString() + " / " + pageCount
                checkArrow()
            }.onLoad { nbPages ->
                isLoaded = true
                totPages = nbPages
                binding.pdfview.visibility = View.VISIBLE
                binding.includedProgress.progress.visibility = View.GONE
                binding.txtPageNumbers.visibility = View.VISIBLE
                binding.includedProgress.llError.visibility = View.GONE
                binding.llPdfMenu.visibility = View.VISIBLE
                checkArrow()
            }.onError { t ->
                Constant.showLog(t.message)
                isLoaded = false
                totPages = 0
                binding.pdfview.visibility = View.GONE
                binding.includedProgress.progress.visibility = View.GONE
                binding.txtPageNumbers.visibility = View.GONE
                binding.includedProgress.llError.visibility = View.VISIBLE
                binding.llPdfMenu.visibility = View.GONE
                Constant.showToast(this@PdfActivity, "Something went wrong")
            }.load()
    }

    @SuppressLint("SetTextI18n")
    private fun downloadPDF() {
        if (!Constant.checkPermissions()) {
            Constant.getPermissions(this)
            return
        }
        showDownloadDialog()
        val filename = "file" + System.currentTimeMillis() + ".pdf"
        val url: String?
        if (pdf != null) {
            url = pdf!!.url
        } else {
            url = entity!!.url
        }
        PRDownloader.download(url, Constant.getPDFdisc(), filename)
            .setTag(pdf!!.id.toInt())
            .build()
            .setOnProgressListener { progress ->
                downloadBinding.txtvlu.text =
                    "Downloading " + (((progress.currentBytes.toDouble() / progress.totalBytes) * 100.0).toInt()) + " %"
            }.start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    hideDownloadDialog()
                    Constant.showToast(this@PdfActivity, "Download Completed")
                    val entity = DownloadEntity(
                        pdf!!.name,
                        Constant.getPDFdisc() + "/" + filename,
                        pdf!!.image,
                        pdf!!.url,
                        Constant.TYPE_PDF
                    )
                    viewModel.insertDownloads(entity)
                    Constant.scanPdf(this@PdfActivity, entity.filePath)
                    checkDownload()
                }

                override fun onError(error: Error) {
                    Constant.showToast(this@PdfActivity, "Something went wrong")
                    hideDownloadDialog()

                }
            })

        downloadBinding.txtCancel.setOnClickListener {
            PRDownloader.cancel(pdf!!.id.toInt())
            hideDownloadDialog()
        }
    }

    private fun sharePDF(mPath: String) {
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.setType("application/pdf")
            i.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))

            var sAux = PowerPreference.getDefaultFile().getString(Constant.vidShareMsg, "")

            val sAux2 = "https://play.google.com/store/apps/details?id=" + packageName
            sAux = sAux + "\n\n" + sAux2
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
            Constant.showToast(this@PdfActivity, "Something went wrong")
        }
    }
}