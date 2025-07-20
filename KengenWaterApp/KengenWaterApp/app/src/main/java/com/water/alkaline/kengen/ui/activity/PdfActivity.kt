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
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import com.rajat.pdfviewer.PdfRendererView
import androidx.core.net.toUri

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

    private fun onClick() {
        binding.includedToolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                uiController.onBackPressed(this@PdfActivity)
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
        mPath = intent.extras!!.getString("mpath", "")
        if (mPath.startsWith("http")) {
            pdf = null
            if (viewModel.getByUrl(mPath).isNotEmpty()) pdf = viewModel.getByUrl(mPath)[0]
        } else {
            entity = null
            if (viewModel.getByPath(mPath).isNotEmpty()) entity = viewModel.getByPath(mPath)[0]
        }
        listener()
        checkDownload()
        if (mPath.startsWith("http")) {
            loadFromUrl()
        } else {
            setPdfFile(mPath)
        }
    }


    fun listener() {
        onClick()
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
                    binding.pdfview.jumpToPage(page - 1, true)
                }
            }
        })

        binding.ivMenuRight.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                 if (page < binding.pdfview.totalPageCount - 1) {
                    binding.pdfview.jumpToPage(page + 1, true)
                }
            }
        })
    }

    fun checkDownload() {
        if (mPath.startsWith("http")) {
            var entity: DownloadEntity? = null
            if (viewModel.getDownloadByUrl(pdf!!.url).isNotEmpty()) entity =
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
                    setPdfUrl(mPath)
                }
            }
        } else {
            showNetworkDialog {
                loadFromUrl()
            }
        }
    }

    fun checkArrow() {
        if (page == 0) {
            binding.ivMenuLeft.visibility = View.GONE
        } else {
            binding.ivMenuLeft.visibility = View.VISIBLE
        }

        if (page == binding.pdfview.totalPageCount - 1) {
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
        dialogJump.window!!.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialogJump.window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        dialogJump.setCancelable(true)
        dialogJump.show()
        jumpBinding.editJump.setText((page + 1).toString() + "")
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
                    binding.pdfview.jumpToPage(
                        jumpBinding.editJump.getText().toString().toInt() - 1
                    )
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
    fun setPdfUrl( stream: String) {
        isLoaded = false
        binding.pdfview.statusListener = object : PdfRendererView.StatusCallBack {
            override fun onPdfLoadSuccess(absolutePath: String) {

            }

            override fun onPdfRenderSuccess() {
                super.onPdfRenderSuccess()
                isLoaded = true
                totPages = binding.pdfview.totalPageCount
                binding.pdfview.visibility = View.VISIBLE
                binding.includedProgress.progress.visibility = View.GONE
                binding.txtPageNumbers.visibility = View.GONE
                binding.includedProgress.llError.visibility = View.GONE
                binding.llPdfMenu.visibility = View.VISIBLE
                checkArrow()
            }

            override fun onPageChanged(currentPage: Int, totalPage: Int) {
                super.onPageChanged(currentPage, totalPage)
                this@PdfActivity.page = currentPage
                binding.txtPageNumbers.text = (page + 1).toString() + " / " + totalPage
                checkArrow()
            }

            override fun onError(error: Throwable) {
                super.onError(error)
                Constant.showLog(error.message)
                isLoaded = false
                totPages = 0
                binding.pdfview.visibility = View.GONE
                binding.includedProgress.progress.visibility = View.GONE
                binding.txtPageNumbers.visibility = View.GONE
                binding.includedProgress.llError.visibility = View.VISIBLE
                binding.llPdfMenu.visibility = View.GONE
                Constant.showToast(this@PdfActivity, "Something went wrong")
            }
        }
        binding.pdfview.initWithUrl(
            url = stream,
            lifecycleCoroutineScope = lifecycleScope,
            lifecycle = lifecycle
        )
    }


    @SuppressLint("SetTextI18n")
    private fun setPdfFile( stream: String) {

        isLoaded = false
        binding.pdfview.statusListener = object : PdfRendererView.StatusCallBack {
            override fun onPdfLoadSuccess(absolutePath: String) {

            }

            override fun onPdfRenderSuccess() {
                super.onPdfRenderSuccess()
                isLoaded = true
                totPages = binding.pdfview.totalPageCount
                binding.pdfview.visibility = View.VISIBLE
                binding.includedProgress.progress.visibility = View.GONE
                binding.txtPageNumbers.visibility = View.GONE
                binding.includedProgress.llError.visibility = View.GONE
                binding.llPdfMenu.visibility = View.VISIBLE
                checkArrow()
            }

            override fun onPageChanged(currentPage: Int, totalPage: Int) {
                super.onPageChanged(currentPage, totalPage)
                this@PdfActivity.page = currentPage
                binding.txtPageNumbers.text = (page + 1).toString() + " / " + totalPage
                checkArrow()
            }

            override fun onError(error: Throwable) {
                super.onError(error)
                Constant.showLog(error.message)
                isLoaded = false
                totPages = 0
                binding.pdfview.visibility = View.GONE
                binding.includedProgress.progress.visibility = View.GONE
                binding.txtPageNumbers.visibility = View.GONE
                binding.includedProgress.llError.visibility = View.VISIBLE
                binding.llPdfMenu.visibility = View.GONE
                Constant.showToast(this@PdfActivity, "Something went wrong")
            }
        }
        binding.pdfview.initWithUri(
            uri = "file://$stream".toUri(),
        )
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