package com.muabe.ble.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.muabe.uniboot.boot.wing.MenuBoot


class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MenuBoot.putContentView(this).initHomeFragment(IntroFragment())
    }


    override fun onBackPressed() {
        if(!MenuBoot.onBackPressed(this)){
            super.onBackPressed()
        }
    }
}