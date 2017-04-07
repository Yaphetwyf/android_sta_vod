package com.example.administrator.android_sta_vod;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.broadcast.android.android_sta_jni.ndk_wrapper;
import com.example.administrator.android_sta_vod.app.My_application;
import com.example.administrator.android_sta_vod.base.Global;
import com.example.administrator.android_sta_vod.bean.User;
import com.example.administrator.android_sta_vod.bean.Users;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.ui.activity.About_us_activity;
import com.example.administrator.android_sta_vod.ui.activity.Audio_activity;
import com.example.administrator.android_sta_vod.ui.activity.Base_activity;
import com.example.administrator.android_sta_vod.ui.activity.Call_back_activity;
import com.example.administrator.android_sta_vod.ui.activity.Remote_play_activity;
import com.example.administrator.android_sta_vod.ui.activity.System_activity;
import com.example.administrator.android_sta_vod.ui.activity.User_talk_activity;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_answer;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_calluser;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_text_answer;
import com.example.administrator.android_sta_vod.utils.Beacon_util;
import com.example.administrator.android_sta_vod.utils.NetUtils;
import com.example.administrator.android_sta_vod.utils.SPUtils;
import com.example.administrator.android_sta_vod.utils.T;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.administrator.android_sta_vod.base.Const.pass_word;
import static com.example.administrator.android_sta_vod.base.Const.servers_address;
import static com.example.administrator.android_sta_vod.base.Const.user_name;

public class MainActivity extends Base_activity {


    private LinearLayout local_ll_;
    private LinearLayout about_ll_;
    private LinearLayout system_ll_;
    private LinearLayout call_ll_;
    private LinearLayout remote_ll_;
    private Dialog_text_answer dialog_text_answer;
    private Dialog_calluser dialog_calluser;
    private String tag = "USERS_FRAGMENT";
    private Dialog_answer dialog_answer;
    private ArrayList<User> user_list;
    private Users users;
    private static final int request_code = 106;
    private String username;
    private MediaPlayer player;
    private AssetManager assetManager;
    private FrameLayout fl_off_line;

    @Override
    public int get_layout_res() {
        return R.layout.activity_main;
    }

    @Override
    public void init_view() {
        fl_off_line = findView(R.id.fl_off_line);
        local_ll_ = findView(R.id.main_ll_local);
        about_ll_ = findView(R.id.main_ll_aboutus);
        system_ll_ = findView(R.id.main_ll_system);
        call_ll_=findView(R.id.main_ll_call);
        remote_ll_= findView(R.id.main_ll_remote);
    }

    @Override
    public void init_listener() {
        local_ll_.setOnClickListener(this);
        about_ll_.setOnClickListener(this);
        system_ll_.setOnClickListener(this);
        call_ll_.setOnClickListener(this);
        remote_ll_.setOnClickListener(this);
    }

    @Override
    public void init_data() {
        login_main();

    }

    private void login_main() {
        String username = SPUtils.getString(Ui_utils.get_context(), user_name);
        String password = SPUtils.getString(Ui_utils.get_context(), pass_word);
        String server_address = SPUtils.getString(Ui_utils.get_context(), servers_address);
        boolean net_connect = NetUtils.isConnected(Ui_utils.get_context());
        if(!(TextUtils.isEmpty(username) && TextUtils.isEmpty(password) && TextUtils.isEmpty(server_address))){
            if(net_connect){
                ndk_wrapper.instance().avsz_init(server_address, (short) 1220, username, password);
            }else {
                T.show_short(Ui_utils.get_string(R.string.net_connect_failure));
                fl_off_line.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        create_player();
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(tag,"onStop");

        if (null != player) {
            if (player.isPlaying()) {
                player.pause();
                player.seekTo(0);
                player.release();
            }
            player = null;
        }
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v, int btnId) {
        switch (btnId) {
            case R.id.main_ll_local: //本地播放界面
                Intent intent = new Intent(getApplicationContext(), Audio_activity.class);
                startActivity(intent);
                break;
            case R.id.main_ll_aboutus://关于我们界面
                Intent about_intent = new Intent(getApplicationContext(), About_us_activity.class);
                startActivity(about_intent);
                break;
            case R.id.main_ll_system: //系统设置界面
                Intent system_intent = new Intent(getApplicationContext(), System_activity.class);
                startActivity(system_intent);
                break;
            case R.id.main_ll_call:  //呼叫对讲界面
                Intent call_intent=new Intent(getApplicationContext(),Call_back_activity.class);
                startActivity(call_intent);
                break;
            case R.id.main_ll_remote:  //远程点播界面
                Intent remote_intent=new Intent(getApplicationContext(),Remote_play_activity.class);
                startActivity(remote_intent);
                break;
        }
    }
    //接收回调消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Avsz_info_event info) {
        String type = info.get_type();
        String key = info.get_key();
        String value = info.get_value();
        Log.d(tag, "type" + type + "key" + key + "value" + value);
        if ("usr_call_req".equals(type)) {
            if(null == dialog_text_answer){
                show_ansdialog(type, key, value);
                if (null != player) {
                    if (!player.isPlaying()) {
                        player.start();
                    }
                }
            }
        }
        if ("usr_call_req_cancel".equals(type)) {
            if (null != dialog_text_answer) {
                dialog_text_answer.dismiss();
                dialog_text_answer = null;
            }
            if (null != player) {
                if (player.isPlaying()) {
                    player.pause();
                    player.seekTo(0);
                }
            }
        }
        if ("close".equals(key) || "timeout".equals(key) || "finished".equals(key)) {
           fl_off_line.setVisibility(View.VISIBLE);
        }else{
            fl_off_line.setVisibility(View.GONE);
        }
        if (null != users) {
            if ("usr_offline".equals(type)) {
                for (int i = 0; i < users.getUsers().size(); i++) {

                    if (key.equals(users.getUsers().get(i).getName())) {
                        users.getUsers().get(i).setStatus("0");

                        Log.d(tag, "usr_online" + users.getUsers().get(i).getName());

                    }
                }
            }
            if ("usr_online".equals(type)) {
                for (int i = 0; i < users.getUsers().size(); i++) {
                    if (key.equals(users.getUsers().get(i).getName())) {
                        users.getUsers().get(i).setStatus("1");

                    }
                }
            }
        }

    }


    //显示回答的Dialog
    private void show_ansdialog(String type, String key, String value) {
        dialog_text_answer = new Dialog_text_answer(this);
        dialog_text_answer.set_on_refuse_listener(new Dialog_text_answer.On_refuse_listener() {
            @Override
            public void refuse_listener() {
                ndk_wrapper.instance().avsz_usr_call_req_ret(key, 0);
                if (null != player) {
                    if (player.isPlaying()) {
                        player.pause();
                        player.seekTo(0);
                    }
                }
                dialog_text_answer.dismiss();
                dialog_text_answer=null;
            }
        });
        dialog_text_answer.set_on_answer_listener(new Dialog_text_answer.On_answer_listener() {
            @Override
            public void answer_listener() {
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
                dialog_text_answer.dismiss();
                dialog_text_answer=null;
            }
        });
        dialog_text_answer.set_on_transfer_listener(new Dialog_text_answer.On_transfer_listener() {
            @Override
            public void transfer_listener() {
                ndk_wrapper.instance().avsz_usr_call_req_ret(key, 0);
                if (null != player) {
                    if (player.isPlaying()) {
                        player.pause();
                        player.seekTo(0);
                    }
                }
                dialog_text_answer.dismiss();
                dialog_text_answer=null;
            }
        });
        dialog_text_answer.show();
        dialog_text_answer.set_user(key);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dialog_text_answer!=null){
            dialog_text_answer.dismiss();
            dialog_text_answer=null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    private long firstTime = 0;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    //如果两次按键时间间隔大于2秒，则不退出
                   T.show_short(Ui_utils.get_string(R.string.exit_login));
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {                                                    //两次按键小于2秒时，退出应用
                    My_application.getInstance().exit();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
