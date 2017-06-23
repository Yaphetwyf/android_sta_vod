package com.example.administrator.android_sta_vod.ui.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afa.tourism.greendao.gen.DaoMaster;
import com.afa.tourism.greendao.gen.DaoSession;
import com.afa.tourism.greendao.gen.TermDao;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.adapter.Sta_terminal_Adapter;
import com.example.administrator.android_sta_vod.bean.Cap_start_ret;
import com.example.administrator.android_sta_vod.bean.Term;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.ui.activity.Add_terminal_activity;
import com.example.administrator.android_sta_vod.utils.XmlUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/2.
 */

public class Terminal_two_fragment extends BaseFragment {

    private TextView add_terminal;
    private LinearLayout ll_add_termianl;
    private ListView lv_sta_term;
    private ArrayList<Term> checked_terms;
    private Sta_terminal_Adapter terminal_adapter;
    private TermDao termDao;
    private Term terminal;
    private List<Term> terms;
    private List<String> idList;
    private String tag = "REAL_TIME_TWO_FRAGMENT";
    @Override
    public int get_layout_res() {
        return R.layout.fragment_terminal_two;
    }

    @Override
    public void init_view() {
        add_terminal = findView(R.id.tv_add_terminal);
        ll_add_termianl = findView(R.id.ll_add_terminal);
        lv_sta_term=findView(R.id.lv_sta_term);
    }

    @Override
    public void init_listener() {
        ll_add_termianl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), Add_terminal_activity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void init_data() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getActivity().getApplicationContext(), "term.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        termDao = daoSession.getTermDao();
        //拿到的终端列表数据
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            checked_terms=bundle.getParcelableArrayList("terms");
        }
        terms = termDao.loadAll();
      save_list_data();
      query_list_data();
    }
    private void query_list_data() {

        if(terms !=null){
            terms = termDao.loadAll();
            terminal_adapter=new Sta_terminal_Adapter(terms,mListener);
            lv_sta_term.setAdapter(terminal_adapter);
        }

    }

    private void save_list_data() {
        idList=new ArrayList<>();
         if(checked_terms!=null){
             for (int i = 0; i < checked_terms.size(); i++) {
                 List<Term> termList = termDao.loadAll();
                 for (Term term : termList) {
                     String id = term.getId();
                    idList.add(id);
                 }
                 if(idList.contains(checked_terms.get(i).getId())){

                 }else {
                     terminal = new Term(
                             checked_terms.get(i).getId(), checked_terms.get(i).getName(),checked_terms.get(i).getPid(),checked_terms.get(i).getStatus()
                     );
                     termDao.insert(terminal);
                 }
             }
             query_list_data();
}
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Avsz_info_event avsz_info_event)
    {
        String type = avsz_info_event.get_type();
        String key = avsz_info_event.get_key();
        String value = avsz_info_event.get_value();
        if ("real_cap_start_ret".equals(type) && value.contains("ok")) {

            Cap_start_ret cap_start_ret = XmlUtils.toBean(Cap_start_ret.class,value.getBytes());
            List<Term> speak_terms = cap_start_ret.getTerms().getTerms();
            for (Term term : terms) {
                for(Term term_speak : speak_terms){
                    if(term.getId().equals(term_speak.getId())){
                        term.setSpeaking(true);
                    }
                }
                terminal_adapter.notifyDataSetChanged();
            }

        }else if(type.equals("term_status")&&value.equals("1")){
            for (Term term : terms) {
                term.setSpeaking(false);
            }
            terminal_adapter.notifyDataSetChanged();
        }

    }
    @Override
    public void onClick(View v, int btnId) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }
    private Sta_terminal_Adapter.MyClickListener mListener = new Sta_terminal_Adapter.MyClickListener() {
        @Override
        public void myOnClick(int position, View v) {
            String id = terms.get(position).getId();
            QueryBuilder<Term> qb = termDao.queryBuilder();
            DeleteQuery<Term> bd = qb.where(TermDao.Properties.Id.eq(id)).buildDelete();
            bd.executeDeleteWithoutDetachingEntities();
            query_list_data();
            terminal_adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
