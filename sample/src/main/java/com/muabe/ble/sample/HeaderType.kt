package com.muabe.ble.sample

import com.muabe.ble.sample.databinding.HeaderBinding
import com.muabe.ble.sample.util.ScanDto
import com.muabe.uniboot.extension.recycler.TypeHolder

class HeaderType : TypeHolder<ScanDto, HeaderBinding>(){
    override fun onBind(itemBinding: HeaderBinding, item: ScanDto?) {
        itemBinding.vm = item
    }
}