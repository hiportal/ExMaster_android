package com.ex.master.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.LOCATION_SERVICE;
import static com.ex.master.util.CommonConstant.NETWORK_TYPE_MOBILE;
import static com.ex.master.util.CommonConstant.NETWORK_TYPE_NOT_CONNECTED;
import static com.ex.master.util.CommonConstant.NETWORK_TYPE_WIFI;

public class CommonUtil {
    private static String TAG = CommonUtil.class.getSimpleName();

    //위치 서비스 활성화 되어있는지 확인
    public static boolean checkLocationServicesStatus(Context context) {
        LogUtil.d(TAG, "***** checkLocationServicesStatus() ");
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    /**
     * String null 체크
     * @param object
     * @return
     */
    public static String checkNull(String object) {
        if (isNotNullCheck(object)) {
            return object.trim();
        } else {
            return "";
        }
    }

    public static boolean isNotNullCheck(String object) {
        return ((object != null) && !object.isEmpty());
    }

    public static String[] checkNull(String[] object) {
        return object;
    }


    /**
     * int null 체크
     * @param object
     * @return
     */
    public static int checkNull(final int object) {
        if (isNotNullCheck(object)) {
            return object;
        } else {
            return 0;
        }
    }

    public static boolean isNotNullCheck(final int object) {
        return ((object != 0));
    }


    /**
     * Json null 체크
     * @param object
     * @return
     */
    public static String checkNull(final JsonElement object) {
        if (isNotNullCheck(object)) {
            return object.toString();
        } else {
            return "";
        }
    }

    public static boolean isNotNullCheck(final JsonElement object) {
        return ((object != null) && !object.isJsonNull());
    }


    /**
     * json 형식 체크
     * @param value
     * @return
     */
    public static boolean isJSONValid(String value) {
        try {
            new JSONObject(value);
        } catch (JSONException ex) {
            try {
                new JSONArray(value);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


    /**
     * 앱 이름 가져오기
     * @param context
     * @return
     */
    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }


    /**
     * 앱 패키지명 가져오기
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        return checkNull(context.getPackageName());
    }


    /**
     * 앱 버전이름 가져오기
     * @param context
     * @return
     */
    public static String getAppVersionNm(Context context) {
        String version = "";
        PackageInfo pi = null;
        try {
            pi = context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("[Exception] CommonUtill >> getAppVersionNm() ");
        }
        version = pi.versionName;
        return version;
    }


    /**
     * 네트워크 상태 확인
     * 1 - LTE/3G
     * 2 - WIFI
     * 3 - 네트워크연결없음
     * @param context
     * @return
     */
    public static int checkNetworkStatus(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null){
            int type = networkInfo.getType();
            if(type == ConnectivityManager.TYPE_MOBILE){//쓰리지나 LTE로 연결된것(모바일을 뜻한다.)
                return NETWORK_TYPE_MOBILE;
            }else if(type == ConnectivityManager.TYPE_WIFI){//와이파이 연결된것
                return NETWORK_TYPE_WIFI;
            }
        }
        return NETWORK_TYPE_NOT_CONNECTED;  //연결이 되지않은 상태
    }

}
