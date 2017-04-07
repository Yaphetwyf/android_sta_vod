package com.example.administrator.android_sta_vod.inference;

/**
 * Created by Administrator on 2016/12/8.
 */
public interface I_audio_play {

    /** 打开音频，开始播放 */
    public void openAudio();

    /** 开始播放 */
    public void start();

    /** 暂停播放 */
    public void pause();

    /** 是否正在播放 */
    public boolean isPlaying();

    /** 快进快退(跳转到某个位置开始播放) */
    public void seekTo(int position);

    /** 上一首 */
    public void prev();

    /** 下一首 */
    public void next();

    /** 获取播放总时长 */
    public int getDuration();

    /** 获取当前播放进度 */
    public int getCurrentPosition();

    /** 切换播放模式 */
    public int switchPlayMode();

}















