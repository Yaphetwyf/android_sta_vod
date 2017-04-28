package com.example.administrator.android_sta_vod.ui.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.adapter.Terminal_expandlist_adapter;
import com.example.administrator.android_sta_vod.app.Net_data;
import com.example.administrator.android_sta_vod.bean.Area;
import com.example.administrator.android_sta_vod.bean.Areas;
import com.example.administrator.android_sta_vod.bean.Term;
import com.example.administrator.android_sta_vod.bean.Terms;
import com.example.administrator.android_sta_vod.utils.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/26.
 */

public class Terminal_fragment extends BaseFragment{
    private String tag = "REAL_TIME_FRAGMENT";
    private ExpandableListView elv_terminal_list;
    private Terminal_expandlist_adapter expandlist_adapter;
    private Areas areas;
    private Terms terms;
    private ArrayList<Term> term_list;
    private ArrayList<ArrayList<Term>> terms_list;
    private Button btn_sure_term;

    @Override
    public int get_layout_res() {
        return R.layout.fragment_terminal;
    }

    @Override
    public void init_view() {
        elv_terminal_list = findView(R.id.elv_terminal_list);
        btn_sure_term = findView(R.id.btn_sure_term);
    }

    @Override
    public void init_listener() {
        btn_sure_term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Term> terms = expandlist_adapter.get_terms();

            }
        });
    }

    @Override
    public void init_data() {
        areas = Net_data.instance().get_areas();
        terms = Net_data.instance().get_terms();

        if (null != areas.getAreas())
        {
            init_goal_data();
            expandlist_adapter = new Terminal_expandlist_adapter((ArrayList<Area>) areas.getAreas(), terms_list);
        }
        elv_terminal_list.setAdapter(expandlist_adapter);

    }

    @Override
    public void onClick(View v, int btnId) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Areas areas)
    {
        this.areas = areas;
        init_goal_data();
        expandlist_adapter.set_data((ArrayList<Area>) areas.getAreas(), terms_list);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Terms terms)
    {
        this.terms = terms;
        init_goal_data();
        expandlist_adapter.set_data((ArrayList<Area>) areas.getAreas(), terms_list);
    }
    //初始化2级ExpandableListView数据
    private void init_goal_data() {
        terms_list = new ArrayList<>();
        if (null == areas.getAreas())
        {
            areas.setAreas(new ArrayList<>());
        }
        for (Area area : areas.getAreas())
        {
            L.d(area.toString());
            term_list = new ArrayList<>();
            if (null != terms)
            {
                if (null == terms.getTerms())
                {
                    terms.setTerms(new ArrayList<>());
                }
                for (Term term : terms.getTerms())
                {
                    if (term.getPid().equals(area.getId()))
                    {
                        term_list.add(term);
                    }
                }
            }
            terms_list.add(term_list);
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
