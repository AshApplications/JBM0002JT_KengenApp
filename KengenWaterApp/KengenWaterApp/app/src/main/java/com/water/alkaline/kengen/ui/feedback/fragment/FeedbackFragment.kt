package com.water.alkaline.kengen.ui.feedback.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.preference.PowerPreference
import com.water.alkaline.kengen.Encrypt.DecryptEncrypt
import com.water.alkaline.kengen.MyApplication
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.data.network.RetroClient
import com.water.alkaline.kengen.databinding.FragmentFeedbackBinding
import com.water.alkaline.kengen.library.ViewAnimator.ViewAnimator
import com.water.alkaline.kengen.model.NetworkResult
import com.water.alkaline.kengen.model.feedback.FeedbackResponse
import com.water.alkaline.kengen.ui.feedback.FeedbackActivity
import com.water.alkaline.kengen.ui.base.BaseFragment
import com.water.alkaline.kengen.ui.feedback.FeedbackViewModel
import com.water.alkaline.kengen.utils.Constant
import com.water.alkaline.kengen.utils.FeedBacksEvent
import com.water.alkaline.kengen.utils.NewFeedBackEvent
import com.water.alkaline.kengen.utils.showNetworkDialog
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class FeedbackFragment : BaseFragment(), OnRatingBarChangeListener {

    private val binding by lazy {
        FragmentFeedbackBinding.inflate(layoutInflater)
    }
    private var isAnimated: Boolean = false
    private lateinit var feedbackViewModel: FeedbackViewModel

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
        initializeUI()
        bindObservers()
    }

    private fun bindObservers() {
        feedbackViewModel = ViewModelProvider(this)[FeedbackViewModel::class.java]
        feedbackViewModel.sendFeedData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    hideDialog()
                    try {
                        val response = GsonBuilder().create().fromJson(
                            (DecryptEncrypt.DecryptStr(
                                appContext, it.data!!.string()
                            )), FeedbackResponse::class.java
                        )
                        if (response != null) {
                            EventBus.getDefault().post(NewFeedBackEvent(response.feedbacks[0]))
                            Toast.makeText(activity, "feedback submitted", Toast.LENGTH_SHORT)
                                .show()
                            binding.txtComments.setText("")
                            return@observe
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    appContext.showNetworkDialog(getString(R.string.kk_error_try_again)) {

                    }
                }

                is NetworkResult.Error -> {
                    hideDialog()
                    Constant.showLog(it.message)
                    appContext.showNetworkDialog(it.message) {

                    }
                }

                is NetworkResult.Loading -> {
                    showDialog()
                }
            }
        }
    }

    private fun initializeUI() {
        disableEmojiInTitle()
        binding.ratingBar.rating = 0f
        binding.layoutForm.visibility = View.INVISIBLE
        binding.ratingBar.onRatingBarChangeListener = this
        binding.btnSubmit.setOnClickListener {
            if (binding.txtComments.text.toString().equals("", ignoreCase = true)) {
                Toast.makeText(activity, "Plz Enter Something", Toast.LENGTH_SHORT).show()
            } else {
                sendFeedback()
            }
        }
    }

    private fun disableEmojiInTitle() {
        val emojiFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (index in start until (end - 1)) {
                val type = Character.getType(source[index])

                if (type == Character.SURROGATE.toInt()) {
                    return@InputFilter ""
                }
            }
            null
        }
        binding.txtComments.filters = arrayOf(emojiFilter)
    }

    override fun onRatingChanged(ratingBar: RatingBar, value: Float, b: Boolean) {
        if (!isAnimated) {
            binding.layoutForm.visibility = View.VISIBLE
            ViewAnimator
                .animate(binding.lblHowHappy)
                .dp().translationY(0f, -75f)
                .duration(350)
                .interpolator(LinearOutSlowInInterpolator())
                .andAnimate(ratingBar)
                .dp().translationY(0f, -75f)
                .duration(450)
                .interpolator(LinearOutSlowInInterpolator())
                .andAnimate(binding.layoutForm)
                .dp().translationY(0f, -75f)
                .singleInterpolator(LinearOutSlowInInterpolator())
                .duration(450)
                .alpha(0f, 1f)
                .interpolator(FastOutSlowInInterpolator())
                .andAnimate(binding.lblWeHearFeedback)
                .dp().translationY(0f, -20f)
                .interpolator(LinearOutSlowInInterpolator())
                .duration(300)
                .alpha(0f, 1f)
                .andAnimate(binding.txtComments)
                .dp().translationY(30f, -30f)
                .interpolator(LinearOutSlowInInterpolator())
                .duration(550)
                .alpha(0f, 1f)
                .andAnimate(binding.btnSubmit)
                .dp().translationY(60f, -35f)
                .interpolator(LinearOutSlowInInterpolator())
                .duration(800)
                .alpha(0f, 1f)
                .onStop { isAnimated = true }
                .start()
        }
    }

    private fun sendFeedback() {
        @SuppressLint("HardwareIds") val deviceId = Settings.Secure.getString(
            appContext.contentResolver, Settings.Secure.ANDROID_ID
        )
        feedbackViewModel.sendFeedData(
            DecryptEncrypt.EncryptStr(
                appContext, MyApplication.sendFeedApi(
                    appContext,
                    deviceId,
                    PowerPreference.getDefaultFile().getString(
                        Constant.mToken, "123"
                    ),
                    binding.txtComments.text.toString(),
                    binding.ratingBar.rating.toString()
                )
            )
        )
    }
}