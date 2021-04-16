package com.muabe.unible.client;

public class BleState {
    boolean scanning;
    boolean connecting;
    boolean bonding;

    BleState(){
        init();
    }

    void init(){
        connecting = false;
        scanning = false;
        bonding = false;
    }
}
