package com.example.administrator.android_sta_vod.ui.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.broadcast.android.android_sta_jni_official.ndk_wrapper;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

/**
 * Created by Administrator on 2017/6/21.
 */

public class Answer_user_activity extends Base_activity {

    private ImageButton ibtn_refuse;
    private ImageButton ibtn_answer;
    private ImageButton ibtn_transfer;
    private MediaPlayer player;
    private AssetManager assetManager;
    private String key;
    private TextView tv_userid;

    @Override
    public int get_layout_res() {
        return R.layout.activity_answer_user;
    }

    @Override
    public void init_view() {
        ibtn_refuse = findView(R.id.btn_refuse);
        ibtn_answer = findView(R.id.btn_answer);
        ibtn_transfer = findView(R.id.btn_transfer);
        tv_userid = findView(R.id.tv_userid);
    }

    @Override
    public void init_listener() {
        ibtn_refuse.setOnClickListener(this);
        ibtn_answer.setOnClickListener(this);
        ibtn_transfer.setOnClickListener(this);
    }

    @Override
    public void init_data() {
        key = getIntent().getStringExtra("key");
        Log.d("getIntentString","key:"+key);
        tv_userid.setText(key);
    }

    @Override
    public void onClick(View v, int btnId) {
      switch (btnId){
          case R.id.btn_refuse:
              //拒绝
              ndk_wrapper.instance().avsz_usr_call_req_ret(key, 0);
              if (null != player) {
                  if (player.isPlaying()) {
                      player.pause();
                      player.seekTo(0);
                  }
              }
              finish();
              break;
          case R.id.btn_answer:
              ndk_wrapper.instance().avsz_usr_call_req_ret(key, 2);
              if (null != player) {
                  if (player.isPlaying()) {
                      player.pause();
                      player.seekTo(0);
                  }
              }
              Intent intent = new Intent(getApplicationContext(), User_talk_activity.class);
              intent.putExtra("user_name", key);
              startActivity(intent);

              finish();
              break;
          case R.id.btn_transfer:

              break;
      }
    }

    @Override
    protected void onStart() {
        super.onStart();
        create_player();
    }

    private void create_player() {
        //准备播放音乐
        player = new MediaPlayer();
        assetManager =this.getAssets();
        try {
            AssetFileDescriptor fileDescriptor = assetManager.openFd("call.mp3");
            player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (null != player) {
            if (player.isPlaying()) {
                player.pause();
                player.seekTo(0);
                player.release();
            }
            player = null;
        }
    }
    //接收回调消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Avsz_info_event info) {
        String type = info.get_type();
        String key = info.get_key();
        String value = info.get_value();
        if ("usr_call_req_ret".equals(type)) {
           finish();
        }
        if ("usr_call_req_timeout".equals(type)) {
           finish();
        }
        if ("usr_call_req_cancel".equals(type)) {
          finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
