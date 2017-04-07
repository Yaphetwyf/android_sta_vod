package com.example.administrator.android_sta_vod.inference;

import android.view.View;

/**
 * UI操作方法封装
 */
public interface IUIOperation extends View.OnClickListener {


    /** 返回Activity或者Fragment的布局文件 */
    int get_layout_res();

    /** 查找子控件 */
    void init_view();

    /** 初始化监听器 */
    void init_listener();

    /** 初始化数据 */
    void init_data();

    /** 点击事件 */
    void onClick(View v, int btnId);
}
