package com.example.administrator.android_sta_vod.utils;

import android.text.TextUtils;


import com.broadcast.android.android_sta_jni.ndk_wrapper;
import com.example.administrator.android_sta_vod.R;


import static com.example.administrator.android_sta_vod.base.Const.pass_word;
import static com.example.administrator.android_sta_vod.base.Const.servers_address;
import static com.example.administrator.android_sta_vod.base.Const.user_name;

/**
 * Created by AVSZ on 2017/3/10.
 */

public class Beacon_util {
    public static void login(){
        String username = SPUtils.getString(Ui_utils.get_context(), user_name);
        String password = SPUtils.getString(Ui_utils.get_context(), pass_word);
        String server_address = SPUtils.getString(Ui_utils.get_context(), servers_address);
        boolean net_connect = NetUtils.isConnected(Ui_utils.get_context());
        if(!(TextUtils.isEmpty(username) && TextUtils.isEmpty(password) && TextUtils.isEmpty(server_address))){
            if(net_connect){
                ndk_wrapper.instance().avsz_init(server_address, (short) 1220, username, password);
            }else {
                T.show_short(Ui_utils.get_string(R.string.net_connect_failure));
            }
        }
    }
}
