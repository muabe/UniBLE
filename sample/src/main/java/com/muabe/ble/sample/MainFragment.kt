package com.muabe.ble.sample

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.ScanResult
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import android.view.View
import com.markjmind.uni.OnBackpressCallback
import com.markjmind.uni.UniBindFragment
import com.markjmind.uni.common.Store
import com.markjmind.uni.mapper.annotiation.AutoBinder
import com.markjmind.uni.mapper.annotiation.OnClick
import com.muabe.ble.sample.databinding.MainFragmentBinding
import com.muabe.ble.sample.service.ScreenShotService
import com.muabe.ble.sample.util.ScanDto
import com.muabe.ble.sample.util.Util
import com.muabe.unible.client.BleListener
import com.muabe.unible.client.UniBle
import com.muabe.unible.client.exception.BleException
import com.muabe.uniboot.extension.recycler.UniRecycler
import com.muabe.uniboot.extension.recycler.ViewType


@AutoBinder
class MainFragment : UniBindFragment<MainFragmentBinding>(), BleListener{
    lateinit var uniRecycler : UniRecycler
    lateinit var uniBle :UniBle
    var list = ArrayList<ScanDto>()
    lateinit var service: ScreenShotService
    var message = ScanDto()
    var time = 0L

    override fun onPre() {
        binder.image.visibility = View.INVISIBLE
        uniBle = UniBle(context, this)
        service = ScreenShotService(object : ScreenShotService.ReadResult {
            override fun readImageSize(size: Int) {
                uiToast("image size : " + size.toString())
            }

            override fun readImageCount(count: Int) {
                uiToast("packet : " + count.toString())
            }

            override fun onProcess(
                currentPosition: Int,
                totalPosition: Int,
                currentBytes: Int,
                totalBytes: Int
            ) {
                if (time == 0L) {
                    time = SystemClock.currentThreadTimeMillis()
                }
                uiToast("${100 * currentPosition / totalPosition}%($currentBytes/$totalBytes) $currentPosition/$totalPosition, ${(SystemClock.currentThreadTimeMillis() - time) / 10}초")
            }

            override fun onCompleted(imageData: ByteArray?) {
                uiToast("complete ${(SystemClock.currentThreadTimeMillis() - time) / 10}초, ${imageData?.size ?: -1}bytes")


                activity?.runOnUiThread(Runnable {
//                    val bmp: Bitmap = BitmapFactory.decodeByteArray(Util.testData(), 0, Util.testData().size)
//                    binder.image.setImageBitmap(
//                            Bitmap.createScaledBitmap(
//                                    bmp,
//                                    binder.image.getWidth(),
//                                    binder.image.getHeight(),
//                                    false
//                            )
//                    )
//                    binder.image.visibility = View.VISIBLE
//                    binder.image.setOnClickListener{
//                        binder.image.visibility = View.GONE
//                    }
                    builder.addParam("uniBle", uniBle).replace(ImageFragment())
                })


            }
        })
        uniBle.addService(service)
        uniBle.bluetoothOn()
        uniBle.enableAutoBluetooth(activity)
        uniBle.regist()

        uniRecycler = UniRecycler(binder.recycler)
        uniRecycler.addHeader("header")
                .addType(HeaderType::class.java)
                .setSingleItem(message)

        uniRecycler.addGroup("main")
            .addType(
                ViewType(ItemType::class.java).addParam("ble", uniBle).addParam(
                    "mainFragment",
                    this
                )
            )
            .setList(list)

        Util.isGps(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        uniBle.disconnect()
        uniBle.unRegist()

    }

    @OnClick
    fun qrcode(view: View){

        builder.setOnBackCallback(object : OnBackpressCallback<MainFragment>(){
            override fun callback(fragment: MainFragment?, result: Store<*>?) {
                if(result?.containsKey("address") == true){
                    toast(result.getString("address"))
                    downLoadImage()
                }
            }

        }).replace(QrFragment())


    }

    @OnClick
    fun scan(view: View){
        if(Util.isGps(context)) {
            message.name = "start scan"
            list.clear()
            uniRecycler.notifyDataSetChanged()
            uniBle.startScan(10 * 1000)
        }
    }

    @OnClick
    fun stop(view: View){
        if (uniBle.isScaning()) {
            uniBle.stopScan()
        }
    }

    @OnClick
    fun touchBtn(view: View){
        if(Util.isGps(context)) {
            if (uniBle.isConnected) {
                builder.addParam("uniBle", uniBle).replace(ImageFragment())
            } else {
                message.name = "접속되지 않았습니다."
            }
        }
    }

    @OnClick
    fun downloadImage(view: View){
        downLoadImage()
    }

    fun downLoadImage(){
        if(Util.isGps(context)) {
            if (uniBle.isConnected) {
                message.name = "read imageSize"
                service.imageSize()
                time = 0L
            } else {
                message.name = "접속되지 않았습니다."
            }
        }
    }
    override fun onScanResult(
        device: BluetoothDevice?,
        result: ScanResult?,
        rssi: Int,
        scanRecord: ByteArray?
    ) {
        activity?.runOnUiThread(Runnable {
            device?.name?.let {
                list.add(ScanDto(device))
                uniRecycler.notifyItemInserted(list.size - 1)
            }
        })
    }

    override fun onScanStop(list: MutableList<BluetoothDevice>?) {
        if(!uniBle.isConnected) {
            uiToast("stop scan")
        }
    }

    override fun onBonded(device: BluetoothDevice?) {
        uiToast("onBonded:" + device?.name)
    }

    override fun onConnected(uniBle: UniBle?, gatt: BluetoothGatt?) {
        uiToast("connect : " + uniBle?.connectedevice?.address)
    }

    override fun onDisconnected() {
        uiToast("disconnect")
    }

    override fun onException(bleException: BleException?) {
        log.e(bleException.toString())
        uiToast(bleException?.casue())
        uniBle.disconnect()
    }

    fun uiToast(msg: String?) {
        activity?.runOnUiThread(Runnable {
            message.name = msg
        })
    }
}