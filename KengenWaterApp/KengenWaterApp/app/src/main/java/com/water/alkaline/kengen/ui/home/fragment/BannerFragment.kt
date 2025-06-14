package com.water.alkaline.kengen.ui.home.fragment

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
import com.water.alkaline.kengen.ui.home.HomeActivity
import com.water.alkaline.kengen.ui.adapter.BannerAdapter
import com.water.alkaline.kengen.ui.base.BaseFragment
import com.water.alkaline.kengen.ui.feedback.FeedbackViewModel
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.delayTask
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerFragment : BaseFragment() {

    private val binding by lazy {
        FragmentBannerBinding.inflate(layoutInflater)
    }
    private var mParam1: String = Constant.defaultId

    var list: MutableList<Banner> = mutableListOf()

    private var adapter: BannerAdapter? = null
    private var appViewModel: AppViewModel? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.setMainContext()
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

    private fun setAdapter() {
        adapter = BannerAdapter(appContext, list) { position: Int, item: Banner ->
            list.removeAll {
                it.id.equals(Constant.defaultId)
            }
            val pos = list.indexOfFirst { it.id.equals(item.id) }
            PowerPreference.getDefaultFile().putString(Constant.mBanners, Gson().toJson(list))
            val intent = Intent(appContext, BannerActivity::class.java)
            intent.putExtra("POS", pos)
            intent.putExtra("PAGE", Constant.LIVE)
            uiController.gotoIntent((appContext as HomeActivity), intent, true, false)
        }
        val manager = GridLayoutManager(appContext, 2)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return when (adapter!!.getItemViewType(i)) {
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
            list = appViewModel!!.getAllBannerByCategory(mParam1)
            adapter!!.refreshAdapter(list)
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
        fun newInstance(param1: String): BannerFragment {
            val fragment = BannerFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }
}