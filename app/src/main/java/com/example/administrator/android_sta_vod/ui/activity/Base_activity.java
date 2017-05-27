package com.example.administrator.android_sta_vod.ui.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.broadcast.android.android_sta_jni.ndk_wrapper;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.app.My_application;
import com.example.administrator.android_sta_vod.app.Net_data;
import com.example.administrator.android_sta_vod.base.Const;
import com.example.administrator.android_sta_vod.base.Global;
import com.example.administrator.android_sta_vod.bean.Users;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.inference.IUIOperation;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_term_answer;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_text_answer;
import com.example.administrator.android_sta_vod.utils.Ui_utils;
import com.example.administrator.android_sta_vod.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;


public abstract class Base_activity extends AppCompatActivity implements IUIOperation {
    private Dialog_text_answer dialog_text_answer;
    private Dialog_term_answer dialog_term_answer;
    private static final int request_code = 106;
    private String tag = "USERS_FRAGMENT";
    private Users users;
    private MediaPlayer player;
    private AssetManager assetManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(get_layout_res());
        My_application.getInstance().addActivity(this);
        // 查找Activity布局中所有的按钮(Button或ImageButton)并设置点击事件
        // android.R.id.content: 系统的一个根布局，activity布局会作为子控件添加到该父控件中
        View root = findViewById(android.R.id.content);
        Utils.findButtonsAndSetOnClickListener(root, this);
        init_view();
        init_listener();
        init_data();
        Net_data.instance();
        EventBus.getDefault().register(this);
    }

    /** 设置Activity界面的标题 */
    public void setPageTitle(String  title) {
        TextView textView = findView(R.id.tv_title);

        if (textView != null) {
            textView.setText(title);
            textView.setTextColor(Color.BLACK);
        }
    }
    /** 设置Activity界面的标题 */
    public void setPageTitle(int title) {
        TextView textView = findView(R.id.tv_title);

        if (textView != null) {
            textView.setText(title);
            textView.setTextColor(Color.BLACK);
        }
    }
    /** 查找按钮，可以省略强制类型转换 */
    public <T> T findView(int id) {
        T view = (T) findViewById(id);
        return view;
    }

    public void showToast(String msg) {
        Global.showToast(msg);
    }

    // 控件的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:         // 点击了标题栏左上角的返回按钮
                finish();               // 销毁当前Activity
                break;
            default:
                onClick(v, v.getId());  // 由子Activity自已处理
                break;
        }
    }
    //接收回调消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Avsz_info_event info) {
        String type = info.get_type();
        String key = info.get_key();
        String value = info.get_value();
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
        if("term_call_req".equals(type)){
            if (null != player) {
                if (!player.isPlaying()) {
                    player.start();
                }
            }
            if(dialog_term_answer==null){
                show_trem_ansdialog(type, key, value);
            }
        }
        if("term_call_req_cancel".equals(type)){
            if (null != player) {
                if (player.isPlaying()) {
                    player.pause();
                    player.seekTo(0);
                }
            }
            if(dialog_term_answer!=null){
                dialog_term_answer.dismiss();
                dialog_term_answer=null;
            }

        }
    }

    private void show_trem_ansdialog(String type, String key, String value) {
        dialog_term_answer = new Dialog_term_answer(this);
        dialog_term_answer.set_on_answer_listener(new Dialog_term_answer.On_answer_listener() {
            @Override
            public void answer_listener() {
                if (null != player) {
                    if (player.isPlaying()) {
                        player.pause();
                        player.seekTo(0);
                    }
                }
                Intent intent = new Intent(Ui_utils.get_context(), Talk_back_activity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Const.term_id, key);
                bundle.putString(Const.term_name, value);
                bundle.putString(Const.term_state,Ui_utils.get_string(R.string.calling));
                intent.putExtras(bundle);
                startActivityForResult(intent, request_code);
                dialog_term_answer.dismiss();
                dialog_term_answer=null;
            }
        });
        dialog_term_answer.set_on_refuse_listener(new Dialog_term_answer.On_refuse_listener() {
            @Override
            public void refuse_listener() {
                if (null != player) {
                    if (player.isPlaying()) {
                        player.pause();
                        player.seekTo(0);
                    }
                }
                dialog_term_answer.dismiss();
                dialog_term_answer=null;
            }
        });
        dialog_term_answer.show();
        dialog_term_answer.set_user(value);
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (null != player && player.isPlaying()) {
            player.stop();
        }
        player.release();
        player = null;
    }
}











