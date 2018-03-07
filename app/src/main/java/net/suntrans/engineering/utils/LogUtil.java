package net.suntrans.engineering.utils;

import android.util.Log;

import static net.suntrans.engineering.BuildConfig.DEBUG;


public class LogUtil {

    private static final String TAG = "Engineering";
    // 是否显示Log消息
    private static boolean isShow = DEBUG;

    public static boolean isShow() {
        return isShow;
    }

    public static void i(String tag, String msg) {
        if (msg == null)
            return;
        if (isShow)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (msg == null)
            return;
        if (isShow)
            Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (msg == null)
            return;
        if (isShow)
            Log.e(tag, msg);
    }

    public static void all(String msg) {
        if (msg == null)
            return;
        if (isShow)
            Log.e(TAG, msg);
    }

    public static void i(String msg) {
        if (msg == null)
            return;
        if (isShow)
            Log.i(TAG, msg);
    }

    public static void w(String msg) {
        if (isShow)
            Log.w(TAG, msg);
    }

    public static void e(String msg) {
        if (msg == null)
            return;
        if (isShow)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (msg == null)
            return;
        if (isShow)
            Log.v(TAG, msg);
    }

    public static void d(String msg) {
        if (isShow)
            Log.d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (msg == null)
            return;
        if (isShow)
            Log.d(tag, msg);
    }

    public static void w(String tag, String msg, Exception e) {
        if (msg == null)
            return;
        if (isShow)
            Log.w(tag, msg, e);
    }

    public static void v(String tag, String msg) {
        if (msg == null)
            return;
        if (isShow)
            Log.v(tag, msg);
    }
}
