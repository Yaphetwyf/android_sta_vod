package com.example.administrator.android_sta_vod.app;

import android.util.Log;


import com.example.administrator.android_sta_vod.bean.Areas;
import com.example.administrator.android_sta_vod.bean.Progs;
import com.example.administrator.android_sta_vod.bean.Root;
import com.example.administrator.android_sta_vod.bean.Task;
import com.example.administrator.android_sta_vod.bean.Tasks;
import com.example.administrator.android_sta_vod.bean.Terms;
import com.example.administrator.android_sta_vod.bean.Time_setting;
import com.example.administrator.android_sta_vod.bean.Users;
import com.example.administrator.android_sta_vod.utils.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by AVSZ on 2016/12/27.
 */

public class Net_data {
    private static Net_data net_data;
    private static String tag = "NET_DATA";
    private Areas areas;
    private Progs progs;
    private Tasks tasks;
    private Terms terms;
    private Users users;
    private Time_setting time;
    private Task task;
    private Net_data(){

        EventBus.getDefault().register(this);
    }

    public static Net_data instance(){
        if(null == net_data){
            net_data = new Net_data();
        }
        return net_data;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Areas areas){
        this.areas = areas;
        L.d("get areas");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Terms terms){
        this.terms = terms;
        L.d("get terms");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Progs progs){
        this.progs = progs;
        L.d("get progs");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Tasks tasks){
        this.tasks = tasks;
        L.d("get tasks");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Users users){
        this.users = users;
        L.d("get users");
    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEventMainThread(Root root){
        areas = root.getAreas();
        progs = root.getProgs();
        tasks = root.getTasks();
        terms = root.getTerms();
        users = root.getUsers();
        L.d("get root");
    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEventMainThread(Task task){
        this.task = task;
    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEventMainThread(Time_setting time){
        this.time = time;
    }

    public Task get_task(){
        return task;
    }
    public Areas get_areas(){
        return areas;
    }

    public Progs get_progs(){
        return progs;
    }

    public Tasks get_tasks(){
        return tasks;
    }

    public Terms get_terms(){
        return terms;
    }

    public Users get_users(){
        if(null == users){
            Log.d(tag,"user == null");
        }
        return users;
    }

    public Time_setting get_time(){
        L.d("net_data======"+time.toString());
        return time;
    }
}
