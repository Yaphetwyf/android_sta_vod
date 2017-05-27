package com.example.administrator.android_sta_vod.app;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.afa.tourism.greendao.gen.DaoMaster;
import com.afa.tourism.greendao.gen.DaoSession;
import com.example.administrator.android_sta_vod.base.Global;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.LinkedList;
import java.util.List;


//主线程中
public class My_application extends Application {
    public List<Activity> mList = new LinkedList<Activity>();
    public static My_application instance;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    public My_application() {
    }
    public synchronized static My_application getInstance() {
        if (null == instance) {
            instance = new My_application();
        }
        return instance;
    }
    public static My_application getMy_application(){
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
        CrashReport.initCrashReport(getApplicationContext(), "8e81059aec", true);
        super.onCreate();
        Global.init(this);

        setDatabase();
        Net_data.instance();

    }

    private void setDatabase() {
        // 通过DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为greenDAO 已经帮你做了。
        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this,"notes.db", null);
        db =mHelper.getWritableDatabase();
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }
    public DaoSession getDaoSession() {
        return mDaoSession;
    }
    public SQLiteDatabase getDb() {
        return db;
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
