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
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel
import com.water.alkaline.kengen.databinding.FragmentPdfBinding
import com.water.alkaline.kengen.model.main.Pdf
import com.water.alkaline.kengen.ui.activity.HomeActivity
import com.water.alkaline.kengen.ui.activity.PdfActivity
import com.water.alkaline.kengen.ui.adapter.PdfAdapter
import com.water.alkaline.kengen.ui.base.BaseFragment
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.delayTask
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PdfFragment : BaseFragment {

    private val binding by lazy {
        FragmentPdfBinding.inflate(layoutInflater)
    }
    private lateinit var activity: Activity
    private lateinit var mParam1: String
    
    var list: MutableList<Pdf> = mutableListOf()
  
    private lateinit var adapter: PdfAdapter
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
    
    fun setAdapter()
    {
        adapter = PdfAdapter(activity, list) { position: Int, item: Pdf ->
            uiController.gotoIntent(
                activity,
                Intent(activity, PdfActivity::class.java).putExtra("mpath", item.url),
                true,
                false
            )
        }
        val manager = GridLayoutManager(activity, 2)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return when (adapter.getItemViewType(i)) {
                    Constant.STORE_TYPE -> 1
                    Constant.AD_TYPE -> 2
                    Constant.LOADING -> 1
                    else -> 1

                }
            }
        }
        binding.rvPdfs.layoutManager = manager
        binding.rvPdfs.adapter = adapter
        binding.rvPdfs.setItemViewCacheSize(100)
    }

    fun refreshFragment() {
        delayTask(500) {
            adapter.refreshAdapter(appViewModel.getAllPdfByCategory(mParam1))
            binding.includedProgress.progress.visibility = View.GONE
            checkData()
        }
    }

    private fun checkData() {
        if (binding.rvPdfs.adapter != null && binding.rvPdfs.adapter!!.itemCount > 0) {
            binding.includedProgress.llError.visibility = View.GONE
        } else {
            binding.includedProgress.llError.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        @JvmStatic
        fun newInstance(activity: Activity, param1: String): PdfFragment {
            val fragment = PdfFragment(activity)
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }
}