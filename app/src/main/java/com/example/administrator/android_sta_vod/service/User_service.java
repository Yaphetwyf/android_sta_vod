package com.example.administrator.android_sta_vod.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.ui.activity.Answer_term_activity;
import com.example.administrator.android_sta_vod.ui.activity.Answer_user_activity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by Administrator on 2017/6/22.
 */

public class User_service extends Service {

    private static final String TAG = "ExampleService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "ExampleService-onBind");
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "ExampleService-onStart");
        super.onStart(intent, startId);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }


    //接收回调消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Avsz_info_event info) {
        String type = info.get_type();
        String key = info.get_key();
        String value = info.get_value();
        if ("usr_call_req".equals(type)) {
            boolean background = isApplicationBroughtToBackground(this);
            if(background){
                Intent intent = new Intent(this, Answer_user_activity.class);
                intent.putExtra("key", key);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        }
        if ("usr_call_req_cancel".equals(type)) {

        }

        if ("term_call_req".equals(type)) {
            boolean background = isApplicationBroughtToBackground(this);
            if(background){
                Intent intent = new Intent(this, Answer_term_activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("term_id", key);
                bundle.putString("term_name", value);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        if ("term_call_req_cancel".equals(type)) {

        }
    }
    @Override
    public void onCreate() {
        Log.i(TAG, "ExampleService-onCreate");
        super.onCreate();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
