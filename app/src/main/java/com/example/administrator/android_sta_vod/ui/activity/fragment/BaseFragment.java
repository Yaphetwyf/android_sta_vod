package com.example.administrator.android_sta_vod.ui.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.base.Global;
import com.example.administrator.android_sta_vod.inference.IUIOperation;
import com.example.administrator.android_sta_vod.ui.activity.Base_activity;
import com.example.administrator.android_sta_vod.utils.Utils;


public abstract class BaseFragment extends Fragment implements IUIOperation {

    public Base_activity mActivity;

    protected View mRoot;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (Base_activity) getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // 避免Fragment界面切换时，重复多次inflate布局和初始化数据
        if (mRoot == null) {

            mRoot = Global.inflate(get_layout_res(), container);
            // 查找布局中所有的按钮(Button或ImageButton)并设置点击事件
            Utils.findButtonsAndSetOnClickListener(mRoot, this);
            init_view();
            init_listener();
            init_data();
        }
        return mRoot;
    }

    /** 查找按钮，可以省略强制类型转换 */
    public <T> T findView(int id) {
        T view = (T) mRoot.findViewById(id);
        return view;
    }


    // 控件的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:         // 点击了标题栏左上角的返回按钮
                mActivity.finish();     // 销毁管理当前Fragment的Activity界面
                break;
            default:
                onClick(v, v.getId());  // 由子Activity自已处理
                break;
        }
    }

    public void showToast(String msg) {
        Global.showToast(msg);
    }

}


















