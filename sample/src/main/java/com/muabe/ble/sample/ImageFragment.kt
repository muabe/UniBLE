package com.muabe.ble.sample

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.markjmind.uni.UniBindFragment
import com.markjmind.uni.common.Jwc
import com.markjmind.uni.mapper.annotiation.AutoBinder
import com.markjmind.uni.mapper.annotiation.Param
import com.muabe.ble.sample.databinding.ImageFragmentBinding
import com.muabe.ble.sample.service.ScreenShotService
import com.muabe.unible.client.UniBle

@AutoBinder
class ImageFragment : UniBindFragment<ImageFragmentBinding>() {
    @Param
    lateinit var uniBle: UniBle
    lateinit var gestureDetector: GestureDetector
    lateinit var scaleGestureDetector: ScaleGestureDetector

    var xSize = 1080
    var ySize = 1920
    var maxScale:Float = 2f
    private var mScaleFactor = 1f

    var lastFocusX = 0f
    var lastFocusY = 0f

    //72


    override fun onPre() {

        binder.layout.isClickable = true
        binder.layout.isLongClickable = false
        binder.layout.setOnTouchListener(View.OnTouchListener { v, event ->
            if (gestureDetector.onTouchEvent(event)) {
                true
            } else {
                scaleGestureDetector.onTouchEvent(event)
            }
        })

        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                mScaleFactor *= detector.scaleFactor
                mScaleFactor = Math.max(1f, Math.min(mScaleFactor, 2.0f))
                binder.layout.scaleX = mScaleFactor
                binder.layout.scaleY = mScaleFactor
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                if(mScaleFactor == 1f) {
                    binder.layout.pivotX = detector.focusX / mScaleFactor
                    binder.layout.pivotY = detector.focusY / mScaleFactor
                    lastFocusX = binder.layout.pivotX
                    lastFocusY = binder.layout.pivotY
                }
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {
            }

        })

        gestureDetector = GestureDetector(context, object : GestureDetector.OnGestureListener {
            override fun onDown(e: MotionEvent?): Boolean {
                return false
            }

            override fun onShowPress(e: MotionEvent?) {
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                binder.check.visibility = View.VISIBLE
                binder.check.x = e?.x!!-144f/2f
                binder.check.y = e?.y!!-144f/2f
                var ratio = Jwc.getWindowWidth(context).toFloat() / xSize
                uniBle.getGattService(ScreenShotService::class.java).touch((e?.x!! / ratio).toInt(), (e?.y!! / ratio).toInt())
                return true
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                return false
            }

            override fun onLongPress(e: MotionEvent?) {
            }

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                return false
            }

        })

    }
}