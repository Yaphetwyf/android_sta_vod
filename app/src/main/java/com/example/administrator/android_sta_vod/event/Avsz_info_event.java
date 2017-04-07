package com.example.administrator.android_sta_vod.event;

/**
 * Created by AVSZ on 2016/12/23.
 */

public class Avsz_info_event {

    private String type;    //连接类型
    private String key;     //信息类型
    private String value;  //信息

    public Avsz_info_event(String type, String key, String value){
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public String get_type(){
        return type;
    }

    public String get_key(){
        return key;
    }

    public String get_value(){
        return value;
    }
}
