package com.muabe.unible.client.exception;

public enum Message {
    BT_NOT_SUPPORT(0,"BLE가 지원 안되는 디바이스입니다."),
    CAN_NOT_CONNECT(1, "디바이스와 연결할수 없거나 찾을수 없습니다."),
    BT_BONDING(2, "Paring을 하고있는 중에는 connet를 할수 없습니다."),
    STATE_CONNECTING(3, "이미 connection을 하고 있는 중입니다."),
    STATE_DISCONNECTED(4, "연결이 종료 되었습니다."),
    STATE_DISCONNECTING(5, "연결을 종료 중 입니다."),
    UNKNOWN_CONNECT_ERROR(6, "알수없는 connection 오류가 발생"),
    SERVICES_DISCOVER_FAILURE(7, "BLE 서비스 찾는중 오류 발생"),
    CHARACTERISTIC_READ_FAILURE(8, "BLE CHARATERISTIC Read 오류"),
    CHARACTERISTIC_WRITE_FAILURE(9, "BLE CHARATERISTIC Write 오류"),
    SCAN_FAILED(10, "스캔 실패"),
    BT_BOND_FAILED (11, "페어링 실패"),
    BT_STATE_OFF(12, "블루투수가 비활성화됨"),
    BT_NOT_ENABLE(13, "블루투스가 활성화되지 않아 명령을 실행할수 없음"),
    ALEADY_CONNECTED(14, "기기에 이미 연결되어 있습니다."),
    ALEADY_CONNECTING(15, "이미 연결 중입니다."),
    CONNECTION_FAILED(16,  "연결에 실패 하였습니다."),
    DESCIPTOR_WRITE_FAILURE(17, "BLE DESCIPTOR WRITE 오류");

    private int code;
    private String cause;

    Message(int code, String cause){
        this.code = code;
        this.cause = cause;
    }

    public int code(){
        return this.code;
    }

    public String cause(){
        return this.cause;
    }
}
