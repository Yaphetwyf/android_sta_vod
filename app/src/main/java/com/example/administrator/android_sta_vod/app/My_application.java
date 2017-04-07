package com.example.administrator.android_sta_vod.app;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.example.administrator.android_sta_vod.base.Global;

import java.util.LinkedList;
import java.util.List;


//主线程中
public class My_application extends Application {
    public List<Activity> mList = new LinkedList<Activity>();
    public static My_application instance;

    public My_application() {
    }
    public synchronized static My_application getInstance() {
        if (null == instance) {
            instance = new My_application();
        }
        return instance;
    }
    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
    //handler---->在项目中一定会用到
    private static Handler handler_;
    //创建一个上下文环境
    private static Context mctx_;
    //主线程对象(UI操作)
    private static Thread main_thead_;
    //主线程id,线程id唯一性,主线程id,子线程id
    private static int main_thread_id_;

   /* public static RefWatcher getRefWatcher(Context context) {
        My_application application = (My_application) context
                .getApplicationContext();
        return application.refWatcher;
    }

    //在自己的Application中添加如下代码
    private RefWatcher refWatcher;*/

    //假设现在运行了一段代码,获取此段代码所在线程id,然后和主线程id比对一下,如果一致,
    //说明此段代码在主线程中,否则子线程中
    @Override
    public void onCreate() {
        //此方法在开启应用的时候，第一个调用
        handler_ = new Handler();
        mctx_ = getApplicationContext();
        //因为Application中的oncreate方法运行在主线程中,获取当前线程,即为主线程.
        main_thead_ = Thread.currentThread();
        //主线程id,等同于mainThead.getId();作用
        main_thread_id_ = android.os.Process.myTid();
       // refWatcher = LeakCanary.install(this);
        super.onCreate();
        Global.init(this);
        Net_data.instance();
    }

    public static Handler get_handler() {
        return handler_;
    }
    public static Context get_context() {
        return mctx_;
    }
    public static Thread get_main_thead() {
        return main_thead_;
    }
    public static int get_main_thread_id() {
        return main_thread_id_;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
      //  L.e("exit_app");
    }




}
