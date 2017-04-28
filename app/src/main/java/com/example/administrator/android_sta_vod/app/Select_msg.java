package com.example.administrator.android_sta_vod.app;

import java.util.ArrayList;

/**
 * Created by AVSZ on 2017/4/26.
 */

public class Select_msg {
    private static Select_msg mIns;
    private static ArrayList<String> mTerm_is_list = new ArrayList<>();
    public static Select_msg instance(){
        if(null == mIns){
            mIns = new Select_msg();
        }
        return mIns;
    }

    public ArrayList<String> get_speak_terms(){
        return mTerm_is_list;
    }
}

