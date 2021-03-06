package com.example.administrator.android_sta_vod;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.broadcast.android.android_sta_jni_official.ndk_wrapper;
import com.example.administrator.android_sta_vod.app.My_application;
import com.example.administrator.android_sta_vod.base.Const;
import com.example.administrator.android_sta_vod.bean.Msg_send;
import com.example.administrator.android_sta_vod.bean.Msg_send_fromMain;
import com.example.administrator.android_sta_vod.bean.User;
import com.example.administrator.android_sta_vod.bean.Users;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.ui.activity.About_us_activity;
import com.example.administrator.android_sta_vod.ui.activity.Answer_term_activity;
import com.example.administrator.android_sta_vod.ui.activity.Answer_user_activity;
import com.example.administrator.android_sta_vod.ui.activity.Audio_activity;
import com.example.administrator.android_sta_vod.ui.activity.Base_activity;
import com.example.administrator.android_sta_vod.ui.activity.Call_back_activity;
import com.example.administrator.android_sta_vod.ui.activity.Real_time_activity;
import com.example.administrator.android_sta_vod.ui.activity.Remote_play_activity;
import com.example.administrator.android_sta_vod.ui.activity.System_activity;
import com.example.administrator.android_sta_vod.ui.activity.Talk_back_activity;
import com.example.administrator.android_sta_vod.ui.activity.Time_broadcast_activity;
import com.example.administrator.android_sta_vod.ui.activity.User_talk_activity;
import com.example.administrator.android_sta_vod.ui.activity.Video_play_activity;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_calluser;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_term_answer;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_text_answer;
import com.example.administrator.android_sta_vod.ui.activity.views.ChatMsg_activity;
import com.example.administrator.android_sta_vod.ui.activity.views.Message_send_activity;
import com.example.administrator.android_sta_vod.utils.NetUtils;
import com.example.administrator.android_sta_vod.utils.SPUtils;
import com.example.administrator.android_sta_vod.utils.T;
import com.example.administrator.android_sta_vod.utils.Ui_utils;
import com.jauker.widget.BadgeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.android_sta_vod.base.Const.pass_word;
import static com.example.administrator.android_sta_vod.base.Const.servers_address;
import static com.example.administrator.android_sta_vod.base.Const.user_name;

public class MainActivity extends Base_activity {

    private final static int requestCode = 105;
    private LinearLayout local_ll_;
    private LinearLayout about_ll_;
    private LinearLayout system_ll_;
    private LinearLayout call_ll_;
    private LinearLayout remote_ll_;
    private LinearLayout video_play_ll_;
    private LinearLayout broadercast_ll_;
    private LinearLayout real_time_ll_;
    private Dialog_text_answer dialog_text_answer;
    private Dialog_calluser dialog_calluser;
    private String tag = "USERS_FRAGMENT";
    private ImageView massage_send_picture;

    private ArrayList<User> user_list;
    private Users users;
    private static final int request_code = 106;
    private String username;
    private MediaPlayer player;
    private AssetManager assetManager;
    private TextView tv_sta_vod_state;
    private Dialog_term_answer dialog_term_answer;
    private BadgeView badgeView;
    private int BadgeCount=1;
    private static String badgecountkey;
    private static String badgecountValue;
    private  List<Msg_send_fromMain> msg_send_fromMains=new ArrayList<>();
    @Override
    public int get_layout_res() {
        return R.layout.activity_main;
    }

    @Override
    public void init_view() {
        tv_sta_vod_state = findView(R.id.tv_sta_vod_state);
        local_ll_ = findView(R.id.main_ll_local);
        about_ll_ = findView(R.id.main_ll_aboutus);
        system_ll_ = findView(R.id.main_ll_system);
        call_ll_=findView(R.id.main_ll_call);
        remote_ll_= findView(R.id.main_ll_remote);
        video_play_ll_= findView(R.id.main_ll_videoplay);
        broadercast_ll_=findView(R.id.main_ll_broadercast);
       real_time_ll_= findView(R.id.main_ll_real_time);
        massage_send_picture=findView(R.id.massage_send_picture);
        //小红点设置
        badgeView = new BadgeView(this);
        badgeView.setTargetView(findViewById(R.id.massage_send_picture));
    }
    @Override
    public void init_listener() {
        video_play_ll_.setOnClickListener(this);
        local_ll_.setOnClickListener(this);
        about_ll_.setOnClickListener(this);
        system_ll_.setOnClickListener(this);
        call_ll_.setOnClickListener(this);
        remote_ll_.setOnClickListener(this);
        broadercast_ll_.setOnClickListener(this);
        real_time_ll_.setOnClickListener(this);
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
                tv_sta_vod_state.setText(Ui_utils.get_string(R.string.off_line_one));
            }
        }else{
            T.show_short(Ui_utils.get_string(R.string.please_goto_login));
            tv_sta_vod_state.setText(Ui_utils.get_string(R.string.off_line_one));
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
            case R.id.main_ll_system: //设置消息发送界面
                if(BadgeCount<=1){
                Intent system_intent = new Intent(getApplicationContext(), Message_send_activity.class);
                startActivity(system_intent);
                badgeView.setHideOnNull(true);
                badgeView.setBadgeCount(0);
                BadgeCount=1;}else{
                    Intent massage_intent=new Intent(getApplicationContext(),ChatMsg_activity.class);
//                    massage_intent.putExtra("badgecountkeyfromMain",badgecountkey);
//                    massage_intent.putExtra("badgecountValuefromMain",badgecountValue);

                    massage_intent.putExtra("badgecountkeyfromMainlist",(Serializable) msg_send_fromMains);

                    startActivity(massage_intent);
                    badgeView.setHideOnNull(true);
                    badgeView.setBadgeCount(0);
                    BadgeCount=1;
                }
                break;
            case R.id.main_ll_call: //呼叫对讲界面
                Intent call_intent=new Intent(getApplicationContext(),Call_back_activity.class);
                startActivity(call_intent);
                break;
            case R.id.main_ll_remote:  //远程点播界面
                Intent remote_intent=new Intent(getApplicationContext(),Remote_play_activity.class);
                startActivity(remote_intent);

                break;
            case  R.id.main_ll_videoplay://视频点播界面
                Intent video_play_intent=new Intent(getApplicationContext(),Video_play_activity.class);
                startActivity(video_play_intent);
                break;
            case R.id.main_ll_broadercast:  //定时广播界面
                Intent broadercast_intent=new Intent(getApplicationContext(),Time_broadcast_activity.class);
                startActivity(broadercast_intent);
                break;
            case R.id.main_ll_real_time: //实时采播界面
                Intent real_time_intent=new Intent(getApplicationContext(),Real_time_activity.class);
                startActivity(real_time_intent);
                break;
        }
    }

    @Override
    public void onEventMainThread(Avsz_info_event info) {
       super.onEventMainThread(info);
        String type = info.get_type();
        String key = info.get_key();
        String value = info.get_value();
        Log.d(tag+"main", "type" + type + "key" + key + "value" + value);
        KeyguardManager km = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        boolean showingLocked = km.inKeyguardRestrictedInputMode();
        if("usr_msg".equals(type)){
            badgeView.setBadgeCount(BadgeCount);
            BadgeCount++;
            badgecountkey=key;
            badgecountValue=value;

            Msg_send_fromMain msg_send_fromMain=new Msg_send_fromMain(badgecountkey,badgecountValue);
            msg_send_fromMains.add(msg_send_fromMain);
        }
       /*if ("usr_call_req".equals(type)) {

            if(showingLocked){
                Intent intent=new Intent(this,Answer_user_activity.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }else {
                if(null == dialog_text_answer){
                    show_ansdialog(type, key, value);
                    if (null != player) {
                        if (!player.isPlaying()) {
                            player.start();
                        }
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
        }*/
        if ("tcp".equals(type))
        {
            if ("close_conn_i".equals(key)||"close".equals(key) || "timeout".equals(key) || "finished".equals(key)) {
                tv_sta_vod_state.setText(Ui_utils.get_string(R.string.off_line_one));
            }
        }
        if ("login_ret_usr_list".equals(type) && value.contains("ok"))
        {
            tv_sta_vod_state.setText(Ui_utils.get_string(R.string.on_line_one));
        }
       if ("close_conn_i".equals(key)||"close".equals(key) || "timeout".equals(key) || "finished".equals(key)) {
            tv_sta_vod_state.setText(Ui_utils.get_string(R.string.off_line_one));
        }else{
            tv_sta_vod_state.setText(Ui_utils.get_string(R.string.on_line_one));
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
      /*  if("term_call_req".equals(type)){
            if(showingLocked){
                Intent intent=new Intent(this,Answer_term_activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("term_id", key);
                bundle.putString("term_name", value);
                intent.putExtras(bundle);
                startActivity(intent);
            }else {
                if (null != player) {
                    if (!player.isPlaying()) {
                        player.start();
                    }
                }
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
//            dialog_term_answer.dismiss();
            dialog_term_answer=null;
        }*/
    }

   /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Avsz_info_event info) {
        String type = info.get_type();
        String key = info.get_key();
        String value = info.get_value();
        Log.d(tag+"main", "type" + type + "key" + key + "value" + value);
        KeyguardManager km = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        boolean showingLocked = km.inKeyguardRestrictedInputMode();
        if("usr_msg".equals(type)){
            badgeView.setBadgeCount(BadgeCount);
            BadgeCount++;
        }
        if ("usr_call_req".equals(type)) {

            if(showingLocked){
                Intent intent=new Intent(this,Answer_user_activity.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }else {
                if(null == dialog_text_answer){
                    show_ansdialog(type, key, value);
                    if (null != player) {
                        if (!player.isPlaying()) {
                            player.start();
                        }
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
        if ("tcp".equals(type))
        {
            if ("close_conn_i".equals(key)||"close".equals(key) || "timeout".equals(key) || "finished".equals(key)) {
                tv_sta_vod_state.setText(Ui_utils.get_string(R.string.off_line_one));
            }
        }
        if ("login_ret_usr_list".equals(type) && value.contains("ok"))
        {
            tv_sta_vod_state.setText(Ui_utils.get_string(R.string.on_line_one));
        }
        if ("close_conn_i".equals(key)||"close".equals(key) || "timeout".equals(key) || "finished".equals(key)) {
            tv_sta_vod_state.setText(Ui_utils.get_string(R.string.off_line_one));
        }else{
            tv_sta_vod_state.setText(Ui_utils.get_string(R.string.on_line_one));
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
            if(showingLocked){
                Intent intent=new Intent(this,Answer_term_activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("term_id", key);
                bundle.putString("term_name", value);
                intent.putExtras(bundle);
                startActivity(intent);
            }else {
                if (null != player) {
                    if (!player.isPlaying()) {
                        player.start();
                    }
                }
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
//            dialog_term_answer.dismiss();
            dialog_term_answer=null;
        }

    }*/
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
                startActivityForResult(intent, requestCode);
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
                if(dialog_term_answer!=null){
                    dialog_term_answer.dismiss();
                    dialog_term_answer=null;
                }

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);

Log.d("sssssss","sdadsadsadsd");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dialog_text_answer!=null){
            dialog_text_answer.dismiss();
            dialog_text_answer=null;
        }
        if(dialog_term_answer!=null){
            dialog_term_answer.dismiss();
            dialog_term_answer=null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        System.out.println("ondestry");
        ndk_wrapper.instance().avsz_fini();
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
