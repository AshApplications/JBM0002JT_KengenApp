package com.water.alkaline.kengen.ui.home.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.preference.PowerPreference
import com.water.alkaline.kengen.BuildConfig
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt
import com.water.alkaline.kengen.MyApplication
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel
import com.water.alkaline.kengen.databinding.FragmentChannelBinding
import com.water.alkaline.kengen.model.NetworkResult
import com.water.alkaline.kengen.model.SaveEntity
import com.water.alkaline.kengen.model.channel.ChannelResponse
import com.water.alkaline.kengen.model.channel.PlaylistResponse
import com.water.alkaline.kengen.model.main.Channel
import com.water.alkaline.kengen.model.main.Subcategory
import com.water.alkaline.kengen.model.update.UpdateResponse
import com.water.alkaline.kengen.ui.home.HomeViewModel
import com.water.alkaline.kengen.ui.activity.ChannelActivity
import com.water.alkaline.kengen.ui.home.HomeActivity
import com.water.alkaline.kengen.ui.activity.PreviewActivity
import com.water.alkaline.kengen.ui.adapter.ChannelAdapter
import com.water.alkaline.kengen.ui.adapter.SubcatAdapter
import com.water.alkaline.kengen.ui.adapter.VideosAdapter
import com.water.alkaline.kengen.ui.base.BaseFragment
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.delayTask
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody

@AndroidEntryPoint
class ChannelFragment : BaseFragment() {

    private var appViewModel: AppViewModel? = null
    private var homeViewModel: HomeViewModel? = null

    private var isChannel: Boolean = true
    private var pageToken = ""
    private var channelId = ""

    private var subList: MutableList<Subcategory> = mutableListOf()
    private var chanList: MutableList<Channel> = mutableListOf()
    private var videoList: MutableList<SaveEntity> = mutableListOf()

    private var channelAdapter: ChannelAdapter? = null
    private var subcatAdapter: SubcatAdapter? = null
    private var videosAdapter: VideosAdapter? = null

    private var mParam1: String? = null
    private var mParam2: String? = null

    private val binding by lazy {
        FragmentChannelBinding.inflate(layoutInflater)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.setMainContext()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1, "")
            mParam2 = requireArguments().getString(ARG_PARAM2, "")
            isChannel = requireArguments().getBoolean(ARG_PARAM3, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanseState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
        if (mParam2.equals("Home", ignoreCase = true)) {
            fromSubCategory
        } else if (mParam2.equals("Channel", ignoreCase = true)) {
            fromChannel
        } else if (mParam2.equals("Video", ignoreCase = true)) {
            fromVideo
        }
    }

    private fun bindObservers() {
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeViewModel!!.updateData.observe(viewLifecycleOwner) {
            if (it is NetworkResult.Success) {
                try {
                    val updateResponse = GsonBuilder().create().fromJson(
                        (DecryptEncrypt.DecryptStr(
                            appContext, it.data!!.string()
                        )), UpdateResponse::class.java
                    )
                    if (updateResponse != null) {
                        val appInfo = updateResponse.data.appInfo[0]
                        PowerPreference.getDefaultFile()
                            .putString(Constant.mKeyId, appInfo.apiKey)
                        getVideoData()
                        return@observe
                    }
                    updateError("Something went Wrong")
                } catch (e: Exception) {
                    e.printStackTrace()
                    updateError("Something went Wrong")
                }

            } else {
                Constant.showLog(it.message)
                updateError("Something went Wrong")
            }
        }

        homeViewModel!!.videoData.observe(viewLifecycleOwner) {
            if (it is NetworkResult.Success) {
                try {
                    parseData(it.data!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Constant.showToast(appContext, "Something went Wrong")
                }
            } else {
                if (it.message.equals(
                        "quotaExceeded",
                        ignoreCase = true
                    ) ||
                    it.message.equals(
                        "forbidden",
                        ignoreCase = true
                    )
                ) {
                    updateAPI()
                } else {
                    Constant.showToast(appContext, "Something went Wrong")
                    showError("Something went Wrong")
                }
            }
        }
    }

    private fun parseData(responseBody: ResponseBody) {

        if (videosAdapter != null && videosAdapter!!.arrayList.size != 0 && videosAdapter!!.arrayList[videosAdapter!!.arrayList.size - 1].videoId.equals(
                "99999",
                ignoreCase = true
            )
        ) {
            videosAdapter!!.arrayList.removeAt(videosAdapter!!.arrayList.size - 1)
            videosAdapter!!.notifyItemRemoved(videosAdapter!!.arrayList.size)
        }

        if (isChannel) {
            val channelResponse =
                Gson().fromJson(responseBody.string(), ChannelResponse::class.java)

            if (channelResponse != null) {
                if (channelResponse.nextPageToken != null && !channelResponse.nextPageToken.equals(
                        "",
                        ignoreCase = true
                    )
                ) {
                    pageToken = channelResponse.nextPageToken ?: ""
                } else {
                    pageToken = ""
                }

                channelResponse.items?.forEach {
                    if (!it.snippet!!.title.equals(
                            "Private video",
                            ignoreCase = true
                        ) && !it.snippet!!.title.equals(
                            "Deleted video",
                            ignoreCase = true
                        )
                    ) {
                        val url =
                            "http://i.ytimg.com/vi/" + it.id!!.videoId + "/hqdefault.jpg"
                        val entity = SaveEntity(
                            it.id!!.videoId!!,
                            it.snippet!!.title,
                            it.snippet!!.title,
                            url
                        )
                        videoList.add(entity)
                    }
                }
                refreshActivity()
            } else {
                showError("Something went Wrong")
            }

        } else {
            val playlistResponse =
                Gson().fromJson(responseBody.string(), PlaylistResponse::class.java)

            if (playlistResponse != null) {

                if (playlistResponse.nextPageToken != null && !playlistResponse.nextPageToken.equals(
                        "",
                        ignoreCase = true
                    )
                ) {
                    pageToken = playlistResponse.nextPageToken
                } else {
                    pageToken = ""
                }

                playlistResponse.items?.forEach {
                    if (!it.snippet!!.title.equals(
                            "Private video",
                            ignoreCase = true
                        ) && !it.snippet!!.title.equals(
                            "Deleted video",
                            ignoreCase = true
                        )
                    ) {
                        val url =
                            "http://i.ytimg.com/vi/" + it.snippet.resourceId.videoId + "/hqdefault.jpg"
                        val entity = SaveEntity(
                            it.snippet.resourceId.videoId,
                            it.snippet.title,
                            it.snippet.title,
                            url
                        )
                        videoList.add(entity)
                    }
                }
                refreshActivity()
            } else {
                showError("Something went Wrong")
            }
        }
    }


    private val fromSubCategory: Unit
        get() {
            if (appContext != null) {
                subList = appViewModel!!.getAllSubByCategory(mParam1!!)
                if (subList.size > 1) {
                    showSubCategory()
                } else if (subList.size == 1) {
                    chanList = appViewModel!!.getAllChannelByCategory(subList[0].id)
                    if (chanList.size > 1) {
                        showChannels()
                    } else if (chanList.size == 1) {
                        channelId = chanList[0].youid
                        isChannel = chanList[0].type.equals("0", ignoreCase = true)
                        showVideos()
                    }
                }
            }
        }

    private val fromChannel: Unit
        get() {
            if (appContext != null) {
                chanList = appViewModel!!.getAllChannelByCategory(mParam1!!)
                if (chanList.size > 1) {
                    showChannels()
                } else if (chanList.size == 1) {
                    channelId = chanList[0].youid
                    isChannel = chanList[0].type.equals("0", ignoreCase = true)
                    showVideos()
                }
            }
        }

    private val fromVideo: Unit
        get() {
            if (appContext != null) {
                channelId = mParam1.toString()
                showVideos()
            }
        }

    fun refreshFragment() {
        if (subList.isNotEmpty() && subcatAdapter != null) {
            subcatAdapter!!.refreshAdapter(subList)
        } else if (chanList.isNotEmpty() && channelAdapter != null) {
            channelAdapter!!.refreshAdapter(chanList)
        } else if (videoList.isNotEmpty() && videosAdapter != null) {
            videosAdapter!!.refreshAdapter(videoList)
        }
    }

    private fun showSubCategory() {
        subcatAdapter = SubcatAdapter(appContext, subList) { position, item ->
            uiController.gotoIntent(
                if (appContext is HomeActivity)
                    appContext as HomeActivity
                else
                    appContext as ChannelActivity,
                Intent(appContext, ChannelActivity::class.java).putExtra("catId", item.id),
                true,
                false
            )
        }
        val manager = GridLayoutManager(appContext, 2)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return when (subcatAdapter!!.getItemViewType(i)) {
                    Constant.STORE_TYPE -> 1
                    Constant.AD_TYPE -> 2
                    Constant.LOADING -> 1
                    else -> 1

                }
            }
        }
        binding.rvCats.layoutManager = manager
        binding.rvCats.adapter = subcatAdapter
        binding.rvCats.setItemViewCacheSize(100)
        checkData()
    }

    private fun showChannels() {
        channelAdapter = ChannelAdapter(appContext, chanList) { position, item ->
            uiController.gotoIntent(
                if (appContext is HomeActivity)
                    appContext as HomeActivity
                else
                    appContext as ChannelActivity,
                Intent(appContext, ChannelActivity::class.java).putExtra("catId", item.youid)
                    .putExtra("isChannel", item.type.equals("0", ignoreCase = true)),
                true,
                false
            )
        }
        val manager = GridLayoutManager(appContext, 2)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return when (channelAdapter!!.getItemViewType(i)) {
                    Constant.STORE_TYPE -> 1
                    Constant.AD_TYPE -> 2
                    Constant.LOADING -> 1
                    else -> 1

                }
            }
        }
        binding.rvCats.layoutManager = manager
        binding.rvCats.adapter = channelAdapter
        binding.rvCats.setItemViewCacheSize(100)
        checkData()
    }

    private fun showVideos() {
        videosAdapter =
            VideosAdapter(appContext, videoList, binding.rvCats) { position, item ->
                videoList.removeAll {
                    it.videoId.equals(Constant.defaultId)
                }
                val pos = videoList.indexOfFirst { it.videoId.equals(item.videoId) }
                PowerPreference.getDefaultFile().putString(Constant.mList, Gson().toJson(videoList))
                uiController.gotoIntent(
                    if (appContext is HomeActivity)
                        appContext as HomeActivity
                    else
                        appContext as ChannelActivity,
                    Intent(appContext, PreviewActivity::class.java).putExtra(
                        Constant.POSITION, pos
                    ),
                    true,
                    false
                )
            }
        val manager = GridLayoutManager(appContext, 2)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return when (videosAdapter!!.getItemViewType(i)) {
                    Constant.STORE_TYPE -> 1
                    Constant.AD_TYPE -> 2
                    Constant.LOADING -> 1
                    else -> 1

                }
            }
        }
        binding.rvCats.layoutManager = manager
        binding.rvCats.adapter = videosAdapter
        binding.rvCats.setItemViewCacheSize(100)
        videosAdapter!!.setOnLoadMoreListener {
            try {
                videosAdapter!!.arrayList.add(SaveEntity("99999", null, null, null))
                videosAdapter!!.notifyItemInserted(videosAdapter!!.arrayList.size - 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delayTask {
                if (!pageToken.equals("", ignoreCase = true)
                ) {
                    getVideoData()
                } else {
                    videosAdapter!!.arrayList.removeAt(videosAdapter!!.arrayList.size - 1)
                    videosAdapter!!.notifyItemRemoved(videosAdapter!!.arrayList.size)
                }
            }
        }

        delayTask {
            pageToken = ""
            getVideoData()
        }
    }

    private fun refreshActivity() {
        videosAdapter!!.refreshAdapter(videoList)
        checkData()
    }

    private fun checkData() {
        binding.includedProgress.progress.visibility = View.GONE
        if (binding.rvCats.adapter != null && binding.rvCats.adapter!!.itemCount > 0) {
            binding.includedProgress.llError.visibility = View.GONE
        } else {
            binding.includedProgress.llError.visibility = View.VISIBLE
        }
    }

    private fun getVideoData() {
        if (Constant.checkInternet(appContext)) {
            homeViewModel!!.fetchData(
                PowerPreference.getDefaultFile().getString(
                    Constant.mKeyId
                ), channelId, pageToken, isChannel
            )
        } else {
            showError(appContext.resources.getString(R.string.kk_error_no_internet))
        }
    }

    private fun showError(error: String) {
        binding.includedProgress.cvProError.visibility = View.INVISIBLE
        binding.cvIerror.visibility = View.VISIBLE
        binding.txtError.text = error
        binding.txtRetry.setOnClickListener {
            binding.includedProgress.cvProError.visibility = View.VISIBLE
            binding.cvIerror.visibility = View.GONE
            getVideoData()
        }
    }

    private fun updateError(error: String) {
        binding.includedProgress.cvProError.visibility = View.INVISIBLE
        binding.cvIerror.visibility = View.VISIBLE
        binding.txtError.text = error
        binding.txtRetry.setOnClickListener {
            binding.includedProgress.cvProError.visibility = View.VISIBLE
            binding.cvIerror.visibility = View.GONE
            updateAPI()
        }
    }


    private fun update2Error(error: String) {
        binding.includedProgress.cvProError.visibility = View.INVISIBLE
        binding.cvIerror.visibility = View.VISIBLE
        binding.txtError.text = error
        binding.txtRetry.setOnClickListener {
            binding.includedProgress.cvProError.visibility = View.VISIBLE
            binding.cvIerror.visibility = View.GONE
            getVideoData()
        }
    }


    private fun updateAPI() {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.mIsApi, false)) {
            update2Error("Please wait Sometimes")
            return
        }

        if (Constant.checkInternet(appContext)) {

            @SuppressLint("HardwareIds") val deviceId = Settings.Secure.getString(
                appContext.contentResolver, Settings.Secure.ANDROID_ID
            )

            val token = PowerPreference.getDefaultFile().getString(Constant.Token, "123abc")
            var version = 0

            val manager = appContext.packageManager
            var info: PackageInfo? = null

            try {
                info = manager.getPackageInfo(appContext.packageName, 0)
                version = info.versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                Constant.showLog(e.toString())
                version = BuildConfig.VERSION_CODE
            }

            homeViewModel!!.fetchUpdateData(
                DecryptEncrypt.EncryptStr(
                    appContext, MyApplication.updateApi(
                        appContext,
                        deviceId,
                        token,
                        appContext.packageName,
                        version.toString(),
                        ""
                    )
                )
            )
        } else {
            updateError(appContext.resources.getString(R.string.kk_error_no_internet))
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val ARG_PARAM3 = "param3"

        @JvmStatic
        fun newInstance(
            param1: String,
            param2: String,
            param3: Boolean
        ): ChannelFragment {
            val fragment = ChannelFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            args.putBoolean(ARG_PARAM3, param3)
            fragment.arguments = args
            return fragment
        }
    }
}