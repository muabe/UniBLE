package com.muabe.ble.sample

import android.view.View
import com.markjmind.uni.mapper.annotiation.OnClick
import com.markjmind.uni.mapper.annotiation.Param
import com.muabe.ble.sample.databinding.ItemBinding
import com.muabe.ble.sample.util.ScanDto
import com.muabe.ble.sample.util.Util
import com.muabe.unible.client.UniBle
import com.muabe.uniboot.extension.recycler.TypeHolder

class ItemType : TypeHolder<ScanDto, ItemBinding>(){
    @Param
    lateinit var ble : UniBle

    @Param
    lateinit var mainFragment: MainFragment


    override fun onBind(itemBinding: ItemBinding, item: ScanDto?) {
        itemBinding.vm = item
    }

    @OnClick
    fun row(view: View){
        if(Util.isGps(view.context)) {
            if (ble.isConnected) {
                mainFragment.message.name = "disconnecting...."
                ble.disconnect()
            } else {
                mainFragment.message.name = "connecting...."
                ble.stopScan()
                ble.connectNotPairing(item.address)
            }
        }
    }

}