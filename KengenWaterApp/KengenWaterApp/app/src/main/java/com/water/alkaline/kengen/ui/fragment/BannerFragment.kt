package com.water.alkaline.kengen.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.preference.PowerPreference
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel
import com.water.alkaline.kengen.databinding.FragmentBannerBinding
import com.water.alkaline.kengen.model.main.Banner
import com.water.alkaline.kengen.ui.activity.BannerActivity
import com.water.alkaline.kengen.ui.activity.HomeActivity
import com.water.alkaline.kengen.ui.adapter.BannerAdapter
import com.water.alkaline.kengen.ui.base.BaseFragment
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.delayTask
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerFragment : BaseFragment {

    private val binding by lazy {
        FragmentBannerBinding.inflate(layoutInflater)
    }
    private lateinit var activity: Activity
    private lateinit var mParam1: String

    var list: MutableList<Banner> = mutableListOf()

    private lateinit var adapter: BannerAdapter
    private lateinit var appViewModel: AppViewModel

    constructor()

    constructor(activity: Activity) {
        this.activity = activity
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.setMainContext()
        if (activity == null) {
            activity = context as HomeActivity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1).toString()
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
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        setAdapter()
        refreshFragment()
    }

    fun setAdapter() {
        adapter = BannerAdapter(activity, list) { position: Int, item: Banner ->
            list.removeAll(setOf<Any?>(null))
            var pos = position
            for (i in list.indices) {
                if (list[i].id.equals(item.id, ignoreCase = true)) {
                    pos = i
                    break
                }
            }
            PowerPreference.getDefaultFile().putString(Constant.mBanners, Gson().toJson(list))
            val intent = Intent(activity, BannerActivity::class.java)
            intent.putExtra("POS", pos)
            intent.putExtra("PAGE", Constant.LIVE)
            uiController.gotoIntent(activity, intent, true, false)
        }
        val manager = GridLayoutManager(activity, 2)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return when (adapter.getItemViewType(i)) {
                    Constant.STORE_TYPE -> 1
                    Constant.AD_TYPE -> 2
                    else -> 1

                }
            }
        }
        binding.rvBanners.layoutManager = manager
        binding.rvBanners.adapter = adapter
        binding.rvBanners.setItemViewCacheSize(100)
    }

    fun refreshFragment() {
        delayTask(500) {
            list = appViewModel.getAllBannerByCategory(mParam1)
            adapter.refreshAdapter(list)
            binding.includedProgress.progress.visibility = View.GONE
            checkData()
        }
    }

    private fun checkData() {
        if (binding.rvBanners.adapter != null && binding.rvBanners.adapter!!.itemCount > 0) {
            binding.includedProgress.llError.visibility = View.GONE
        } else {
            binding.includedProgress.llError.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(activity: Activity, param1: String): BannerFragment {
            val fragment = BannerFragment(activity)
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }
}