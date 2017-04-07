package com.example.administrator.android_sta_vod.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.bean.Mp3;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/30 0030.
 */

public class Remote_play_activity extends Base_activity {

    private ListView lv_remote_play;

    @Override
    public int get_layout_res() {
        return R.layout.activity_remote_play;
    }
    @Override
    public void init_view() {
        lv_remote_play = findView(R.id.lv_remote_play);

    }

    @Override
    public void init_listener() {

    }

    @Override
    public void init_data() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v, int btnId) {

    }
}
