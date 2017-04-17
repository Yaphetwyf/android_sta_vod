package com.example.administrator.android_sta_vod.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.broadcast.android.android_sta_jni.ndk_wrapper;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.app.My_application;
import com.example.administrator.android_sta_vod.base.Const;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.utils.Beacon_util;
import com.example.administrator.android_sta_vod.utils.SPUtils;
import com.example.administrator.android_sta_vod.utils.T;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by Administrator on 2017/3/20 0020.
 */
public class System_activity extends Base_activity {

    private EditText et_user_name;
    private EditText et_pass_word;
    private EditText et_address;
    private Button btn_login,btn_cancel,btn_exit;
    private TextView title;
    private String tempvalue;
public String state="";
    @Override
    public int get_layout_res() {
        return R.layout.activity_system;

    }

    @Override
    public void init_view() {
        et_user_name = findView(R.id.et_username);
        et_pass_word = findView(R.id.et_password);
        et_address=  findView(R.id.et_servers_address);
        title = findView(R.id.tv_title);
        btn_login = findView(R.id.btn_login);
        btn_cancel= findView(R.id.btn_cancel);
        btn_exit = findView(R.id.btn_exit);
    }

    @Override
    public void init_listener() {

    }

    @Override
    public void init_data() {

    }

    @Override
    public void onClick(View v, int btnId) {
        switch (btnId){
            case R.id.btn_login:
               login();
                break;
            case R.id.btn_cancel:
                ndk_wrapper.instance().avsz_fini();
                break;
            case R.id.btn_exit:
                new AlertDialog.Builder(this)
                        .setTitle(Ui_utils.get_string(R.string.exit_procedure))
                        .setMessage(R.string.are_you_sure)
                        .setNegativeButton(Ui_utils.get_string(R.string.no), null)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                My_application.getInstance().exit();
                            }
                        })
                        .show();
                break;
            }
    }
/*
* 登入
*/
    private void login() {
        String user_name= et_user_name.getText().toString();
        String pass_word= et_pass_word.getText().toString();
        String servers_address= et_address.getText().toString();
        if(!TextUtils.isEmpty(user_name)){
            SPUtils.putString(Ui_utils.get_context(), Const.user_name, user_name);
        }else{
            T.show_short(Ui_utils.get_string(R.string.toast_username));
            return;
        }
        if(!TextUtils.isEmpty(pass_word)){
            SPUtils.putString(Ui_utils.get_context(), Const.pass_word, pass_word);
        }else{
            T.show_short(Ui_utils.get_string(R.string.toast_password));
            return;
        }
        if(!TextUtils.isEmpty(servers_address)){
            SPUtils.putString(Ui_utils.get_context(), Const.servers_address, servers_address);
        }else{
            T.show_short(Ui_utils.get_string(R.string.toast_address));
            return;
        }
        Beacon_util.login();
    }



    //接收eventbus传来的信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Avsz_info_event event) {
        String type = event.get_type();
        String key = event.get_key();
        String value = event.get_value();

        Log.d("onEventMainThread", type + "================" + key+"=============="+value);
        if (value.contains("invalid_pwd")) {
            T.show_short(Ui_utils.get_string(R.string.check_username_password));
        }
        if ("remote_endpoint: Transport endpoint is not connected".equals(value)) {
            T.show_short(Ui_utils.get_string(R.string.server_connect_failure));
        }

            if ("usr_online".equals(type)) {
                Log.d("login", "login_after");
                title.setText(R.string.on_line);
        }

            if ("close".equals(key) || "timeout".equals(key) || "finished".equals(key)) {
                title.setText(R.string.off_line);
        }

//        if("tcp".equals(type) && "finished".equals(key)){
//            T.show_short(Ui_utils.get_string(R.string.lost_connection));
//            Beacon_util.login();
//        }

        if ("xml".equals(key)) {

        }

    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        String sp_username = SPUtils.getString(Ui_utils.get_context(), Const.user_name);
        String sp_password = SPUtils.getString(Ui_utils.get_context(), Const.pass_word);
        String sp_address = SPUtils.getString(Ui_utils.get_context(), Const.servers_address);
        if (!TextUtils.isEmpty(sp_username)) {
            et_user_name.setText(sp_username);
        }
        if (!TextUtils.isEmpty(sp_password)) {
            et_pass_word.setText(sp_password);
        }
        if (!TextUtils.isEmpty(sp_address)) {
            et_address.setText(sp_address);
        }
        Beacon_util.login();
    }
    protected void onDestroy() {
        super.onDestroy();
        // 解绑服务
        EventBus.getDefault().unregister(this);
    }
}
