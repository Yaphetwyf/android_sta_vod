package com.example.administrator.android_sta_vod.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.broadcast.android.android_sta_jni.ndk_wrapper;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.adapter.Time_broadcast_adapter;
import com.example.administrator.android_sta_vod.bean.Task;
import com.example.administrator.android_sta_vod.bean.Tasks;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.utils.Beacon_util;
import com.example.administrator.android_sta_vod.utils.T;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by Administrator on 2017/4/24.
 */
public class Time_broadcast_activity extends Base_activity {

    private Time_broadcast_adapter broadcast_adapter;
    private ListView lv_broadcast_task;
    private List<Task> tasks1;
    private Tasks tasks;

    private int item_id=-1;
    @Override
    public int get_layout_res() {
        return R.layout.activity_time_broadcast;
    }

    @Override
    public void init_view() {
        lv_broadcast_task = findView(R.id.lv_time_broadcast_task);
    }

    @Override
    public void init_listener() {
        lv_broadcast_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item_id=position;
                broadcast_adapter.setSelectedPosition(item_id);
                broadcast_adapter.notifyDataSetInvalidated();
            }
        });
    }

    @Override
    public void init_data() {

    }

    @Override
    public void onClick(View v, int btnId) {
        switch (btnId){
            case R.id.ib_hand_start:
                if (-1 == item_id) {
                    T.show_short(Ui_utils.get_string(R.string.delete_toast));
                    return;
                }
                String status = tasks1.get(item_id).getStatus();
                if ("2".equals(status)) {
                    T.show_short(Ui_utils.get_string(R.string.task_started));
                }else {
                    String id = tasks1.get(item_id).getId();
                    ndk_wrapper.instance().avsz_task_start(Integer.valueOf(id));
                }
                break;
            case R.id.ib_hand_stop:
                if (-1 == item_id) {
                    T.show_short(Ui_utils.get_string(R.string.delete_toast));
                    return;
                }
                String id1=  tasks1.get(item_id).getId();
                String states=tasks1.get(item_id).getStatus();
                Log.d("test_states",states+"");
                if ("0".equals(states)) {
                    T.show_short(Ui_utils.get_string(R.string.task_free));
                    return;
                }
                ndk_wrapper.instance().avsz_task_stop(Integer.valueOf(id1));
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusOnMainThread(Avsz_info_event msg) {
        String type = msg.get_type();
        String key = msg.get_key();
        String value = msg.get_value();
        Log.d("----value---","value:"+value+"type:"+type+"key:"+key);
        if("task_start_ret".equals(type)){
            if(!"ok".equals(value)){
                T.show_short(Ui_utils.get_string(R.string.handler_start_failure));
            }
        }

        if("task_stop_ret".equals(type)){
            if(!"ok".equals(value)){
                T.show_short(Ui_utils.get_string(R.string.handler_stop_failure));
            }
        }

        if ("task_status".equals(type)) {
            for (int i = 0; i < tasks1.size(); i++) {
                if(tasks1.get(i).getId().equals(key)){
                    tasks1.get(i).setStatus(value);
                    broadcast_adapter.notifyDataSetChanged();
                }
            }
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusOnMainThread(Tasks tasks) {
        tasks1 = tasks.getTasks();
        Log.d("tasks1111",tasks1+"");
        broadcast_adapter = new Time_broadcast_adapter(tasks1);
        lv_broadcast_task.setAdapter(broadcast_adapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Beacon_util.login();

            EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
