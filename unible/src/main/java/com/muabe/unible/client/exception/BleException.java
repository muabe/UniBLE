package com.muabe.unible.client.exception;

public class BleException {

    Message msg;
    private String subMessage;

    public BleException(Message msg, String subMessage){
        this.msg = msg;
        this.subMessage = subMessage;
    }

    public BleException(Message msg){
        this(msg, null);
    }

    public Message message(){
        return msg;
    }

    public int code(){
        return msg.code();
    }

    public String casue(){
        return msg.cause();
    }

    public String subMessage(){
        return subMessage;
    }

    @Override
    public String toString() {
        if(subMessage == null){
            return casue()+ " code:"+ code();
        }else{
            return casue()+"("+subMessage+") code:"+ casue();
        }

    }

//    public <T>boolean Null(int code, T t, String cause){
//        if(t == null){
//            onException(code, cause);
//            return true;
//        }
//        return false;
//    }

//    public void onException(int code, String cause){
//
//    }

}
