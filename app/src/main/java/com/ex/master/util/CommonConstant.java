package com.ex.master.util;

public interface CommonConstant {

//    public static String MOB_SERVER_URL = "http://180.148.182.109:5004/MTEQ/";  //운영서버
    public static String MOB_SERVER_URL = "https://ecard.ex.co.kr:5004/MTEQ/";  //운영서버


    public static String MOB_SERVER_DOMAIN = "ecard.ex.co.kr";

    public static final String MOB_TITLE = "[자재장비대금관리]";

    public static final boolean FEATURE_USE_DEBUG = false;              //배포시 false

    //위치서비스 요청값
    public static final int GPS_ENABLE_REQUEST_CODE = 2001;
    public static final int PERMISSIONS_REQUEST_CODE = 100;

    //모바일 네트워크 확인
    public static final int NETWORK_TYPE_MOBILE = 1;
    public static final int NETWORK_TYPE_WIFI = 2;
    public static final int NETWORK_TYPE_NOT_CONNECTED = 3;
    public static final String NET_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String NET_WIFI_STATE_CHANGE = "android.net.wifi.STATE_CHANGE";
    public static final String NET_WIFI_SUPPLICANT_CONNECTION_CHANGE = "android.net.wifi.supplicant.CONNECTION_CHANGE";

}
