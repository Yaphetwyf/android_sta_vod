package com.example.administrator.android_sta_vod.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.administrator.android_sta_vod.R;

/**
 * Created by Administrator on 2017/6/21.
 */

public class Test_activity extends Base_activity {
    @Override
    public int get_layout_res() {
        return R.layout.activity_test;
    }

    @Override
    public void init_view() {

    }

    @Override
    public void init_listener() {

    }

    @Override
    public void init_data() {

    }

    @Override
    public void onClick(View v, int btnId) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
}
