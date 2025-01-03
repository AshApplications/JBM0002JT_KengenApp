package com.water.alkaline.kengen.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.water.alkaline.kengen.R
import com.water.alkaline.kengen.databinding.DialogInternetBinding

fun View.onSingleClick(debounceTime: Long = 1500, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun Context.showNetworkDialog(text: String = resources.getString(R.string.kk_error_unknown)): DialogInternetBinding {
    val dialog = Dialog(this, R.style.NormalDialog)
    val binding = DialogInternetBinding.inflate(
        LayoutInflater.from(this)
    )
    dialog.setContentView(binding.root)
    dialog.setCancelable(false)
    dialog.setCanceledOnTouchOutside(false)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    dialog.show()
    binding.txtError.text = text
    return binding
}