package com.example.administrator.android_sta_vod.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.broadcast.android.android_sta_jni_official.ndk_wrapper;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.bean.Mp3;
import com.example.administrator.android_sta_vod.service.Audioplay_service;
import com.example.administrator.android_sta_vod.utils.File_utils;
import com.example.administrator.android_sta_vod.utils.SPUtils;
import com.example.administrator.android_sta_vod.utils.T;
import com.example.administrator.android_sta_vod.utils.Ui_utils;
import com.example.administrator.android_sta_vod.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
    private Remote_play_list_adapter1 play_list_adapter;
    private MediaPlayer player;
    private ArrayList<String> songArrayList; //播放声音列表
    private String music_load;
    public static String delete_music_load;
    private int songIndex = 0;
    private File_utils file_utils;
    private String all_music_load;
    private Button remote_prev;
    private Button remote_next;
    private Button remote_toggle_play;
    private Button remote_switch_play_mode;
    private String SD_PATH;
    private int MAX_SIZE;
    private int progress;
    private SeekBar sb_remote;
    private int duration;
    private TextView remote_current_position;
    private int all_size;
    private ArrayList<Mp3> mp3s;
    private Audioplay_service mService;
    private String tag = "RECORD_ACTIVITY";
    private int music_index=0;
    private  ArrayList<Integer> mlist_id;
    private int data_length;
    private Mp3 mp3;

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
        player = new MediaPlayer();
        player.setOnCompletionListener(new CompletionListener());
        SD_PATH  = Environment.getExternalStorageDirectory() + "/";
        music_load= Environment.getExternalStorageDirectory() + "/"+"sdMusic"+"/";
        delete_music_load=Environment.getExternalStorageDirectory() +"/sdMusic/";
        songArrayList = new ArrayList<String>();
        file_utils = new File_utils();
        ndk_wrapper.instance().set_cb_file_listener(new ndk_wrapper.Cb_file_listener() {
            @Override
            public void cb_file_listener(int id, long file_size, String full_path, String key, byte[] buf) {
                Log.d("download","id:"+id+"file_size:"+file_size+"full_path:"+full_path+"key:"+key+"buf:"+buf.length);
                if(key.equals("file_recv_data")){
                    file_utils.write2SDFromInput("sdMusic", full_path.substring(full_path.lastIndexOf("/")),buf);
                    progress+=buf.length;
                   MAX_SIZE= (int) file_size;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(music_index>mp3s.size()-1){
                            return;
                        }else {

                            mp3s.get(music_index).setProgress((int) (progress*100.0/MAX_SIZE));
                            try {
                                Thread.sleep(5);
                                play_list_adapter.notifyDataSetChanged();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
                }
                if(key.equals("file_recv_start")){
                  /*  mp3 = new Mp3();*/
                    progress=0;
                    mlist_id.add(id);
                    all_music_load = music_load+full_path.substring(full_path.lastIndexOf("/"));
                //    updateGallery(all_music_load);

                }
                if(key.equals("file_recv_end")){
                 mp3s.get(music_index).setDownload(true);

                    music_index++;
                    downLoad_music();

                }
            }
        });

        play_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(songArrayList.size()-1<position){
                    T.show_short(Ui_utils.get_string(R.string.music_loading));
                    return;
                }
                songIndex=position;
                play_list_adapter.setSelectedPosition(songIndex);
                play_list_adapter.notifyDataSetInvalidated();
                remote_toggle_play.setBackgroundResource(R.drawable.btn_audio_pause);
                 songplay();
                 updateUI();

            }
        });

    }
    SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener
            = new SeekBar.OnSeekBarChangeListener() {

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
    private final class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next_song();
            updateUI();
        }
    }
    private void updateGallery(String filename)//filename是我们的文件全名，包括后缀哦
    {
        MediaScannerConnection.scanFile(this,
                new String[] { filename }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                        sendBroadcast(new  Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    }
                });
    }
    HashMap<Integer,Mp3> music_down=new HashMap<>();
    @Override
    public void init_data() {
        Intent intent1 = new Intent(this, Audioplay_service.class);
        bindService(intent1, mServiceConnection, BIND_AUTO_CREATE);

        mlist_id=new ArrayList<>();
        createSDDir("sdMusic");

        String path3="";
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mp3s = bundle.getParcelableArrayList("mp3s");
        List<String> music_file = get_music_file();

        for(int i = 0; i< mp3s.size(); i++){
           // path3=load+ mp3s.get(i).getPath();
            String path1 = mp3s.get(i).getName();
            if(music_file.contains(path1)){
                songArrayList.add(music_load+path1);
            }else {
                songArrayList.add(music_load+path1);
              // download(path3);
            }

        }
        downLoad_music();
        play_list_adapter = new Remote_play_list_adapter1();
        play_list.setAdapter(play_list_adapter);

    }

    private void downLoad_music() {
        String load = SPUtils.getString(Ui_utils.get_context(), download);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(music_index>mp3s.size()-1){
                    return;
                }else {
                    String path=mp3s.get(music_index).getPath();
                    String path2=load+path;
                    Log.d("music_path",path2);
                    ndk_wrapper.instance().avsz_file_transfer(path2);
                }

            }
        }).start();
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Audioplay_service.MyBinder binder = (Audioplay_service.MyBinder) service;
            mService = binder.mService;
            if(mService!=null){
                if(mService.isPlaying()){
                    mService.pause();
                }
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private void updateUI(){
        duration = player.getDuration();      // 获取音频播放的总时长
        sb_remote.setMax(duration);
        updateCurrentPosition();
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
        play_list_adapter.setSelectedPosition(songIndex);
        play_list_adapter.notifyDataSetInvalidated();
        songplay();
        updateUI();
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
        play_list_adapter.setSelectedPosition(songIndex);
        play_list_adapter.notifyDataSetInvalidated();
        songplay();
        updateUI();
    }
    private void download(String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ndk_wrapper.instance().avsz_file_transfer(path);
            }
        }).start();
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
        if (player != null) {
            player.seekTo(position);
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
            player = new MediaPlayer();
            player.setDataSource(path); // 要播放音频的路径
            player.prepareAsync();    // 异步缓冲
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
        if (player != null) {
            return player.getCurrentPosition();
        }
        return 0;
    }

    private void songplay() {
        try {
            player.reset();
            String load = songArrayList.get(songIndex);
            player.setDataSource(load);
            player.prepare();

            player.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            T.show_short(Ui_utils.get_string(R.string.error));
            player.stop();
        }
    }
    private void release() {
        if (player != null) {
            player.reset();
            player.release();
            player = null;
        }
    }


    public void start() {
        if (player != null) {
            player.start();
        }
    }
    public void pause() {
        if (player != null) {
            player.pause();

        }
    }

    public boolean isPlaying() {
        if (player != null) {
            return player.isPlaying();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解绑服务
        deleteDir();
        unbindService(mServiceConnection);
        songArrayList.clear();
        release();
    }
    public static void deleteDir() {
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

    class Remote_play_list_adapter1 extends BaseAdapter {

        private int selectedPosition = -1;// 选中的位置
        private LinearLayout ll_select_music;
        @Override
        public int getCount() {
            if(play_list!=null){
                return mp3s.size();
            }
            return 0;
        }
        @Override
        public Object getItem(int position) {
            return mp3s.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
          ViewHolder viewHolder;
            if(convertView==null){
                convertView= Ui_utils.inflate(R.layout.item_play_list);
                viewHolder=new ViewHolder();
                viewHolder.tv_paly_list= (TextView) convertView.findViewById(R.id.tv_item_play_list);
                viewHolder.npb_domnload= (NumberProgressBar) convertView.findViewById(R.id.npb_down_load);
                viewHolder.ll_select_music= (LinearLayout) convertView.findViewById(R.id.ll_setlect_music);
                convertView.setTag(viewHolder);
            }else{
                viewHolder= (ViewHolder) convertView.getTag();
            }
            ll_select_music=viewHolder.ll_select_music;
            if (selectedPosition == position) {
                ll_select_music.setBackgroundColor(Color.GREEN);
            } else {
                ll_select_music.setBackgroundColor(Color.TRANSPARENT);
            }

      if(mp3s.get(position).isDownload()){
          viewHolder.npb_domnload.setVisibility(View.GONE);
      }else {
         /* if(music_index>position){
          }else if(music_index==position){
              viewHolder.npb_domnload.setProgress(mp3s.get(music_index).getProgress());
          }*/
          Mp3 mp3 = mp3s.get(position);
          int progress = mp3s.get(position).getProgress();
          int progress1 = mp3.getProgress();
          if(progress1!=0){
              viewHolder.npb_domnload.setProgress(progress1);
          }else {
              viewHolder.npb_domnload.setProgress(0);
              viewHolder.npb_domnload.setVisibility(View.VISIBLE);
          }
      }
            viewHolder.tv_paly_list.setText(mp3s.get(position).getName());
            return convertView;
        }

        class ViewHolder{
            TextView tv_paly_list;
            NumberProgressBar npb_domnload;
            LinearLayout ll_select_music;
        }

    }
}
