package com.example.administrator.android_sta_vod.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;


import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.app.My_application;
import com.example.administrator.android_sta_vod.bean.Mp3;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;



/**
 * @author admin
 *         和UI相关的代码,尽可能封装在此类中
 */
public class Ui_utils {
    private static final String TAG = "UIUtils";

    public static Context get_context() {
        return My_application.get_context();
    }

    public static int get_main_thread_id() {
        return My_application.get_main_thread_id();
    }

    public static Handler get_handler() {
        return My_application.get_handler();
    }

    //1.xml---->view
    public static View inflate(int layoutId) {
        return View.inflate(get_context(), layoutId, null);
    }

    public static Resources get_resources() {
        return get_context().getResources();
    }

    //2.从xml中获取一个字符串stringId
    public static String get_string(int stringId) {
        return get_resources().getString(stringId);
    }

    //3.根据id获取drawable
    public static Drawable get_drawable(int drawableId) {
        return get_resources().getDrawable(drawableId);
    }


    //获取颜色
    public static int get_color(int color_id) {
        int color = get_resources().getColor(color_id);

        return color;
    }
    //4.dip==dp		dpi==ppi(像素密度)
    //dp px 不同手机dp和px的比例关系不一致
    //1dp = 0.75px
    //1dp = 1px
    //1dp = 1.5px
    //1dp = 2px
    //1dp = 3px
    //....
    //

    /**
     * 保存数据到本地
     *
     * @param buffer 要保存的数据
     * @param offset 要保存数据的起始位置
     * @param length 要保存数据长度
     * @param path   保存路径
     * @param append 是否追加
     */
    public static void save(byte[] buffer, int offset, int length, String path, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path, append);
            fos.write(buffer, offset, length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static int dip2px(int dip) {
        //手机的dp和px的比例关系
        float density = get_resources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);//在数学角度上做四舍五入
        //100.4px  接近于100px
        //100.5px+0.5f  接近于101px
    }

    public static int px2dip(int px) {
        //手机的dp和px的比例关系
        float density = get_resources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);//在数学角度上做四舍五入
    }

    //指定任务在主线或者子线程中执行,对runOnUiThread的重新封装
    public static void run_on_main_thread(Runnable runnable) {
        //主线程id和此方法运行在的线程id是否一致
        Log.i(TAG, "Thread.currentThread().getId() = " + Thread.currentThread().getId());
        Log.i(TAG, "android.os.Process.myTid() = " + android.os.Process.myTid());
        if (get_main_thread_id() == android.os.Process.myTid()) {
            //运行在主线程中
            runnable.run();
        } else {
            get_handler().post(runnable);
        }
    }

    public static ColorStateList getColorStateList(int mTabTextColorResId) {
        return get_resources().getColorStateList(mTabTextColorResId);
    }

    /**
     * @param arrayId 数组id
     * @return 根据id获取的字符串数组
     */
    public static String[] getStringArray(int arrayId) {
        return get_resources().getStringArray(arrayId);
    }

    public static String get_runtime(String task_type, String start_date, String excute_time, String end_select,
                                     String end_date, String continu_time, String weeks) {
        StringBuilder sb_time = new StringBuilder();
        if (end_select.equals("0")) {
            sb_time.append(start_date + Ui_utils.get_string(R.string.from) + ",");
        } else {
            sb_time.append(start_date + Ui_utils.get_string(R.string.to)).append(end_date);
        }
        if("0".equals(task_type)){
            sb_time.append(Ui_utils.get_string(R.string.everyday));
        }
        if("1".equals(task_type)){
            sb_time.append(Ui_utils.get_string(R.string.once));
        }
        if("7".equals(task_type)){
            sb_time.append(Ui_utils.get_string(R.string.everyweek));
        }
        if (task_type.equals("7")) {
            byte[] ws = weeks.getBytes();
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i < ws.length; i++) {
                switch (i) {
                    case 1:
                        if ('1' == ws[i]) {
                            L.d("ws[1] == " + ws[i]);
                            sb.append(Ui_utils.get_string(R.string.one));
                        }
                        break;
                    case 2:
                        if ('1' == ws[i]) {
                            sb.append(Ui_utils.get_string(R.string.two));
                        }
                        break;
                    case 3:
                        if ('1' == ws[i]) {
                            sb.append(Ui_utils.get_string(R.string.three));
                        }
                        break;
                    case 4:
                        if ('1' == ws[i]) {
                            sb.append(Ui_utils.get_string(R.string.four));
                        }
                        break;
                    case 5:
                        if ('1' == ws[i]) {
                            sb.append(Ui_utils.get_string(R.string.five));
                        }
                        break;
                    case 6:
                        if ('1' == ws[i]) {
                            sb.append(Ui_utils.get_string(R.string.six));
                        }
                }
            }
            if ('1' == ws[0]) {
                sb.append(Ui_utils.get_string(R.string.seven));
            }
            sb_time.append(sb);
        }
        sb_time.append(",");
        sb_time.append(excute_time + Ui_utils.get_string(R.string.exc) + ".");
        if (!continu_time.equals("0")) {
            int t = Integer.valueOf(continu_time);
            int m = t / 60;
            int s = t % 60;
            sb_time.append(Ui_utils.get_string(R.string.duration) + m + Ui_utils.get_string(R.string.minute) + s +
                    Ui_utils.get_string(R.string.second) + ".");
        }
        L.d(sb_time.toString());
        return sb_time.toString();
    }

    //任务类型
    public static String get_type(String type, String weeks) {
        StringBuilder sb_type = new StringBuilder();
        if ("0".equals(type)) {
            sb_type.append(Ui_utils.get_string(R.string.day_task));
        }
        if ("1".equals(type)) {
            sb_type.append(Ui_utils.get_string(R.string.once));
        }
        if ("7".equals(type)) {
            sb_type.append(Ui_utils.get_string(R.string.eweek));
            byte[] ws = weeks.getBytes();
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i < ws.length; i++) {
                switch (i) {
                    case 1:
                        if ('1' == ws[i]) {
                            L.d("ws[1] == " + ws[i]);
                            sb.append(Ui_utils.get_string(R.string.one));
                        }
                        break;
                    case 2:
                        if ('1' == ws[i]) {
                            sb.append(Ui_utils.get_string(R.string.two));
                        }
                        break;
                    case 3:
                        if ('1' == ws[i]) {
                            sb.append(Ui_utils.get_string(R.string.three));
                        }
                        break;
                    case 4:
                        if ('1' == ws[i]) {
                            sb.append(Ui_utils.get_string(R.string.four));
                        }
                        break;
                    case 5:
                        if ('1' == ws[i]) {
                            sb.append(Ui_utils.get_string(R.string.five));
                        }
                        break;
                    case 6:
                        if ('1' == ws[i]) {
                            sb.append(Ui_utils.get_string(R.string.six));
                        }
                }
            }
            if ('1' == ws[0]) {
                sb.append(Ui_utils.get_string(R.string.seven));
            }
            sb_type.append(sb);
        }
        return sb_type.toString();
    }

  /*  //音频总长
    public static String get_length(Files files) {
        int l = 0;
        for (File file : files.getFiles()) {
            String len = file.getTm_long();
            String[] ts = len.split(":");
            int h = Integer.valueOf(ts[0]);
            int m = Integer.valueOf(ts[1]);
            int s = Integer.valueOf(ts[2]);
            l += h * 3600;
            l += m * 60;
            l += s;
        }

        String th = l / 3600 + "";
        String tm = (l - l / 3600 * 3600) / 60 + "";
        String ts = l % 60 + "";
        if(Integer.valueOf(th)<10){
            th = "0" + th;
        }
        if(Integer.valueOf(tm)<10){
            tm = "0" + tm;
        }
        if(Integer.valueOf(ts)<10){
            ts = "0" + ts;
        }
        String tt_long = th + ":" + tm + ":" + ts;
        return tt_long;
    }*/

    public static String get_mp3len(List<Mp3> mp3s) {
        int l = 0;
        for (Mp3 mp3 : mp3s) {
            String len = mp3.getLength();
            String[] ts = len.split(":");
            int h = Integer.valueOf(ts[0]);
            int m = Integer.valueOf(ts[1]);
            int s = Integer.valueOf(ts[2]);
            l += h * 3600;
            l += m * 60;
            l += s;
        }
        String th = l / 3600 + "";
        String tm = (l - l / 3600 * 3600) / 60 + "";
        String ts = l % 60 + "";
        if(Integer.valueOf(th)<10){
            th = "0" + th;
        }
        if(Integer.valueOf(tm)<10){
            tm = "0" + tm;
        }
        if(Integer.valueOf(ts)<10){
            ts = "0" + ts;
        }
        String tt_long = th + ":" + tm + ":" + ts;
        return tt_long;
    }

    public static String get_mp3lenz(List<Mp3> mp3s) {
        String length = get_mp3len(mp3s);
        String[] ls = length.split(":");
        String hour = ls[0];
        String min = ls[1];
        String second = ls[2];
        return hour + get_string(R.string.hour) + min + get_string(R.string.min) + second + get_string(R.string
                .second);
    }

   /* public static String get_filelen(Files files) {
        String length = get_length(files);
        String[] ls = length.split(":");
        String hour = ls[0];
        String min = ls[1];
        String second = ls[2];
        return hour + get_string(R.string.hour) + min + get_string(R.string.min) + second + get_string(R.string
                .second);
    }*/
}
