package com.example.administrator.android_sta_vod.ui.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.broadcast.android.android_sta_jni.ndk_wrapper;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.adapter.User_list_adapter;
import com.example.administrator.android_sta_vod.bean.User;
import com.example.administrator.android_sta_vod.bean.Users;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_calluser;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_text_answer;
import com.example.administrator.android_sta_vod.utils.Beacon_util;
import com.example.administrator.android_sta_vod.utils.T;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/3/22 0022.
 */
public class Call_back_activity extends Base_activity {
    private GridView grid_call_view;


    private String tag = "USERS_FRAGMENT";
    private static final int request_code = 106;
    private Dialog_calluser dialog_calluser;

    public static final String ON_LINE="1";//在线
    public static final String OFF_LINE="0";//离线
    public static final String PLAY="2";//播放
    public static final String TALK_BACK_ING="3";//对讲中T
    public static final String CALL_PLAY="4";//对讲播放

    private Dialog_text_answer dialog_text_answer;

    private MediaPlayer player;
    private AssetManager assetManager;
    private List<User> userList;
    private TextView tv_title;
    private User_list_adapter user_list_adapter;

    @Override
    public int get_layout_res() {
        return R.layout.activity_call_back;
    }

    @Override
    public void init_view() {
        grid_call_view = findView(R.id.my_gridview);
        tv_title = findView(R.id.tv_title);

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
    public void init_listener() {
        grid_call_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String  user_name=  userList.get(position).getName();
                String  user_state= userList.get(position).getStatus();

                user_list_adapter.notifyDataSetChanged();
                if(ON_LINE.equals(user_state)){
                    show_calldialog(user_name,user_state);
                    ndk_wrapper.instance().avsz_usr_call_req(user_name);
                }

            }
        });
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
      EventBus.getDefault().register(this);
        Beacon_util.login();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }



    @Override
    protected void onStop() {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Users users) {
        userList = users.getUsers();
        String s = users.getUsers().toString();

        user_list_adapter = new User_list_adapter(userList);

        grid_call_view.setAdapter(user_list_adapter);
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
        if ("usr_call_req_ret".equals(type)) {
            Log.d(tag, "usr_call_req_ret");
            dismiss_calldialog(type, key, value);
        }
        if ("usr_call_req_timeout".equals(type)) {
            Log.d(tag, "usr_call_req_timeout");
            dismiss_calldialog(type, key, value);
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
        if ("usr_online".equals(type)) {
            tv_title.setText(R.string.on_line);
        }
        if ("close".equals(key) || "timeout".equals(key) || "finished".equals(key)) {
            tv_title.setText(R.string.off_line);
        }
        if (null != userList) {
            if ("usr_offline".equals(type)) {

                for (int i = 0; i < userList.size(); i++) {

                    if (key.equals(userList.get(i).getName())) {
                        userList.get(i).setStatus("0");
                        Log.d(tag, "usr_offline" + userList.get(i).getName());
                        user_list_adapter.notifyDataSetChanged();
                    }
                }

            }
            if ("usr_online".equals(type)) {
                tv_title.setText(R.string.on_line);
                for (int i = 0; i < userList.size(); i++) {
                    if (key.equals(userList.get(i).getName())) {
                        userList.get(i).setStatus("1");
                        Log.d(tag, "usr_online" + userList.get(i).getName());
                        user_list_adapter.notifyDataSetChanged();
                    }
                }

            }
            if ("close".equals(key) ) {
                tv_title.setText(R.string.off_line);
                Log.d(tag, "finished++++" );
                userList.clear();
              /*  for (int i = 0; i < userList.size(); i++) {
                    if (key.equals(userList.get(i).getName())) {
                        userList.get(i).setStatus("0");
                        Log.d(tag, "finished" );
                    }
                }*/
                user_list_adapter.notifyDataSetChanged();
            }
        }

    }

    //呼叫方弹框
    private void show_calldialog(String username, String userstate) {
        dialog_calluser=new Dialog_calluser(this, R.style.MyDialog, new Dialog_calluser.On_cancel_listener() {
           @Override
           public void onClick(View v) {
               ndk_wrapper.instance().avsz_usr_call_req_cancel(username);
               dialog_calluser.dismiss();
           }
       }
       );
        dialog_calluser.show();
        dialog_calluser.set_user(username);

    }

    //呼叫方取消弹框
    private void dismiss_calldialog(String type, String key, String value) {
        if ("1".equals(value)) {
            T.show_short(Ui_utils.get_string(R.string.opposite_busy));
        }
        dialog_calluser.dismiss();
        dialog_calluser=null;
        Log.d(tag, "dismiss_calldialog");
        if ("2".equals(value)) {
            Intent intent = new Intent(this, User_talk_activity.class);
            intent.putExtra("user_name", key);
            startActivityForResult(intent, request_code);
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
}

