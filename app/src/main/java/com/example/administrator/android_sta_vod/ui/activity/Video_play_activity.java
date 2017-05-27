package com.example.administrator.android_sta_vod.ui.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.adapter.Terminal_user_adapter;
import com.example.administrator.android_sta_vod.base.Const;
import com.example.administrator.android_sta_vod.bean.Term;
import com.example.administrator.android_sta_vod.bean.Terms;
import com.example.administrator.android_sta_vod.event.Avsz_info_event;
import com.example.administrator.android_sta_vod.ui.activity.dialogs.Dialog_term_answer;
import com.example.administrator.android_sta_vod.utils.Beacon_util;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/4/17 0017.
 */
public class Video_play_activity extends Base_activity{

    private final static int requestCode = 105;

    private GridView vedio_gridview;
    private List<Term> termList;
    private Terminal_user_adapter terminal_user_adapter;
    public static final String ON_LINE="1";//在线
    private Dialog_term_answer dialog_term_answer;
    private MediaPlayer player;
    private AssetManager assetManager;
    @Override
    public int get_layout_res() {
        return R.layout.activity_vedio_play;
    }

    @Override
    public void init_view() {
        vedio_gridview = findView(R.id.video_gridview);
    }

    @Override
    public void init_listener() {

        vedio_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String  term_name=  termList.get(position).getName();
                String  term_state= termList.get(position).getStatus();
                String term_id = termList.get(position).getId();
                terminal_user_adapter.notifyDataSetChanged();
                if(ON_LINE.equals(term_state)||"2".equals(term_state)){
                    Intent intent = new Intent(Ui_utils.get_context(), Talk_back_activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Const.term_id, term_id);
                    bundle.putString(Const.term_name, term_name);
                    bundle.putString(Const.term_state, term_state);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, requestCode);
                }
            }
        });
    }

    @Override
    public void init_data() {

    }

    @Override
    public void onClick(View v, int btnId) {

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Avsz_info_event info) {
        String type = info.get_type();
        String key = info.get_key();
        String value = info.get_value();
        Log.d("-----Avsz_info_event", "type:"+type+"key:"+key+"value:"+value);
        if("term_call_req".equals(type)){
            if (player != null && !player.isPlaying()) {
                try {
                    player.start();
                    Log.d("media_player", "media_player success");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("media_player", e.getMessage());
                }
            }
            show_ansdialog(type, key, value);
        }
        if("term_call_req_cancel".equals(type)){
            if (null != player && player.isPlaying()) {
                player.pause();
                player.seekTo(0);
            }
            dialog_term_answer.dismiss();
            dialog_term_answer=null;
        }
        if ("term_status".equals(info.get_type()))
        {
           for(int i=0;i<termList.size();i++){
               if(termList.get(i).getId().equals(info.get_key())){
                   termList.get(i).setStatus(info.get_value());
                   Log.d("term_state",info.get_value()+"");
                   terminal_user_adapter.notifyDataSetChanged();
               }
           }
        }
        if ("tcp".equals(type))
        {

            if ("close".equals(key) || "timeout".equals(key) || "finished".equals(key))
            {
                for(int i=0;i<termList.size();i++){
                   termList.get(i).setStatus("0");
                    terminal_user_adapter.notifyDataSetChanged();
                }
            }
        }
    }
    private void show_ansdialog(String type, String key, String value) {
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
                startActivityForResult(intent, requestCode);
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

    //接收终端信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Terms terms)
    {
        termList = terms.getTerms();
        terminal_user_adapter = new Terminal_user_adapter(termList);
        vedio_gridview.setAdapter(terminal_user_adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
        Beacon_util.login();
    }
    @Override
    public void onStart() {
        super.onStart();
        //准备播放音乐

        if (null == player) {
            player = new MediaPlayer();
            assetManager = this.getAssets();
            try {
                AssetFileDescriptor fileDescriptor = assetManager.openFd("call.mp3");
                player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                player.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dialog_term_answer!=null){
            dialog_term_answer.dismiss();
            dialog_term_answer=null;
        }
    }
}
