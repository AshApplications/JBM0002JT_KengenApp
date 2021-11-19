package com.water.alkaline.kengen.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.DialogDownloadBinding;
import com.water.alkaline.kengen.databinding.DialogInternetBinding;
import com.water.alkaline.kengen.databinding.DialogJumpBinding;
import com.water.alkaline.kengen.library.downloader.Error;
import com.water.alkaline.kengen.library.downloader.OnDownloadListener;
import com.water.alkaline.kengen.library.downloader.OnProgressListener;
import com.water.alkaline.kengen.library.downloader.PRDownloader;
import com.water.alkaline.kengen.library.downloader.Progress;
import com.water.alkaline.kengen.library.pdfviewer.listener.OnErrorListener;
import com.water.alkaline.kengen.library.pdfviewer.listener.OnLoadCompleteListener;
import com.water.alkaline.kengen.library.pdfviewer.listener.OnPageChangeListener;
import com.water.alkaline.kengen.library.pdfviewer.scroll.DefaultScrollHandle;
import com.water.alkaline.kengen.library.pdfviewer.util.FitPolicy;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.databinding.ActivityPdfBinding;
import com.water.alkaline.kengen.model.DownloadEntity;
import com.water.alkaline.kengen.model.main.Pdf;
import com.water.alkaline.kengen.utils.Constant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class PdfActivity extends AppCompatActivity {

    ActivityPdfBinding binding;
    String mPath = "";

    int page = 0, totPages = 0;
    FitPolicy fitPolicy;
    boolean isLoaded = false;

    boolean nightMode = false;

    InputStream stream;
    Pdf pdf;
    DownloadEntity entity;
    public AppViewModel viewModel;

    public Dialog dialog;

    public Dialog downloadDialog;
    public DialogDownloadBinding downloadBinding;

    public void dismiss_dialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public DialogInternetBinding network_dialog(String text) {
        dialog = new Dialog(this, R.style.NormalDialog);
        DialogInternetBinding binding = DialogInternetBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        binding.txtError.setText(text);
        return binding;
    }

    public void dismiss_download_dialog() {
        if (downloadDialog != null && downloadDialog.isShowing())
            downloadDialog.dismiss();
    }

    public void download_dialog() {
        downloadDialog = new Dialog(this, R.style.NormalDialog);
        downloadBinding = DialogDownloadBinding.inflate(getLayoutInflater());
        downloadDialog.setContentView(downloadBinding.getRoot());
        Objects.requireNonNull(downloadDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        downloadDialog.setCancelable(false);
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        downloadDialog.show();
    }

    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        Glide.with(this).load(R.drawable.bg).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivBG);

        binding.includedToolbar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public void listener() {


        binding.llmenuJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJumpDialog();
            }
        });

        binding.llmenuShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pdf != null) {
                    DownloadEntity entity = null;
                    if (viewModel.getDownloadbyUrl(pdf.getUrl()).size() > 0)
                        entity = viewModel.getDownloadbyUrl(pdf.getUrl()).get(0);

                    if (entity != null) {
                        if (new File(entity.filePath).exists()) {
                            sharePDF(entity.filePath);
                        } else {
                            downloadPDF(true);
                        }
                    } else {
                        downloadPDF(true);
                    }
                } else {

                    if (entity != null) {
                        if (new File(entity.filePath).exists()) {
                            sharePDF(entity.filePath);
                        } else {
                            downloadPDF(true);
                        }
                    } else {
                        downloadPDF(true);
                    }
                }
            }
        });
        binding.ivMenuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page > 0) {
                    binding.pdfview.jumpTo(binding.pdfview.getCurrentPage() - 1, true);
                }
            }
        });

        binding.ivMenuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page < binding.pdfview.getPageCount() - 1) {
                    binding.pdfview.jumpTo(binding.pdfview.getCurrentPage() + 1, true);
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBG();
        mPath = getIntent().getExtras().getString("mpath", "");
        if (mPath.startsWith("http")) {
            pdf = null;
            if (viewModel.getbyUrl(mPath).size() > 0)
                pdf = viewModel.getbyUrl(mPath).get(0);
        } else {
            entity = null;
            if (viewModel.getbyPath(mPath).size() > 0)
                entity = viewModel.getbyPath(mPath).get(0);
        }

        this.fitPolicy = FitPolicy.BOTH;
        listener();
        checkDownload();
        if (mPath.startsWith("http")) {
            loadFromUrl();
        } else {
            setPdfFile(nightMode, FitPolicy.BOTH, mPath);
        }
    }

    public void checkDownload() {
        if (mPath.startsWith("http")) {
            DownloadEntity entity = null;
            if (viewModel.getDownloadbyUrl(pdf.getUrl()).size() > 0)
                entity = viewModel.getDownloadbyUrl(pdf.getUrl()).get(0);

            if (entity != null) {
                binding.ivDownload.setVisibility(View.GONE);
                binding.ivShare.setVisibility(View.VISIBLE);
            } else {
                binding.ivDownload.setVisibility(View.VISIBLE);
                binding.ivShare.setVisibility(View.GONE);
            }
        } else {
            binding.ivDownload.setVisibility(View.GONE);
            binding.ivShare.setVisibility(View.VISIBLE);
        }
    }

    public void loadFromUrl() {
        if (Constant.checkInternet(this)) {
            new RetrivePDFfromUrl().execute(mPath);
        } else {
            network_dialog(getResources().getString(R.string.error_internet)).txtRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss_dialog();
                    if (Constant.checkInternet(PdfActivity.this)) {
                        loadFromUrl();
                    } else dialog.show();
                }
            });
        }
    }

    public void checkArrow() {
        if (binding.pdfview.getCurrentPage() == 0) {
            binding.ivMenuLeft.setVisibility(View.GONE);
        } else {
            binding.ivMenuLeft.setVisibility(View.VISIBLE);
        }

        if (binding.pdfview.getCurrentPage() == binding.pdfview.getPageCount() - 1) {
            binding.ivMenuRight.setVisibility(View.GONE);
        } else {
            binding.ivMenuRight.setVisibility(View.VISIBLE);
        }

    }


    public void showJumpDialog() {
        DialogJumpBinding binding = DialogJumpBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(PdfActivity.this, R.style.NormalDialog);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        binding.editJump.setText(PdfActivity.this.binding.pdfview.getCurrentPage() + 1 + "");
        binding.txtJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (binding.editJump.getText().toString().equalsIgnoreCase(""))
                        binding.editJump.setError("Enter Page Number");
                    else if (Integer.parseInt(binding.editJump.getText().toString()) > totPages || Integer.parseInt(binding.editJump.getText().toString()) == 0)
                        binding.editJump.setError("Page Not Available");
                    else {
                        dialog.dismiss();
                        PdfActivity.this.binding.pdfview.jumpTo(Integer.parseInt(binding.editJump.getText().toString()) - 1);
                    }
                } catch (Exception e) {
                    Log.e("TAG", e.toString());
                    Constant.showToast(PdfActivity.this, "Something went wrong");
                }

            }
        });


        binding.txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            stream = inputStream;
            setPdfUrl(nightMode, FitPolicy.BOTH, stream);
        }
    }

    public void setPdfUrl(boolean nightMode, FitPolicy fitPolicy, InputStream stream) {
        isLoaded = false;
        binding.pdfview.fromStream(stream)
                .password(null)
                .enableAnnotationRendering(true)
                .pageFitPolicy(fitPolicy)
                .defaultPage(page)
                .swipeHorizontal(true)
                .autoSpacing(true)
                .scrollHandle(new DefaultScrollHandle(PdfActivity.this))
                .pageFling(true)
                .pageSnap(false)
                .nightMode(nightMode)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        PdfActivity.this.page = page;
                        binding.txtPageNumbers.setText((page + 1) + " / " + pageCount);
                        checkArrow();
                    }
                }).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {
                isLoaded = true;
                totPages = nbPages;
                binding.pdfview.setVisibility(View.VISIBLE);
                binding.includedProgress.progress.setVisibility(View.GONE);
                binding.txtPageNumbers.setVisibility(View.VISIBLE);
                binding.includedProgress.llError.setVisibility(View.GONE);
                binding.cvBottom.setVisibility(View.VISIBLE);
                checkArrow();
            }
        }).onError(new OnErrorListener() {
            @Override
            public void onError(Throwable t) {
                Constant.showLog(t.getMessage());
                isLoaded = false;
                totPages = 0;
                binding.pdfview.setVisibility(View.GONE);
                binding.includedProgress.progress.setVisibility(View.GONE);
                binding.txtPageNumbers.setVisibility(View.GONE);
                binding.includedProgress.llError.setVisibility(View.VISIBLE);
                binding.cvBottom.setVisibility(View.GONE);
                Constant.showToast(PdfActivity.this, "Something went wrong");
            }
        }).load();
    }


    public void setPdfFile(boolean nightMode, FitPolicy fitPolicy, String stream) {
        isLoaded = false;
        binding.pdfview.fromUri(Uri.parse("file://" + stream))
                .password(null)
                .enableAnnotationRendering(true)
                .pageFitPolicy(fitPolicy)
                .defaultPage(page)
                .autoSpacing(true)
                .swipeHorizontal(true)
                .scrollHandle(new DefaultScrollHandle(PdfActivity.this))
                .pageFling(true)
                .pageSnap(false)
                .nightMode(nightMode)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        PdfActivity.this.page = page;
                        binding.txtPageNumbers.setText((page + 1) + " / " + pageCount);
                        checkArrow();
                    }
                }).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {
                isLoaded = true;
                totPages = nbPages;
                binding.pdfview.setVisibility(View.VISIBLE);
                binding.includedProgress.progress.setVisibility(View.GONE);
                binding.txtPageNumbers.setVisibility(View.VISIBLE);
                binding.includedProgress.llError.setVisibility(View.GONE);
                binding.cvBottom.setVisibility(View.VISIBLE);
                checkArrow();
            }
        }).onError(new OnErrorListener() {
            @Override
            public void onError(Throwable t) {
                Constant.showLog(t.getMessage());
                isLoaded = false;
                totPages = 0;
                binding.pdfview.setVisibility(View.GONE);
                binding.includedProgress.progress.setVisibility(View.GONE);
                binding.txtPageNumbers.setVisibility(View.GONE);
                binding.includedProgress.llError.setVisibility(View.VISIBLE);
                binding.cvBottom.setVisibility(View.GONE);
                Constant.showToast(PdfActivity.this, "Something went wrong");
            }
        }).load();
    }

    public void downloadPDF(boolean isShare) {
        download_dialog();
        String filename = "file" + System.currentTimeMillis() + ".pdf";
        String url = "";
        if (pdf != null) {
            url = pdf.getUrl();
        } else {
            url = entity.url;
        }
        PRDownloader.download(url, Constant.getPDFdisc(), filename)
                .setTag(Integer.parseInt(pdf.getId()))
                .build()
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        downloadBinding.txtvlu.setText("Downloading " + (int) (((double) progress.currentBytes / progress.totalBytes) * 100.0) + " %");
                    }
                }).start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                dismiss_download_dialog();
                Constant.showToast(PdfActivity.this, "Download Completes");
                DownloadEntity entity = new DownloadEntity(pdf.getName(), Constant.getPDFdisc() + "/" + filename, pdf.getImage(), pdf.getUrl(), Constant.TYPE_PDF);
                viewModel.insertDownloads(entity);
                checkDownload();
            }

            @Override
            public void onError(Error error) {
                Constant.showToast(PdfActivity.this, "Something went wrong");
                dismiss_download_dialog();
            }
        });

        downloadBinding.txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PRDownloader.cancel(Integer.parseInt(pdf.getId()));
                dismiss_download_dialog();
            }
        });
    }

    public void sharePDF(String mPath) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("application/pdf");
            i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));

            String sAux = "I recommend Kengen app which gives you all the information like Kengen Demo, Business Plan, Results Photo & Videos, Events video, Motivational video, Forms, PPT, PDF etc.\n" +
                    "\n" +
                    "So Download & share this app into your growth line, leaders and work easily and smartly and achieve your target as soon as possible. \n\n";

            String sAux2 = "https://play.google.com/store/apps/details?id=" + getPackageName();
            sAux = sAux + sAux2;
            Uri fileUri = FileProvider.getUriForFile(getApplicationContext(),
                    getPackageName() + ".fileprovider", new File(mPath));
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.putExtra(Intent.EXTRA_STREAM, fileUri);
            startActivity(Intent.createChooser(i, "Choose One"));
        } catch (Exception e) {
            e.printStackTrace();
            Constant.showToast(PdfActivity.this, "Something went wrong");
        }

    }
}