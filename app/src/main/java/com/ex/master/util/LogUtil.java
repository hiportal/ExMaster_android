package com.ex.master.util;

import android.util.Log;

import static com.ex.master.util.CommonConstant.FEATURE_USE_DEBUG;

public class LogUtil {

    private static final boolean ENABLE = FEATURE_USE_DEBUG;

    public static final int ASSERT = 7;
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    public static final int VERBOSE = 2;
    public static final int WARN = 5;

    public static void d(String tag, String msg)
    {
        if(ENABLE)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg)
    {
        if(ENABLE)
            Log.e(tag, msg);
    }

    public static void i(String tag, String msg)
    {
        if(ENABLE)
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg)
    {
        if(ENABLE)
            Log.v(tag, msg);
    }

    public static void w(String tag, String msg)
    {
        if(ENABLE)
            Log.w(tag, msg);
    }

}
