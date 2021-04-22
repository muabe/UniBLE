package com.muabe.ble.sample

import android.content.Context.VIBRATOR_SERVICE
import android.graphics.PointF
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.markjmind.uni.UniBindFragment
import com.markjmind.uni.mapper.annotiation.AutoBinder
import com.markjmind.uni.mapper.annotiation.OnClick
import com.markjmind.uni.mapper.annotiation.Param
import com.muabe.ble.sample.databinding.QrFragmentBinding
import com.muabe.unible.client.UniBle

@AutoBinder
class QrFragment : UniBindFragment<QrFragmentBinding>(), QRCodeReaderView.OnQRCodeReadListener {

    override fun onPre() {
        binder.qrdecoderview.setOnQRCodeReadListener(this)
        binder.qrdecoderview.setQRDecodingEnabled(true)
        binder.qrdecoderview.setAutofocusInterval(1000L)
        binder.qrdecoderview.setBackCamera()
    }

    @OnClick
    fun close(view: View){
        onBackPressed()
    }

    override fun onPost() {
        binder.qrdecoderview.startCamera()
    }

    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {

        binder.qrdecoderview.stopCamera()
        val vibrator = activity?.getSystemService(VIBRATOR_SERVICE) as Vibrator
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1)
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        else
            vibrator.vibrate(500)

        addBackpressResult("address", text)
        onBackPressed()
    }

    override fun onPause() {
        binder.qrdecoderview.stopCamera()
        super.onPause()
    }


}