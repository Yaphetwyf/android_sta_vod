package com.example.administrator.android_sta_vod.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * 全局类： 封装屏幕尺寸，主线程运行，inflate, toast等等..
 */
public class Global {

    public static Context mContext;

    // 屏幕尺寸密度
    public static int mScreenWidth;
    public static int mScreenHeight;
    public static float mDensity;

    public static Handler mHandler = new Handler();

    public static void init(Context context) {
        Global.mContext = context;

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mDensity = dm.density;
    }

    /** dp 转 px */
    public static int dp2px(int dp) {   // 10
        return (int) (mDensity * dp + 0.5f);
    }

    /** 判断当前是否在主线程运行 */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /** 在主线程运行 */
    public static void runOnMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else { // 子线程
            mHandler.post(runnable);
        }
    }

    public static View inflate(int resId, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(resId, parent, false);
    }

    private static Toast mToast;

    /**
     * showToast,可以在子线程调用
     */
    public static void showToast(final String msg) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
                }
                mToast.setText(msg);
                mToast.show();
            }
        });
    }
}














