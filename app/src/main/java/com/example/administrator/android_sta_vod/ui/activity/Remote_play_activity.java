package com.example.administrator.android_sta_vod.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.adapter.MusicAdapter;
import com.example.administrator.android_sta_vod.app.Net_data;
import com.example.administrator.android_sta_vod.bean.FileBean;
import com.example.administrator.android_sta_vod.bean.Mp3;
import com.example.administrator.android_sta_vod.bean.MusicBean;
import com.example.administrator.android_sta_vod.bean.Progs;
import com.example.administrator.android_sta_vod.utils.Beacon_util;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2017/3/30 0030.
 */

public class Remote_play_activity extends AppCompatActivity implements View.OnClickListener {


    private static final String tag = "ADD_MUSIC_ACTIVITY";
    private RecyclerView mRv_addmusic;
    private ArrayList<FileBean> mData;
    private ArrayList<FileBean> mRoot;
    private MusicAdapter mMusicAdapter;
    private ArrayList<MusicBean> mSelect_data;
    private ImageView mIv_clear;
    private ImageView mIv_search;
    private EditText mEt_search;
    private ArrayList<FileBean> up_datas;
    private ImageView mIv_up;
    private ImageView mIv_down;
    private Stack<ArrayList<FileBean>> retreat_stack;
    private Stack<ArrayList<FileBean>> advance_stack;
    private TextView mTv_cancel;
    private TextView mTv_sel;
    private TextView mTv_confirm;
    private TextView mTv_all_select;

    @Override
    protected void onCreate(Bundle savedInstanceState)    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmusic);
        EventBus.getDefault().register(this);
     //   StatusBarCompat.compat(this, getResources().getColor(R.color.LimeGreen));
        init_data();
        init_view();
        init_event();
    }

    private void init_data()
    {
        mSelect_data = new ArrayList<>();
        Progs progs = Net_data.instance().get_progs();
        if (null != progs)
        {
            mData = Beacon_util.progs_to_file(progs);
            mRoot = Beacon_util.progs_to_file(progs);
        }
        advance_stack = new Stack<>();
        retreat_stack = new Stack<>();
    }

    private void init_view()
    {
        mRv_addmusic = (RecyclerView) findViewById(R.id.rv_addmusic);
        mIv_clear = (ImageView) findViewById(R.id.iv_clear);
        mIv_search = (ImageView) findViewById(R.id.iv_search);
        mEt_search = (EditText) findViewById(R.id.et_search);
        mIv_up = (ImageView) findViewById(R.id.iv_up);
        mIv_down = (ImageView) findViewById(R.id.iv_down);
        mTv_cancel = (TextView) findViewById(R.id.tv_cancel);
        mTv_sel = (TextView) findViewById(R.id.tv_sel);
        mTv_sel.setText(Ui_utils.get_string(R.string.selected) + "0" + Ui_utils.get_string(R.string.song));
        mTv_confirm = (TextView) findViewById(R.id.tv_confirm);
        mTv_all_select = (TextView) findViewById(R.id.tv_all_select);
        if (null == mData)
        {
            Log.d(tag, "mdata == null");
            mData = new ArrayList<>();
        }
        mMusicAdapter = new MusicAdapter(this, mData);
        mRv_addmusic.setAdapter(mMusicAdapter);
        mRv_addmusic.setLayoutManager(new LinearLayoutManager(this));
        mRv_addmusic.setItemAnimator(new DefaultItemAnimator());

    }

    private void init_event()
    {
        adapter_addlis();
        et_search_addlis();
        mIv_clear.setOnClickListener(this);
        mIv_search.setOnClickListener(this);
        mIv_up.setOnClickListener(this);
        mIv_down.setOnClickListener(this);
        mTv_cancel.setOnClickListener(this);
        mTv_confirm.setOnClickListener(this);
        mTv_all_select.setOnClickListener(this);
    }

    private void adapter_addlis()
    {
        mMusicAdapter.setOnMusicClickLitener(new MusicAdapter.OnMusicClickLitener() {
            @Override
            public void onMusicClick(View view, int position)
            {
                if (mData.get(position).isSelect())
                {
                    Log.d(tag, "isselect");
                    mData.get(position).setSelect(false);
                    mSelect_data.remove(mData.get(position));
                } else
                {
                    Log.d(tag, "!isselect");
                    mData.get(position).setSelect(true);
                    mSelect_data.add((MusicBean) mData.get(position));
                }
                mTv_sel.setText(Ui_utils.get_string(R.string.selected) + mSelect_data.size() + Ui_utils.get_string(R
                        .string.song));
            }
        });

        mMusicAdapter.setOnFolderClickLitener(new MusicAdapter.OnFolderClickLitener() {
            @Override
            public void onFolderClick(View view, int position)
            {
//                Toast.makeText(Add_music_activity.this, "onMusicClick" + mData.get(position).getName(), Toast
//                        .LENGTH_SHORT)
//                        .show();
                if (mData.get(position).getChildren() != null)
                {
                    retreat_stack.push(mData);

                    //空目录
                    if (mData.get(position).getChildren().size() == 0)
                    {
                        up_datas = mData;
                        mData.clear();
                        mMusicAdapter.notifyDataSetChanged();
                        Log.d(tag, "空目录");
                    } else
                    {
                        Log.d(tag, "mData.hashcode" + mData.hashCode());
                        mData = mData.get(position).getChildren();
                        Log.d(tag, "mData.hashcode" + mData.hashCode());
                        Log.d(tag, "mData.size == " + mData.size());
                        mMusicAdapter.set_data(mData);
//                        mMusicAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onFolderLongClick(View view, int position)
            {
                if (!mData.get(position).isSelect())
                {
                    mData.get(position).setSelect(true);
                } else
                {
                    mData.get(position).setSelect(false);
                }

                ArrayList<FileBean> children = mData.get(position).getChildren();
                for (int i = 0; i < children.size(); i++)
                {
                    if (children.get(i) instanceof MusicBean)
                    {
                        if (!children.get(i).isSelect())
                        {
                            ((MusicBean) children.get(i)).set_select(true);
                            mSelect_data.add((MusicBean) children.get(i));
                        } else
                        {
                            ((MusicBean) children.get(i)).set_select(false);
                            mSelect_data.remove((MusicBean) children.get(i));
                        }

                    }
                }
                mTv_sel.setText(Ui_utils.get_string(R.string.selected) + mSelect_data.size() + Ui_utils.get_string(R
                        .string.song));
            }
        });
    }

    private void et_search_addlis()
    {
        mEt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                mIv_clear.setVisibility(View.VISIBLE);
                mIv_search.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String msg = mEt_search.getText().toString();
                if ("".equals(msg))
                {
                    mMusicAdapter.set_data(mData);
                } else
                {
                    mMusicAdapter.set_data((ArrayList<FileBean>) filter(mData, msg));
                    mData = (ArrayList<FileBean>) filter(mData,msg);
                }
                mIv_clear.setVisibility(View.GONE);
                mIv_search.setVisibility(View.VISIBLE);
            }
        });
    }

    private static List<FileBean> filter(List<FileBean> models, String query)
    {
        final String lowerCaseQuery = query.toLowerCase();
        Log.d("AddMusicDemo", "lowerCaseQuery" + lowerCaseQuery);
        final List<FileBean> filteredModelList = new ArrayList<>();
        for (FileBean model : models)
        {
            final String text = model.getName().toLowerCase();

//            final String rank = String.valueOf(model.getRank());
            if (model instanceof MusicBean)
            {
                if (text.contains(lowerCaseQuery))
                {
                    Log.d("AddMusicDemo", "text" + text);
                    filteredModelList.add(model);
                }
            }
        }
        Log.d("AddMusicDemo", "filteredModelList" + filteredModelList.size());
        return filteredModelList;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusOnMainThread(Progs progs)
    {

        mData = Beacon_util.progs_to_file(progs);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_all_select:

                for (int i = 0; i < mData.size(); i++)
                {
                    if (mData.get(i) instanceof MusicBean)
                    {
                        if (!mData.get(i).isSelect())
                        {
                            ((MusicBean) mData.get(i)).set_select(true);
                            mSelect_data.add((MusicBean) mData.get(i));
                        }
                    }
                }
                mTv_sel.setText(Ui_utils.get_string(R.string.selected) + mSelect_data.size() + Ui_utils.get_string(R
                        .string.song));
                mMusicAdapter.notifyDataSetChanged();
                mTv_all_select.setClickable(false);
                break;
            case R.id.tv_cancel:
               /* for (int i = 0; i < mData.size(); i++)
                {
                    if (mData.get(i) instanceof MusicBean)
                    {
                        if (mData.get(i).isSelect())
                        {
                            ((MusicBean) mData.get(i)).set_select(false);
                            mSelect_data.remove(mData.get(i));
                        }
                    }
                }
                mTv_sel.setText(Ui_utils.get_string(R.string.selected) + mSelect_data.size() + Ui_utils.get_string(R
                        .string.song));
                mMusicAdapter.notifyDataSetChanged();*/
                finish();
                break;
            case R.id.tv_confirm:
                ArrayList<Mp3> mp3s = new ArrayList<>();
                for (int i = 0; i < mSelect_data.size(); i++)
                {
                    mp3s.add(new Mp3(mSelect_data.get(i).getId(), mSelect_data.get(i).getName(), mSelect_data.get(i)
                            .getLength() + ""));
                }
                Log.d(tag, "mp3s.size == " + mp3s.size());
                Intent confirm_intent = new Intent();
                confirm_intent.setClass(Remote_play_activity.this,Remote_paly_list_activity.class);
                Bundle confirm_bundle = new Bundle();
                confirm_bundle.putParcelableArrayList("mp3s", mp3s);
                confirm_intent.putExtras(confirm_bundle);
                startActivity(confirm_intent);
                break;
            case R.id.iv_clear:
                mEt_search.setText("");
                break;
            case R.id.iv_search:
                String msg = mEt_search.getText().toString();
                if ("".equals(msg))
                {
                    mMusicAdapter.set_data(mData);
                } else
                {
                    mMusicAdapter.set_data((ArrayList<FileBean>) filter(mData, msg));
                }
                break;
            case R.id.iv_up:
                //空文件夹

                if (!retreat_stack.empty())
                {
                    advance_stack.push(mData);
                    mData = retreat_stack.pop();
                    mMusicAdapter.set_data(mData);
                    Log.d(tag, "!retreat_stack.empty()");
                }
                Log.d(tag, "click_up");
                break;
            case R.id.iv_down:
                if (!advance_stack.empty())
                {
                    retreat_stack.push(mData);
                    mData = advance_stack.pop();
                    mMusicAdapter.set_data(mData);
                    Log.d(tag, "!advance_stack.empty()");

                }
                Log.d(tag, "click_down");
//                mMusicAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
