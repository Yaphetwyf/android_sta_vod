package com.example.administrator.android_sta_vod.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;


import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.base.Const;
import com.example.administrator.android_sta_vod.bean.AudioBean;
import com.example.administrator.android_sta_vod.inference.I_audio_play;
import com.example.administrator.android_sta_vod.ui.activity.Audio_play_activity;
import com.example.administrator.android_sta_vod.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;


/**
 * 音频播放服务
 */
public class Audioplay_service extends Service implements I_audio_play {

    /** 要播放第几个音频 */
    private int position = -1;

    /** 所有的音频数据 */
    private ArrayList<AudioBean> audioDatas;

    /** 要播放的音频实体 */
    private AudioBean mCurrentAudio;
    /**
     * 打开是否是同一首音频
     * true： 不会从头开始播放
     */
    private boolean openThemSameAudio = false;

    // 服务的代理对象
    public class MyBinder extends Binder {
        public Audioplay_service mService = Audioplay_service.this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 接收点击通知后传过来的what参数
        if(intent!=null){
            int what = intent.getIntExtra("what", -1);

            switch (what) {
                case NOTIFICATION_ROOT:     // 点击通知的根节点进入Activity，然后再启动服务，进入到此方法
                    openThemSameAudio = true;
                    break;
                case NOTIFICATION_PREV:     // 点击通知上一首启动服务进入此方法
                    prev();
                    break;
                case NOTIFICATION_NEXT:     // 点击通知下一首启动服务进入此方法
                    next();
                    break;

                default:                    // 从主界面点击进来，没有带what参数
                    int temp_position_ = intent.getIntExtra(Const.KEY_POSITION, -1);
                    if (temp_position_ == position) {  // 表示打开的是同一首歌
                        openThemSameAudio = true;
                    } else {
                        openThemSameAudio = false;
                    }

                    audioDatas = (ArrayList<AudioBean>)
                            intent.getSerializableExtra(Const.KEY_DATA);

                    position = temp_position_;
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void openAudio() {

        if (position == -1 || audioDatas == null || audioDatas.size() < 1) {
            return;
        }

        // 要播放的音频实体
        mCurrentAudio = audioDatas.get(position);
        // showToast("开始播放：" + mCurrentAudio.title);

        // 打开同一首歌，不从头开始播放
        if (openThemSameAudio) {
            openThemSameAudio = false;
            // 发送EventBus事件，通知界面刷新
            sendEventBus();
            return;
        }
        release();
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setDataSource(mCurrentAudio.path); // 要播放音频的路径
            mMediaPlayer.prepareAsync();    // 异步缓冲
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    MediaPlayer.OnCompletionListener mOnCompletionListener
            = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            // 播放完成后，自动切换到下一首音频
            if (position != audioDatas.size() - 1) {
                next();
            }else {
                pause();
            }

        }
    };

    MediaPlayer.OnPreparedListener mOnPreparedListener
            = new MediaPlayer.OnPreparedListener () {

        @Override // 音频缓冲结束后，开始播放
        public void onPrepared(MediaPlayer mp) {
            start();

            sendEventBus();
        }
    };

    /**
     * 发送EventBus事件，通知界面刷新
     */
    private void sendEventBus() {
        // mCurrentAudio: 当前正在播放的音频
        EventBus.getDefault().post(mCurrentAudio);
    }

    private MediaPlayer mMediaPlayer;

    private void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            // 显示通知
            showCustomNotification();
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            // 取消通知
            cancelNotification();
        }
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public void prev() {
        switch (mCurrentPlayMode) {
            case PLAY_MODE_ORDER:       // 顺序循环播放
                // 如果当前正在播放第一首，当用户点击播放上一首时，播放最后一首
                if (position == 0) {
                    position = audioDatas.size() - 1;
                } else {
                    position--;
                }
                break;

            case PLAY_MODE_RANDOM:     // 随机播放
                position = mRandom.nextInt(audioDatas.size());
                break;
        }
        openAudio();
        // 显示通知
        showCustomNotification();
    }

    private  Random mRandom = new Random();

    @Override
    public void next() {

        switch (mCurrentPlayMode) {
            case PLAY_MODE_ORDER:       // 顺序循环播放
                // 如果当前正在播放最后一首，当用户点击播放下一首时，播放第一首
                if (position == audioDatas.size() - 1) {
                    position = 0;
                } else {
                    position++;
                }
                break;

            case PLAY_MODE_RANDOM:     // 随机播放
                position = mRandom.nextInt(audioDatas.size());
                break;
        }
        openAudio();
        // 显示通知
        showCustomNotification();
    }

    @Override
    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    //============切换播放模式====(begin)=======================
    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // 读取上一次的播放模式, 并根据该模式进行播放
        mCurrentPlayMode = Utils.getAudioPlayMode(this, PLAY_MODE_ORDER);
    }

    /** 顺序播放 */
    public static final int PLAY_MODE_ORDER = 1;

    /** 随机播放 */
    public static final int PLAY_MODE_RANDOM = 3;

    /** 当前播放模式 */
    private int mCurrentPlayMode = PLAY_MODE_ORDER;

    // 切换播放模式
    @Override
    public int switchPlayMode() {
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

    //============切换播放模式====(end)=======================


    //===============通知提示==(begin)=====================

    /** 点击了通知栏的根节点 */
    private static final int NOTIFICATION_ROOT = 0;
    /** 点击了通知栏的上一首 */
    private static final int NOTIFICATION_PREV = 1;
    /** 点击了通知栏的下一首 */
    private static final int NOTIFICATION_NEXT = 2;

    private NotificationManager notificationManager;
    private int notificationId = R.drawable.modelmp3;

    /** 显示自定义通知 */
    private void showCustomNotification() {
        // 1. 创建通知，并设置属性
        // 使用v4包中的NotificationCompat，可以兼容不同的版本
        Notification notification = new NotificationCompat
                .Builder(this)
                .setSmallIcon(R.drawable.modelmp3)
                .setTicker("" + mCurrentAudio.title)
                .setWhen(System.currentTimeMillis())
                // 设置点击通知后做的逻辑(打开Activity或者启动Service或者发送广播)
                .setContent(getRemoteView())
                .build();

        // 2. 显示通知
        notificationManager.notify(notificationId, notification);
    }

    /** 取消通知 */
    private void cancelNotification() {
        notificationManager.cancel(notificationId);
    }

    private PendingIntent getPendingIntentActivity(int what) {
        int requestCode = what;
        Intent intent = new Intent(this, Audio_play_activity.class);
        intent.putExtra("what", what);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;  // 更新当前
        return PendingIntent.getActivity(this, requestCode, intent, flags);
    }

    private PendingIntent getPendingIntentService(int what) {
        int requestCode = what;
        Intent intent = new Intent(this, Audioplay_service.class);
        intent.putExtra("what", what);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;  // 更新当前
        return PendingIntent.getService(this, requestCode, intent, flags);
    }


    private RemoteViews getRemoteView() {
        RemoteViews  remoteViews = new RemoteViews(getPackageName(),
                R.layout.activity_custom_notification);

        remoteViews.setTextViewText(R.id.tv_title, mCurrentAudio.title);
        remoteViews.setTextViewText(R.id.tv_content, mCurrentAudio.artist);

        // 设置控件的点击事件
        remoteViews.setOnClickPendingIntent(R.id.root, getPendingIntentActivity(NOTIFICATION_ROOT));
        remoteViews.setOnClickPendingIntent(R.id.btn_prev, getPendingIntentService(NOTIFICATION_PREV));
        remoteViews.setOnClickPendingIntent(R.id.btn_next, getPendingIntentService(NOTIFICATION_NEXT));

        return remoteViews;
    }

    //===============通知提示==(end)=====================


}














