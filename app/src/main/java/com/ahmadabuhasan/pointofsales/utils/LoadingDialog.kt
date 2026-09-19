package com.ahmadabuhasan.pointofsales.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.ahmadabuhasan.pointofsales.databinding.DialogLoadingBinding

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class LoadingDialog(context: Context) {

    private val binding = DialogLoadingBinding.inflate(LayoutInflater.from(context))

    private val dialog = AlertDialog.Builder(context)
        .setView(binding.root)
        .setCancelable(false)
        .create()
        .apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }

    fun show(message: String) {
        binding.tvLoadingMessage.text = message
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}
