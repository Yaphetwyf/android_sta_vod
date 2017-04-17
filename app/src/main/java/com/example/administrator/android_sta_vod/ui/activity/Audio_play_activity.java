package com.example.administrator.android_sta_vod.ui.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.bean.AudioBean;
import com.example.administrator.android_sta_vod.service.Audioplay_service;
import com.example.administrator.android_sta_vod.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.example.administrator.android_sta_vod.R.id.btn_toggle_play;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class Audio_play_activity extends Base_activity {

    // 更新当前播放进度
    private static final int TYPE_UPDATE_CURRENT_POSITION = 0;

    private TextView tvArtist;
    private TextView music_name;
    private Button btnTogglePlay;
    private ImageView ivAudioPlayAnimation;
    private TextView tvCurrentPosition;
    private SeekBar sbCurrentPosition;
    private Button btnSwitchMode;
    private Button up;
    private Button down;
    AudioManager aManager;
    /** 歌词显示控件 */
   // private LyricsView lyricsView;

    private Audioplay_service mService;
    private AnimationDrawable animationDrawable;
    private int duration;

    @Override
    public int get_layout_res() {
        return R.layout.activity_audio_play;
    }

    @Override
    public void init_view() {
        aManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        tvArtist = findView(R.id.tv_artist);
        btnTogglePlay = findView(btn_toggle_play);
        tvCurrentPosition = findView(R.id.tv_current_position);
        sbCurrentPosition = findView(R.id.sb_current_position);
        btnSwitchMode = findView(R.id.btn_switch_play_mode);
       up= findView(R.id.btn_voice_up);
       down= findView(R.id.btn_voice_down);
        music_name= findView(R.id.tv_music_name);

        // 显示音频播放的动画效果
        init_Audio_Play_Animation();
    }

    /**
     * 显示音频播放的动画效果
     */
    private void init_Audio_Play_Animation() {
       ivAudioPlayAnimation = findView(R.id.iv_play_animation);
        animationDrawable = (AnimationDrawable)
                ivAudioPlayAnimation.getBackground();
    }

    @Override
    public void init_listener() {
        sbCurrentPosition.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 指定调节音乐的音频，增大音量,而现实音量图形示意
                aManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 指定调节音乐的音频，降低音量,而现实音量图形示意
                aManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            }
        });
    }

    SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener
            = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) { // 用户滑动seekbar, 则实现快进快退功能
                mService.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    @Override
    public void init_data() {

        Intent intent1 = new Intent(this, Audioplay_service.class);
        intent1.putExtras(getIntent().getExtras());  // 设置主界面传过来的数据，传递到Service
        startService(intent1);
        intent1 = new Intent(this, Audioplay_service.class);
        bindService(intent1, mServiceConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Audioplay_service.MyBinder binder = (Audioplay_service.MyBinder) service;
            mService = binder.mService;
            mService.openAudio();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    @Override
    public void onClick(View v, int btnId) {
        switch (btnId) {
            case btn_toggle_play:          // 播放或暂停
                togglePlay();
                break;
            case R.id.btn_prev:                 // 播放上一首
                mService.prev();
                break;
            case R.id.btn_next:                 // 播放下一首
                mService.next();
                btnTogglePlay.setBackgroundResource(R.drawable.btn_audio_pause);
                break;
            case R.id.btn_switch_play_mode:    // 切换播放模式
                switch_play_mode();
                break;

        }
    }

    /**
     *  切换播放模式
     */
    private void switch_play_mode() {
        int playMode = mService.switchPlayMode();
        update_switch_mode_BtnBG(playMode);
    }

    /**
     * 根据当前播放模式，显示不同的按钮背景
     * @param playMode
     */
    private void update_switch_mode_BtnBG(int playMode) {
        int btnResBG = R.drawable.loop;
        switch (playMode) {
            case Audioplay_service.PLAY_MODE_ORDER:       // 顺序播放
                btnResBG = R.drawable.btn_playmode_allrepeat;
                break;

            case Audioplay_service.PLAY_MODE_RANDOM:
                btnResBG = R.drawable.btn_playmode_random;
                break;
            default:
                throw new IllegalStateException("不能识别的模式");
        }
        btnSwitchMode.setBackgroundResource(btnResBG);
    }

    /**
     * 播放或暂停
     */
    private void togglePlay() {
        if (mService.isPlaying()) { // 播放 -> 暂停
            mService.pause();
            btnTogglePlay.setBackgroundResource(R.drawable.btn_audio_play);
            // 停止帧动画显示
            animationDrawable.stop();
        } else {                    // 暂停 -> 播放
            mService.start();
            btnTogglePlay.setBackgroundResource(R.drawable.btn_audio_pause);
            // 开始帧动画显示
            animationDrawable.start();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(AudioBean audio) {
        updateUI(audio);
    }

    /**
     * 服务开始播放音频，需要刷新界面显示
     * @param audio 正在播放的音频实体
     */
    private void updateUI(AudioBean audio) {
        super.setPageTitle(audio.title);        // 显示音频名称
     //   tvArtist.setText(audio.artist);         // 显示歌手
        music_name.setText(audio.title+"\n"+audio.artist);         // 显示歌手
        animationDrawable.start();              // 开始显示帧动画

        duration = mService.getDuration();      // 获取音频播放的总时长
        sbCurrentPosition.setMax(duration);
        updateCurrentPosition();                // 更新播放进度
    }

    /**
     * 更新播放进度
     */
    private void updateCurrentPosition() {
        int currentPosition = mService.getCurrentPosition();
        String text = Utils.formatDuration(currentPosition) + "/"
                + Utils.formatDuration(duration);
        tvCurrentPosition.setText(text);        // 显示播放进度与总时长
        sbCurrentPosition.setProgress(currentPosition);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        // 一进入音频播放界面后，需要获取上一次的播放模式，刷新按钮的背景
        int playMode = Utils.getAudioPlayMode(this, Audioplay_service.PLAY_MODE_ORDER);
        update_switch_mode_BtnBG(playMode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解绑服务
        unbindService(mServiceConnection);

        EventBus.getDefault().unregister(this);

        // 删除消息队列中所有的消息，退出Handler死循环
        mHandler.removeCallbacksAndMessages(null);
    }

    //===========接收服务的EventBus事件==(end)========================
}
