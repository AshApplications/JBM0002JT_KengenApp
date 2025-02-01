package com.water.alkaline.kengen.ui.feedback.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.water.alkaline.kengen.databinding.FragmentHistoryBinding
import com.water.alkaline.kengen.model.feedback.Feedback
import com.water.alkaline.kengen.ui.base.BaseFragment
import com.water.alkaline.kengen.ui.feedback.FeedbackActivity
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.FeedBacksEvent
import com.water.alkaline.kengen.utils.NewFeedBackEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@AndroidEntryPoint
class HistoryFragment : BaseFragment() {

    private val binding by lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }
    private var adapter: FeedAdapter? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.setMainContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewFeedBackEvent(event: NewFeedBackEvent) {
        adapter!!.addItem(event.model)
        checkData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFeedBacksEvent(event: FeedBacksEvent) {
        refreshActivity(event.feedbacks)
    }


    private fun setAdapter() {
        val manager = GridLayoutManager(appContext, 1)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return when (adapter!!.getItemViewType(i)) {
                    Constant.STORE_TYPE -> 1
                    Constant.AD_TYPE -> 1
                    Constant.LOADING -> 1
                    else -> 1

                }
            }
        }
        binding.rvFeeds.layoutManager = manager
        adapter = FeedAdapter(
            appContext,
            mutableListOf()
        )
        binding.rvFeeds.adapter = adapter
        binding.rvFeeds.setItemViewCacheSize(100)
    }

    private fun refreshActivity(feedbacks: List<Feedback>) {
        adapter!!.refresh(feedbacks)
        binding.includedProgress.progress.visibility = View.GONE
        checkData()
    }

    @Subscribe

    private fun checkData() {
        if (adapter!!.itemCount > 0) {
            binding.includedProgress.llError.visibility = View.GONE
        } else {
            binding.includedProgress.llError.visibility = View.VISIBLE
        }
    }
}