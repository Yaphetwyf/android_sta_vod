package com.example.administrator.android_sta_vod.ui.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.broadcast.android.android_sta_jni.ndk_wrapper;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.adapter.Remote_play_list_adapter;
import com.example.administrator.android_sta_vod.bean.Mp3;
import com.example.administrator.android_sta_vod.utils.File_utils;
import com.example.administrator.android_sta_vod.utils.SPUtils;
import com.example.administrator.android_sta_vod.utils.T;
import com.example.administrator.android_sta_vod.utils.Ui_utils;
import com.example.administrator.android_sta_vod.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.administrator.android_sta_vod.base.Const.download;

/**
 * Created by Administrator on 2017/4/12 0012.
 */

public class Remote_paly_list_activity extends Base_activity {

    // 更新当前播放进度
    private static final int TYPE_UPDATE_CURRENT_POSITION = 0;
    private ListView play_list;
    private Remote_play_list_adapter play_list_adapter;
    private MediaPlayer mediaPlayer;
    private ArrayList<String> songArrayList; //播放声音列表
    private String music_load;
    private int songIndex = 0;
    private File_utils file_utils;
    private String all_music_load;
    private Button remote_prev;
    private Button remote_next;
    private Button remote_toggle_play;
    private Button remote_switch_play_mode;
    private String SD_PATH;
    /**
     * 打开是否是同一首音频
     * true： 不会从头开始播放
     */
    private boolean openThemSameAudio = false;
    private SeekBar sb_remote;
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    private int duration;
    private TextView remote_current_position;

    @Override
    public int get_layout_res() {
        return R.layout.activity_remote_play_list;
    }

    @Override
    public void init_view() {
        play_list = findView(R.id.lv_remote_play_list);
        remote_prev = findView(R.id.btn_remote_prev);
        remote_next= findView(R.id.btn_remote_next);
        remote_toggle_play=findView(R.id.btn_remote_toggle_play);
        remote_switch_play_mode= findView(R.id.btn_remote_switch_play_mode);
        sb_remote = findView(R.id.sb_remote_current_position);
        remote_current_position = findView(R.id.tv_remote_current_position);
    }
    @Override
    public void init_listener() {
        sb_remote.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        remote_next.setOnClickListener(this);
        remote_prev.setOnClickListener(this);
        remote_toggle_play.setOnClickListener(this);
        remote_switch_play_mode.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new CompletionListener());
        SD_PATH  = Environment.getExternalStorageDirectory() + "/";
        music_load= Environment.getExternalStorageDirectory() + "/"+"sdMusic"+"/";
        songArrayList = new ArrayList<String>();
        file_utils = new File_utils();
        ndk_wrapper.instance().set_cb_file_listener(new ndk_wrapper.Cb_file_listener() {
            @Override
            public void cb_file_listener(int id, int file_size, String full_path, String key, byte[] buf) {
                Log.d("download","id"+id+"file_size"+file_size+"full_path"+full_path+"key"+key+"buf"+buf.length);
                file_utils.write2SDFromInput("sdMusic", full_path.substring(full_path.lastIndexOf("/")),buf);
                if(key.equals("file_recv_end")){
                   all_music_load = music_load+full_path.substring(full_path.lastIndexOf("/"));
                   songArrayList.add(all_music_load);
             }
            }
        });
        play_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                songIndex=position;
                remote_toggle_play.setBackgroundResource(R.drawable.btn_audio_pause);
                 songplay();
                updateCurrentPosition();
            }
        });
        // 用户滑动seekbar, 则实现快进快退功能
        mOnSeekBarChangeListener =
                new SeekBar.OnSeekBarChangeListener() {

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) { // 用户滑动seekbar, 则实现快进快退功能
            seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
};
    }

    private final class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next_song();

        }
    }
    private void updtaeUI(){

    }
    private Random mRandom = new Random();
    private void next_song() {
        switch (mCurrentPlayMode) {
            case PLAY_MODE_ORDER:       // 顺序循环播放
                // 如果当前正在播放最后一首，当用户点击播放下一首时，播放第一首
                if (songIndex == songArrayList.size() - 1) {
                    songIndex = 0;
                } else {
                    songIndex++;
                }
                break;
            case PLAY_MODE_RANDOM:     // 随机播放
                songIndex = mRandom.nextInt(songArrayList.size());
                break;
        }
      songplay();
        updateCurrentPosition();
    }
    private void prev_song() {
        switch (mCurrentPlayMode) {
            case PLAY_MODE_ORDER:       // 顺序循环播放
                // 如果当前正在播放第一首，当用户点击播放上一首时，播放最后一首
                if (songIndex == 0) {
                    songIndex = songArrayList.size() - 1;
                } else {
                    songIndex--;
                }
                break;
            case PLAY_MODE_RANDOM:     // 随机播放
                songIndex = mRandom.nextInt(songArrayList.size());
                break;
        }
        songplay();
        updateCurrentPosition();
    }
    private void download(String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ndk_wrapper.instance().avsz_file_transfer(path);
            }
        }).start();
    }

    @Override
    public void init_data() {
        String load = SPUtils.getString(Ui_utils.get_context(), download);
        createSDDir("sdMusic");
        Log.d("load",load);
        String path3="";
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ArrayList<Mp3> mp3s = bundle.getParcelableArrayList("mp3s");
        List<String> music_file = get_music_file();

        for(int i=0;i<mp3s.size();i++){
            path3=load+mp3s.get(i).getPath();

            String path1 = mp3s.get(i).getName();
         if(music_file.contains(path1)){
             songArrayList.add(music_load+path1);
         }else {
             download(path3);
         }
        }
        play_list_adapter = new Remote_play_list_adapter(mp3s);
        play_list.setAdapter(play_list_adapter);
    }
    //获取文件名称
    public List<String> get_music_file(){
        // 音乐列表
        List<String> picList = new ArrayList<String>();
        File mfile = new File(music_load);



        File[] files = mfile.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
                picList.add(file.getPath().substring(file.getPath().lastIndexOf("/")+1));
        }
        // 返回得到的音乐列表
        return picList;
    }
    public File createSDDir(String dirName){
        File dir = new File(SD_PATH + dirName);
        dir.mkdir();
        return dir;
    }
    @Override
    public void onClick(View v, int btnId) {
        switch (btnId){
            case R.id.btn_remote_toggle_play:
                togglePlay();
                break;
            case R.id.btn_remote_prev:
                prev_song();
                break;
            case R.id.btn_remote_next:
                next_song();
                break;
            case R.id.btn_remote_switch_play_mode:
                switch_play_mode();
                break;
        }
    }
    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }
    private void togglePlay() {
        if (isPlaying()) { // 播放 -> 暂停
            pause();
            remote_toggle_play.setBackgroundResource(R.drawable.btn_audio_play);
        } else {                    // 暂停 -> 播放
            start();
            remote_toggle_play.setBackgroundResource(R.drawable.btn_audio_pause);
        }
    }
    public void openAudio() {
        String path = songArrayList.get(songIndex);

        release();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path); // 要播放音频的路径
            mediaPlayer.prepareAsync();    // 异步缓冲
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * 更新播放进度
     */
    private void updateCurrentPosition() {
        int currentPosition = getCurrentPosition();
        String text = Utils.formatDuration(currentPosition) + "/"
                + Utils.formatDuration(duration);
        remote_current_position.setText(text);        // 显示播放进度与总时长
        sb_remote.setProgress(currentPosition);

        mHandler.sendEmptyMessageDelayed(TYPE_UPDATE_CURRENT_POSITION, 60);
    }
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TYPE_UPDATE_CURRENT_POSITION:      // 更新播放进度
                    updateCurrentPosition();
                    break;
            }
        }
    };
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    private void songplay() {
        try {
            mediaPlayer.reset();
            String load = songArrayList.get(songIndex);
            mediaPlayer.setDataSource(load);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            T.show_short(Ui_utils.get_string(R.string.error));
        }
    }
    private void release() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();

        }
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }
    /** 顺序播放 */
    public static final int PLAY_MODE_ORDER = 1;

    /** 随机播放 */
    public static final int PLAY_MODE_RANDOM = 3;

    /** 当前播放模式 */
    private int mCurrentPlayMode = PLAY_MODE_ORDER;
    /**
     *  切换播放模式
     */
    private void switch_play_mode() {
        int playMode = switchPlayMode();
        update_switch_mode_BtnBG(playMode);
    }

    private int switchPlayMode() {
        switch (mCurrentPlayMode) {
            case PLAY_MODE_ORDER:       // 如果当前为顺序播放，则切换为随机播放
                mCurrentPlayMode = PLAY_MODE_RANDOM;
                break;
            case PLAY_MODE_RANDOM:      // 如果当前为随机播放，则切换为顺序
                mCurrentPlayMode = PLAY_MODE_ORDER;
                break;
        }
        // 用SharedPreference保存当前播放模式
        Utils.saveAudioPlayMode(this, mCurrentPlayMode);

        // 返回切换后的播放模式
        return mCurrentPlayMode;
    }

    /**
     * 根据当前播放模式，显示不同的按钮背景
     * @param playMode
     */
    private void update_switch_mode_BtnBG(int playMode) {
        int btnResBG = R.drawable.loop;
        switch (playMode) {
            case PLAY_MODE_ORDER:       // 顺序播放
                btnResBG = R.drawable.btn_playmode_allrepeat;
                break;

            case PLAY_MODE_RANDOM:
                btnResBG = R.drawable.btn_playmode_random;
                break;
            default:
                throw new IllegalStateException("不能识别的模式");
        }
        remote_switch_play_mode.setBackgroundResource(btnResBG);
    }
}
