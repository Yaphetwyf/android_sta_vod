package com.example.administrator.android_sta_vod.bean;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class Down_music{

    private boolean isDownload; //下载状态
    private int progress; //进度

    public boolean isDownload() {
        return isDownload;
    }
    public void setDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }
    public int getProgress() {
        return progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
}
