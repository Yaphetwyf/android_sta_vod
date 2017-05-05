package com.example.administrator.android_sta_vod.ui.activity.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.ui.activity.Add_terminal_activity;

/**
 * Created by Administrator on 2017/5/2.
 */

public class Terminal_two_fragment extends BaseFragment {

    private TextView add_terminal;
    private LinearLayout ll_add_termianl;

    @Override
    public int get_layout_res() {
        return R.layout.fragment_terminal_two;
    }

    @Override
    public void init_view() {
        add_terminal = findView(R.id.tv_add_terminal);
        ll_add_termianl = findView(R.id.ll_add_terminal);
    }

    @Override
    public void init_listener() {
        ll_add_termianl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), Add_terminal_activity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void init_data() {

    }

    @Override
    public void onClick(View v, int btnId) {

    }
}
