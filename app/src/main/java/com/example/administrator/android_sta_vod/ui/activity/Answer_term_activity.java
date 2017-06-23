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

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.base.Const;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

/**
 * Created by Administrator on 2017/6/21.
 */
public class Answer_term_activity extends Base_activity{
    private ImageButton term_refuse;
    private ImageButton term_answer;
    private MediaPlayer player;
    private AssetManager assetManager;
    private String type;
    private String key;
    private String value;
    private final static int requestCode = 105;
    private String term_id;
    private String term_name;
    private TextView tv_term_userid;

    @Override
    public int get_layout_res() {
        return R.layout.activity_answer_term;
    }

    @Override
    public void init_view() {
        term_refuse = (ImageButton) findViewById(R.id.btn_term_refuse);
        term_answer= (ImageButton) findViewById(R.id.btn_term_answer);
        tv_term_userid = findView(R.id.tv_term_userid);
    }

    @Override
    public void init_listener() {
        term_refuse.setOnClickListener(this);
        term_answer.setOnClickListener(this);
    }

    @Override
    public void init_data() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        term_id = bundle.getString("term_id");
        term_name = bundle.getString("term_name");
        tv_term_userid.setText(term_name);
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
    @Override
    public void onClick(View v, int btnId) {
       switch (btnId){
           case R.id.btn_term_refuse:
               if (null != player) {
                   if (player.isPlaying()) {
                       player.pause();
                       player.seekTo(0);
                   }
               }
              finish();
               break;
           case R.id.btn_term_answer:
               if (null != player) {
                   if (player.isPlaying()) {
                       player.pause();
                       player.seekTo(0);
                   }
               }
               Intent intent = new Intent(Ui_utils.get_context(), Talk_back_activity.class);
               Bundle bundle = new Bundle();
               bundle.putString(Const.term_id, term_id);
               bundle.putString(Const.term_name, term_name);
               bundle.putString(Const.term_state,Ui_utils.get_string(R.string.calling));
               intent.putExtras(bundle);
               startActivityForResult(intent, requestCode);
               finish();
               break;
       }
    }
    //接收回调消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Avsz_info_event info) {
        type = info.get_type();
        key = info.get_key();
        value = info.get_value();
        Log.d("sadasdasfasdfsdf",info.get_type());

        if("term_call_req_cancel".equals(type)){
            if (null != player) {
                if (player.isPlaying()) {
                    player.pause();
                    player.seekTo(0);
                }
            }
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
