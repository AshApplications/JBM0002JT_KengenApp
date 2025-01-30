package com.water.alkaline.kengen.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.applovin.sdk.AppLovinSdk
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gms.ads.AdLoader
import com.google.gms.ads.MyApp
import com.preference.PowerPreference
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel
import com.water.alkaline.kengen.databinding.ActivityHomeBinding
import com.water.alkaline.kengen.databinding.DialogInfoBinding
import com.water.alkaline.kengen.library.toprightmenu.MenuItem
import com.water.alkaline.kengen.library.toprightmenu.TopRightMenu
import com.water.alkaline.kengen.model.main.Category
import com.water.alkaline.kengen.ui.activity.DownloadActivity
import com.water.alkaline.kengen.ui.activity.SaveActivity
import com.water.alkaline.kengen.ui.adapter.ViewPagerFragmentAdapter
import com.water.alkaline.kengen.ui.feedback.FeedbackActivity
import com.water.alkaline.kengen.ui.feedback.FeedbackViewModel
import com.water.alkaline.kengen.ui.home.fragment.BannerFragment
import com.water.alkaline.kengen.ui.home.fragment.ChannelFragment
import com.water.alkaline.kengen.ui.home.fragment.ChannelFragment.Companion.newInstance
import com.water.alkaline.kengen.ui.home.fragment.PdfFragment
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.uiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]
    }
    private var strings: MutableList<String> = arrayListOf()
    private var pagerFragmentAdapter: ViewPagerFragmentAdapter? = null
    private var savedState: Bundle? = null

    override fun onResume() {
        super.onResume()
        if (MyApp.getAdModel().adsOnOff.equals("Yes", ignoreCase = true)) {
            if (binding.includedAd.flAd.childCount <= 0) {
                AdLoader.getInstance().showUniversalAd(this, binding.includedAd, true)
            }
        } else {
            binding.includedAd.cvAdMain.visibility = View.GONE
            binding.includedAd.flAd.visibility = View.GONE
            refreshFragments()
        }
    }

    private fun setup(activity: Activity, view: View) {

        val menu = TopRightMenu(activity)

        val menuItems: MutableList<MenuItem> = arrayListOf()
        menuItems.add(MenuItem(R.mipmap.info, "Disclaimer"))
        menuItems.add(MenuItem(R.mipmap.feedback, "Feedback"))
        menuItems.add(MenuItem(R.mipmap.share, "Share App"))
        menuItems.add(MenuItem(R.mipmap.thumb, "Rate Us"))

        menu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
            .showIcon(true)
            .dimBackground(true)
            .needAnimationStyle(true)
            .setAnimationStyle(R.style.TRM_ANIM_STYLE)
            .addMenuList(menuItems)
            .setOnMenuItemClickListener { position ->
                when (position) {
                    0 -> showInfoDialog()
                    1 -> uiController.gotoActivity(
                        this@HomeActivity,
                        FeedbackActivity::class.java,
                        true,
                        false
                    )

                    2 -> shareApp()
                    3 -> rateUs()
                }
                menu.dismiss()
            }

        menu.showAsDropDown(view, 0, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        savedState = savedInstanceState
        if (PowerPreference.getDefaultFile().getBoolean("mCheckFirst", true)) {
            PowerPreference.getDefaultFile().putBoolean("mCheckFirst", false)
            showInfoDialog()
        }
        AppLovinSdk.getInstance(this).showMediationDebugger()
        listener()
        setTabs()
    }

    private fun rateUs() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    private fun shareApp() {
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.setType("text/plain")
            i.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))


            var sAux = PowerPreference.getDefaultFile().getString(Constant.appShareMsg, "")

            val sAux2 = "https://play.google.com/store/apps/details?id=$packageName"
            sAux = """
                $sAux
                
                $sAux2
                """.trimIndent()
            i.putExtra(Intent.EXTRA_TEXT, sAux)

            startActivity(Intent.createChooser(i, "Choose One"))
        } catch (e: Exception) {
            e.printStackTrace()
            Constant.showToast(this@HomeActivity, "Something went wrong")
        }
    }

    private fun listener() {
        binding.ivHome.setOnClickListener {
            if (!binding!!.drawer.isDrawerOpen(GravityCompat.START)) binding.drawer.openDrawer(
                GravityCompat.START
            )
            else binding.drawer.closeDrawer(GravityCompat.START)
        }

        binding.ivInfo.setOnClickListener { setup(this@HomeActivity, binding.ivInfo) }

        binding.vpHome.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.navView.menu.getItem(position).setChecked(true)
            }
        })
        binding.navView.setNavigationItemSelectedListener { item ->
            if (item.itemId < 10000) {
                binding.navView.setCheckedItem(item.itemId)
                binding.vpHome.currentItem = item.order
                binding.drawer.closeDrawer(GravityCompat.START)
            } else {
                binding.drawer.closeDrawer(GravityCompat.START)
                if (item.itemId in 10001..10003) {
                    if (item.itemId == 10001) {
                        uiController.gotoActivity(
                            this@HomeActivity,
                            SaveActivity::class.java,
                            true,
                            false
                        )
                    } else if (item.itemId == 10002) {
                        uiController.gotoActivity(
                            this@HomeActivity,
                            DownloadActivity::class.java,
                            true,
                            false
                        )
                    } else if (item.itemId == 10003) {
                        uiController.gotoActivity(
                            this@HomeActivity,
                            FeedbackActivity::class.java,
                            true,
                            false
                        )
                    }
                } else if (item.itemId == 10004) {
                    shareApp()
                }
            }
            false
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        AdLoader.showExit(this)
    }


    private fun showInfoDialog() {
        try {
            val mDialog = Dialog(this, R.style.NormalDialog)
            val binding = DialogInfoBinding.inflate(
                layoutInflater
            )
            mDialog.setContentView(binding.root)
            mDialog.setCancelable(false)
            mDialog.setCanceledOnTouchOutside(false)
            mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mDialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            mDialog.show()
            binding.txtError.text = PowerPreference.getDefaultFile()
                .getString(Constant.mNotice, resources.getString(R.string.kk_info))
            binding.txtRetry.setOnClickListener { v: View? -> mDialog.dismiss() }
        } catch (e: Exception) {
            Log.w("Catch", e.message!!)
        }
    }


    private fun refreshFragments() {
        for (i in 0 until pagerFragmentAdapter!!.itemCount) {
            if (pagerFragmentAdapter!!.arrayList[i] is BannerFragment) {
                (pagerFragmentAdapter!!.arrayList[i] as BannerFragment).refreshFragment()
            } else if (pagerFragmentAdapter!!.arrayList[i] is ChannelFragment) {
                (pagerFragmentAdapter!!.arrayList[i] as ChannelFragment).refreshFragment()
            } else if (pagerFragmentAdapter!!.arrayList[i] is PdfFragment) {
                (pagerFragmentAdapter!!.arrayList[i] as PdfFragment).refreshFragment()
            }
        }
    }

    private fun setTabs() {
        val categories: List<Category> = viewModel.allCategory
        val menu = binding.navView.menu
        var position = 0

        pagerFragmentAdapter = ViewPagerFragmentAdapter(supportFragmentManager, lifecycle)
        for (i in categories.indices) {
            position = i
            strings.add(categories[i].name)
            menu.add(0, categories[i].id.toInt(), i, categories[i].name).setCheckable(true)
            when (categories[i].type) {
                0 -> pagerFragmentAdapter!!.addFragment(
                    newInstance(
                        categories[i].id, "Home", false
                    ), categories[i].name
                )

                1 -> pagerFragmentAdapter!!.addFragment(
                    PdfFragment.newInstance(
                        categories[i].id
                    ), categories[i].name
                )

                2 -> pagerFragmentAdapter!!.addFragment(
                    BannerFragment.newInstance(
                        categories[i].id
                    ), categories[i].name
                )

                else -> {}
            }
        }

        position++
        menu.add(0, 10001, position, "Favorites").setCheckable(false)

        position++
        menu.add(0, 10002, position, "Downloads").setCheckable(false)

        position++
        menu.add(0, 10003, position, "Feedback").setCheckable(false)

        position++
        menu.add(0, 10004, position, "Share App").setCheckable(false)

        binding.vpHome.adapter = pagerFragmentAdapter
        if (pagerFragmentAdapter!!.itemCount > 0) {
            binding.vpHome.offscreenPageLimit = pagerFragmentAdapter!!.itemCount

            binding.includedProgress.progress.visibility = View.GONE
            binding.includedProgress.llError.visibility = View.GONE
        } else {
            binding.includedProgress.progress.visibility = View.GONE
            binding.includedProgress.llError.visibility = View.VISIBLE
        }
        TabLayoutMediator(binding!!.tabHome, binding.vpHome) { tab, position ->
            tab.setText(
                pagerFragmentAdapter!!.mFragmentTitleList[position]
            )
        }.attach()
    }
}