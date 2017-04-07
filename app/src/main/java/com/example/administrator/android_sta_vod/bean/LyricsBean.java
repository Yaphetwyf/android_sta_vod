package com.example.administrator.android_sta_vod.bean;

/**
 * 一行歌词
 */
public class LyricsBean {

    /** 歌词文本 */
    public String text;

    /** 该行歌词的开始显示时间 */
    public long startTime;

    @Override
    public String toString() {
        return "LyricsBean{" +
                "text='" + text + '\'' +
                ", startTime=" + startTime +
                '}';
    }

}
