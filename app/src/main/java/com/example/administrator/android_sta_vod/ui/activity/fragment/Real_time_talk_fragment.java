package com.example.administrator.android_sta_vod.ui.activity.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.adapter.Terminal_expandlist_adapter;

/**
 * Created by Administrator on 2017/4/26.
 */

public class Real_time_talk_fragment extends BaseFragment {

    private ImageView iv_real_time;
   private Terminal_expandlist_adapter  adapter;
    private TextView tv_real_time;

    @Override
    public int get_layout_res() {
        return R.layout.fragment_real_time_talk;
    }

    @Override
    public void init_view() {
        iv_real_time = findView(R.id.iv_real_time);
        tv_real_time = findView(R.id.tv_real_time_start);
    }

    @Override
    public void init_listener() {
      iv_real_time.setOnClickListener(this);
    }

    @Override
    public void init_data() {

    }

    @Override
    public void onClick(View v, int btnId) {
   switch (btnId){
     case   R.id.iv_real_time:

         break;
   }
    }
}
