package com.example.administrator.android_sta_vod.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.adapter.Find_tab_Adapter;
import com.example.administrator.android_sta_vod.app.Net_data;
import com.example.administrator.android_sta_vod.base.Global;
import com.example.administrator.android_sta_vod.ui.activity.fragment.Real_time_talk_fragment;
import com.example.administrator.android_sta_vod.ui.activity.fragment.Terminal_fragment;
import com.example.administrator.android_sta_vod.utils.Beacon_util;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/4/26.
 */
public class Real_time_activity extends Base_activity {

    /** 选项卡 */
    private TextView[] mTabs = new TextView[2];

    /** 当前选中的选项卡 */
    private TextView mCurrentTab;

    /** 选项卡指示线 */
    private View mTabIndicator;

    /** 一个选项卡的宽度 */
    private int mTabWidth;

    private ViewPager viewPager;


    @Override
    public int get_layout_res() {
        return R.layout.activity_real_time;
    }

    @Override
    public void init_view() {
        initTabs();
        initViewPager();
        initTabIndicator();
    }

    /** 初始化选项卡指示线 */
    private void initTabIndicator() {
        mTabIndicator = findViewById(R.id.tab_indicator);
        mTabWidth = Global.mScreenWidth / mTabs.length;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                mTabIndicator.getLayoutParams();
        params.width = mTabWidth;   // 设置指示线的宽度
    }

    private void initViewPager() {
        viewPager = findView(R.id.view_pager);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new Real_time_talk_fragment());
        fragments.add(new Terminal_fragment());

        PagerAdapter mAdapter = new Find_tab_Adapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(mAdapter);
    }

    /** 初始化选项卡 */
    private void initTabs() {
        mTabs[0] = findView(R.id.tv_video);
        mTabs[1] = findView(R.id.tv_audio);

        mCurrentTab = mTabs[0];
        mCurrentTab.setSelected(true);           // 高亮显示
        mCurrentTab.setScaleX(1.2f);             // 放大到1.2倍
        mCurrentTab.setScaleY(1.2f);
    }

    @Override
    public void init_listener() {
        findViewById(R.id.tv_video).setOnClickListener(this);
        findViewById(R.id.tv_audio).setOnClickListener(this);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override // 滑动ViewPager时调用，多次调用
            public void onPageScrolled(int position,
                                       float percent,
                                       int positionOffsetPixels) {
                // System.out.println("--------percent: " + percent);
                // 滑动指示线
                scrollTabIndicator(position, percent);
            }

            @Override   // 界面切换后回调
            public void onPageSelected(int position) {
                onTabSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 滑动指示线
    */
    private void scrollTabIndicator(int position, float percent) {
        int marginLeft = (int) (mTabWidth * position + mTabWidth * percent);


        // 设置水平方向的平移
        mTabIndicator.setTranslationX(marginLeft);
    }

    @Override
    public void init_data() {

    }

    @Override
    public void onClick(View v, int btnId) {
        switch (btnId) {
            case R.id.tv_video:             // 视频
                // onTabSelected(0);
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_audio:             // 音频
                // onTabSelected(1);
                viewPager.setCurrentItem(1);
                break;
        }
    }

    /**
     * 选项切换了, 改变选项卡的显示状态
     *
     * @param position 选中的选项卡的位置
     */
    private void onTabSelected(int position) {
        // 上一次选中的选项卡要取消高亮
        mCurrentTab.setSelected(false);         // 取消高亮，会根据selected状态选择显示

        ViewCompat.animate(mCurrentTab).scaleX(1f).scaleY(1f);

        // 记录前当前用户选中的选项卡
        mCurrentTab = mTabs[position];

        // 当前选中的选项卡高亮显示
        mCurrentTab.setSelected(true);                      // 高亮显示
        mCurrentTab.animate().scaleX(1.2f).scaleY(1.2f);    // 控件放大到1.2倍, 字体会变大
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Beacon_util.login();
        Net_data.instance();
    }
}
