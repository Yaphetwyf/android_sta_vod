package com.example.administrator.android_sta_vod.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.administrator.android_sta_vod.base.Const;



public class Utils {

    /**
     * 查找一个布局中所有的按钮(Button或ImageButton)并设置点击事件
     * @param root
     */
    public static void findButtonsAndSetOnClickListener(View root,
            View.OnClickListener onClickListener) {

        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                // 如果是按钮
                if (child instanceof Button || child instanceof ImageButton) {
                    // 设置点击事件监听器
                    child.setOnClickListener(onClickListener);
                } else {
                    findButtonsAndSetOnClickListener(child, onClickListener);
                }
            }
        }
    }

    /**
     * 打印游标中所有的数据
     * @param cursor
     */
    public static void printCursor(Cursor cursor) {
        if (cursor == null)
            return;

        int count = cursor.getCount();
        log("总条数：" + count);

        while (cursor.moveToNext()) {
            // 打钱一条数据中的所有的字段
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String name = cursor.getColumnName(i);
                String value = cursor.getString(i);
                log("------" + name + "    "  + value);
            }
            log("-------------------------------------" );
        }
    }

    public static void log(String msg) {
        if (DEBUG)
           Log.d("player", "-----" +  msg);
    }

    private static final boolean DEBUG = true;

    /**
     * 格式化时间显示，大于一小时，则显示时分秒，否则显示分钟和秒
     *
     * @param duration
     * @return
     */
    public static String formatDuration(long duration) {
        // 格式化时间显示：
        // SimpleDateFormat:     "HH:mm:ss"
        // DateFormat:           "kk:mm:ss"
        String format = null;
        if (duration >= 1000 * 60 * 60) {   // 大于等于一小时
            format = "kk:mm:ss";
        } else {
            format = "mm:ss";
        }

        return DateFormat.format(format, duration).toString();
    }

    /**
     * 用SharedPreference保存当前音频播放模式
     *
     * @param context
     * @param currentPlayMode 当前音频播放模式
     */
    public static void saveAudioPlayMode(Context context, int currentPlayMode) {
        // SharedPreferences sp = context.getSharedPreferences("xx.xml", Context.MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(Const.KEY_PLAY_MODE, currentPlayMode).commit();
    }

    /**
     * 读取上一次保存的音频播放模式
     *
     * @param context
     * @param defValue 获取不到时，指定的默认的音频播放模式
     * @return  上一次保存的音频播放模式
     */
    public static int getAudioPlayMode(Context context, int defValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Const.KEY_PLAY_MODE, defValue);
    }

}




















