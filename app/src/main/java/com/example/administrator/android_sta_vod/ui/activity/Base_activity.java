package com.example.administrator.android_sta_vod.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.app.My_application;
import com.example.administrator.android_sta_vod.base.Global;
import com.example.administrator.android_sta_vod.inference.IUIOperation;
import com.example.administrator.android_sta_vod.utils.Utils;


public abstract class Base_activity extends AppCompatActivity implements IUIOperation {

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
}











