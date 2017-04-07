package com.example.administrator.android_sta_vod.endecode;

/**
 * Created by AVSZ on 2017/3/1.
 */

public class Pcm_packet {
    private byte[] data;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    private long time;

    public Pcm_packet(byte[] data, long time){
        this.data = data;
        this.time = time;
    }


}
