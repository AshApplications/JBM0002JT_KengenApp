package com.water.alkaline.kengen.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.preference.PowerPreference
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel
import com.water.alkaline.kengen.databinding.ActivityPlayerBinding
import com.water.alkaline.kengen.library.ActionListeners
import com.water.alkaline.kengen.library.ViewToImage
import com.water.alkaline.kengen.model.SaveEntity
import com.water.alkaline.kengen.ui.adapter.VideosAdapter
import com.water.alkaline.kengen.ui.base.BaseActivity
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.RefreshSavedEvent
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.io.File

@AndroidEntryPoint
class PlayerActivity : BaseActivity() {

    private val binding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]
    }

    var mList: MutableList<SaveEntity> = ArrayList()
    var isFullScreen: Boolean = false


    var vPlayer: YouTubePlayer? = null
    private var isPause: Boolean = false

    var position: Int = 0


    private fun onClick() {
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFullScreen) {
                    isFullScreen = false
                    binding.cvToolbar.visibility = View.VISIBLE
                    binding.playerView.layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    uiController.onBackPressed(this@PlayerActivity)
                }
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
        if (intent != null && intent.hasExtra(Constant.POSITION)) {
            position = intent.getIntExtra(Constant.POSITION, 0)
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

        onClick()
        checkLike()
        setSize()
        setAdapter()
        setPlayer()
    }

    private fun setSize() {
        binding.ivShare.setOnClickListener { v: View? -> shareVideo() }
        binding.ivLike.setOnClickListener { v: View? ->
            try {
                val entity = mList[position]
                var entity2: SaveEntity? = null
                if (viewModel.getSaveByVideoId(entity.videoId).isNotEmpty()) entity2 =
                    viewModel.getSaveByVideoId(entity.videoId)[0]

                if (entity2 != null) {
                    viewModel.deleteSaves(entity2)
                    checkLike()
                    EventBus.getDefault().post(RefreshSavedEvent())
                } else {
                    val entity1 =
                        SaveEntity(entity.videoId, entity.title, entity.des, entity.imgUrl)
                    viewModel.insertSaves(entity1)
                    binding.ivLike.speed = 100f
                    binding.ivLike.playAnimation()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun checkLike() {
        try {
            val entity = mList[position]
            var entity2: SaveEntity? = null
            if (viewModel.getSaveByVideoId(entity.videoId).isNotEmpty()) entity2 =
                viewModel.getSaveByVideoId(entity.videoId)[0]

            if (entity2 != null) {
                binding.ivLike.progress = 1f
            } else {
                binding.ivLike.progress = 0f
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapter() {
        val videosAdapter = VideosAdapter(this, mList, null) { pos, item ->
            position = pos
            loadVideo(item.videoId)
        }
        val manager = GridLayoutManager(this, 2)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return when (videosAdapter.getItemViewType(i)) {
                    Constant.STORE_TYPE -> 1
                    Constant.AD_TYPE -> 2
                    Constant.LOADING -> 1
                    else -> 1

                }
            }
        }
        binding.rvVideos.layoutManager = manager
        binding.rvVideos.adapter = videosAdapter
        binding.rvVideos.setItemViewCacheSize(100)
        binding.rvVideos.scrollToPosition(position)

        videosAdapter.refreshAdapter(mList)
        binding.includedProgress.progress.visibility = View.GONE

        if (videosAdapter.itemCount > 0) {
            binding.includedProgress.llError.visibility = View.GONE
        } else {
            binding.includedProgress.llError.visibility = View.VISIBLE
        }
    }

    private fun setPlayer() {
        lifecycle.addObserver(binding.playerView)
        binding.playerView.enableAutomaticInitialization = false
        val options: IFramePlayerOptions = IFramePlayerOptions.Builder().controls(0).rel(0).build()
        binding.playerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                val defaultPlayerUiController = DefaultPlayerUiController(
                    binding.playerView, youTubePlayer
                )
                defaultPlayerUiController.showYouTubeButton(false)
                defaultPlayerUiController.setFullscreenButtonClickListener {
                    if (isFullScreen) {
                        isFullScreen = false
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        binding.cvToolbar.visibility = View.VISIBLE
                        binding.playerView.layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    } else {
                        isFullScreen = true
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        binding.cvToolbar.visibility = View.GONE
                        binding.playerView.layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                }
                binding.playerView.setCustomPlayerUi(defaultPlayerUiController.rootView)
                vPlayer = youTubePlayer
                vPlayer!!.addListener(object : AbstractYouTubePlayerListener() {
                    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                        super.onError(youTubePlayer, error)
                        Log.e("TAG", error.toString())
                        Constant.showToast(this@PlayerActivity, "Something went wrong")
                        nextVideo()
                    }


                    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerState) {
                        super.onStateChange(youTubePlayer, state)
                        if (state == PlayerState.ENDED) {
                            nextVideo()
                        }
                    }
                })
                loadVideo(mList[position].videoId)
            }
        }, options)
    }


    fun nextVideo() {
        val mPos = position
        if (mPos + 1 < mList.size) {
            position = position + 1
            loadVideo(mList[position].videoId)
        } else {
            Constant.showToast(this@PlayerActivity, "Completed All Videos")
            vPlayer!!.seekTo(0f)
            vPlayer!!.pause()
        }
    }

    fun loadVideo(id: String?) {
        if (vPlayer != null) {
            checkLike()
            try {
                vPlayer!!.loadVideo(id!!, 0f)
            } catch (e: IllegalStateException) {
                setPlayer()
            }
        } else {
            Constant.showToast(this@PlayerActivity, "VideoPlayer not loaded")
        }
    }

    override fun onPause() {
        super.onPause()
        if (vPlayer != null) {
            try {
                vPlayer!!.pause()
                isPause = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPause) {
            isPause = false
            if (vPlayer != null) {
                try {
                    vPlayer!!.play()
                } catch (e: Exception) {
                    e.printStackTrace()
                    setPlayer()
                }
            } else {
                setPlayer()
            }
        }
    }

    private fun shareVideo() {
       showLoadingDialog()
        var path: String? = ""

        path = mList[position].imgUrl

        Glide.with(this).asBitmap().load(path)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    saveBitmap(resource)
                }


                override fun onLoadCleared(placeholder: Drawable?) {
                    Toast.makeText(this@PlayerActivity, "Something went Wrong", Toast.LENGTH_SHORT)
                        .show()
                   hideLoadingDialog()
                }
            })
    }

    fun saveBitmap(bitmap: Bitmap?) {
        ViewToImage(this@PlayerActivity, bitmap, object : ActionListeners {
            override fun convertedWithSuccess(var1: Bitmap, var2: String) {
                hideLoadingDialog()
                shareIImage(var2)
            }

            override fun convertedWithError(var1: String) {
                Toast.makeText(this@PlayerActivity, "Something went Wrong", Toast.LENGTH_SHORT)
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

            val title = mList[position].title
            var sAux = PowerPreference.getDefaultFile().getString(Constant.vidShareMsg, "")

            val sAux2 = "https://play.google.com/store/apps/details?id=$packageName"
            sAux = """
                $title
                
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
            Toast.makeText(this@PlayerActivity, "Something went Wrong", Toast.LENGTH_SHORT).show()
          }
    }
}