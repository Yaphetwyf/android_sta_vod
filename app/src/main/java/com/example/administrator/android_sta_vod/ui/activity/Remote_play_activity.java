package com.example.administrator.android_sta_vod.ui.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.broadcast.android.android_sta_jni_official.ndk_wrapper;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.adapter.MusicAdapter;
import com.example.administrator.android_sta_vod.app.Net_data;
import com.example.administrator.android_sta_vod.base.Const;
import com.example.administrator.android_sta_vod.bean.FileBean;
import com.example.administrator.android_sta_vod.bean.Mp3;
import com.example.administrator.android_sta_vod.bean.MusicBean;
import com.example.administrator.android_sta_vod.bean.Progs;
import com.example.administrator.android_sta_vod.bean.Users;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_term_answer;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_text_answer;
import com.example.administrator.android_sta_vod.utils.Beacon_util;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
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

    private ArrayList<FileBean> up_datas;
    private ImageButton mIv_up;
    private ImageView mIv_down;
    private Stack<ArrayList<FileBean>> retreat_stack;
    private Stack<ArrayList<FileBean>> advance_stack;
    private TextView mTv_cancel;
    private TextView mTv_sel;
    private TextView mTv_confirm;
    public  String delete_music_load;
    private Dialog_text_answer dialog_text_answer;
    private Dialog_term_answer dialog_term_answer;
    private MediaPlayer player;
    private AssetManager assetManager;
    private Users users;
    private static final int request_code = 106;
    @Override
    protected void onCreate(Bundle savedInstanceState)    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmusic);
        EventBus.getDefault().register(this);
     //   StatusBarCompat.compat(this, getResources().getColor(R.color.LimeGreen));
        init_data();
        init_view();
        init_event();
        delete_music_load= Environment.getExternalStorageDirectory() +"/sdMusic/";
        deleteDir();

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
        mIv_up = (ImageButton) findViewById(R.id.iv_up);
        mTv_cancel = (TextView) findViewById(R.id.tv_cancel);
        mTv_sel = (TextView) findViewById(R.id.tv_sel);
        mTv_sel.setText(Ui_utils.get_string(R.string.selected) + "0" + Ui_utils.get_string(R.string.song));
        mTv_confirm = (TextView) findViewById(R.id.tv_confirm);

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
      //  et_search_addlis();
        mIv_up.setOnClickListener(this);
        mTv_cancel.setOnClickListener(this);
        mTv_confirm.setOnClickListener(this);

    }
    public  void deleteDir() {
        File dir = new File(delete_music_load);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
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
    //接收回调消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Avsz_info_event info) {
        String type = info.get_type();
        String key = info.get_key();
        String value = info.get_value();
        KeyguardManager km =
                (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        boolean showingLocked = km.inKeyguardRestrictedInputMode();
        if ("usr_call_req".equals(type)) {

            if(showingLocked){
                Intent intent=new Intent(this,Answer_user_activity.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }else {
                if(null == dialog_text_answer){
                    show_ansdialog(type, key, value);
                    if (null != player) {
                        if (!player.isPlaying()) {
                            player.start();
                        }
                    }
                }
            }
        }
        if ("usr_call_req_cancel".equals(type)) {
            if (null != dialog_text_answer) {
                dialog_text_answer.dismiss();
                dialog_text_answer = null;
            }
            if (null != player) {
                if (player.isPlaying()) {
                    player.pause();
                    player.seekTo(0);
                }
            }
        }

        if (null != users) {
            if ("usr_offline".equals(type)) {
                for (int i = 0; i < users.getUsers().size(); i++) {

                    if (key.equals(users.getUsers().get(i).getName())) {
                        users.getUsers().get(i).setStatus("0");
                        Log.d(tag, "usr_online" + users.getUsers().get(i).getName());

                    }
                }
            }
            if ("usr_online".equals(type)) {
                for (int i = 0; i < users.getUsers().size(); i++) {
                    if (key.equals(users.getUsers().get(i).getName())) {
                        users.getUsers().get(i).setStatus("1");

                    }
                }
            }
        }
        if("term_call_req".equals(type)){
            if(showingLocked){
                Intent intent=new Intent(this,Answer_term_activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("term_id", key);
                bundle.putString("term_name", value);
                intent.putExtras(bundle);
                startActivity(intent);
            }else {
                if (null != player) {
                    if (!player.isPlaying()) {
                        player.start();
                    }
                }
                show_trem_ansdialog(type, key, value);
            }
        }
        if("term_call_req_cancel".equals(type)){
            if (null != player) {
                if (player.isPlaying()) {
                    player.pause();
                    player.seekTo(0);
                }
            }
            if(dialog_term_answer!=null){
                dialog_term_answer.dismiss();
                dialog_term_answer=null;
            }

        }
    }

    private void show_trem_ansdialog(String type, String key, String value) {
        dialog_term_answer = new Dialog_term_answer(this);
        dialog_term_answer.set_on_answer_listener(new Dialog_term_answer.On_answer_listener() {
            @Override
            public void answer_listener() {
                if (null != player) {
                    if (player.isPlaying()) {
                        player.pause();
                        player.seekTo(0);
                    }
                }
                Intent intent = new Intent(Ui_utils.get_context(), Talk_back_activity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Const.term_id, key);
                bundle.putString(Const.term_name, value);
                bundle.putString(Const.term_state,Ui_utils.get_string(R.string.calling));
                intent.putExtras(bundle);
                startActivityForResult(intent, request_code);
                dialog_term_answer.dismiss();
                dialog_term_answer=null;
            }
        });
        dialog_term_answer.set_on_refuse_listener(new Dialog_term_answer.On_refuse_listener() {
            @Override
            public void refuse_listener() {
                if (null != player) {
                    if (player.isPlaying()) {
                        player.pause();
                        player.seekTo(0);
                    }
                }
                dialog_term_answer.dismiss();
                dialog_term_answer=null;
            }
        });
        dialog_term_answer.show();
        dialog_term_answer.set_user(value);
    }

    //显示回答的Dialog
    private void show_ansdialog(String type, String key, String value) {
        dialog_text_answer = new Dialog_text_answer(this);
        dialog_text_answer.set_on_refuse_listener(new Dialog_text_answer.On_refuse_listener() {
            @Override
            public void refuse_listener() {
                ndk_wrapper.instance().avsz_usr_call_req_ret(key, 0);
                if (null != player) {
                    if (player.isPlaying()) {
                        player.pause();
                        player.seekTo(0);
                    }
                }
                dialog_text_answer.dismiss();
                dialog_text_answer=null;
            }
        });
        dialog_text_answer.set_on_answer_listener(new Dialog_text_answer.On_answer_listener() {
            @Override
            public void answer_listener() {
                ndk_wrapper.instance().avsz_usr_call_req_ret(key, 2);
                if (null != player) {
                    if (player.isPlaying()) {
                        player.pause();
                        player.seekTo(0);
                    }
                }
                Intent intent = new Intent(getApplicationContext(), User_talk_activity.class);
                intent.putExtra("user_name", key);
                startActivity(intent);
                dialog_text_answer.dismiss();
                dialog_text_answer=null;
            }
        });
        dialog_text_answer.set_on_transfer_listener(new Dialog_text_answer.On_transfer_listener() {
            @Override
            public void transfer_listener() {
                ndk_wrapper.instance().avsz_usr_call_req_ret(key, 0);
                if (null != player) {
                    if (player.isPlaying()) {
                        player.pause();
                        player.seekTo(0);
                    }
                }
                dialog_text_answer.dismiss();
                dialog_text_answer=null;
            }
        });
        dialog_text_answer.show();
        dialog_text_answer.set_user(key);

    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_cancel:

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

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        create_player();
    }
    private void create_player() {
        //准备播放音乐
        player = new MediaPlayer();
        assetManager =this.getAssets();
        try {
            AssetFileDescriptor fileDescriptor = assetManager.openFd("call.mp3");
            player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            player.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (null != player && player.isPlaying()) {
            player.stop();
        }
        player.release();
        player = null;
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
