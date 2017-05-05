package com.example.administrator.android_sta_vod.bean;

import com.daimajia.numberprogressbar.NumberProgressBar;

/**
 * Created by Administrator on 2017/5/4.
 */

public class DownLoad  {
    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public int getData_length() {
        return data_length;
    }

    public void setData_length(int data_length) {
        this.data_length = data_length;
    }



    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }
    private boolean isDownload;
    private long file_size;
    public int data_length;

    public NumberProgressBar getNumberProgressBar() {
        return numberProgressBar;
    }

    public void setNumberProgressBar(NumberProgressBar numberProgressBar) {
        this.numberProgressBar = numberProgressBar;
    }

    public NumberProgressBar numberProgressBar;
    public Mp3 getMp3() {
        return mp3;
    }

    public void setMp3(Mp3 mp3) {
        this.mp3 = mp3;
    }

    public Mp3 mp3;


}
