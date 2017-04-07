package com.example.administrator.android_sta_vod.endecode;

/**
 * Created by Administrator on 2016/12/21.
 */
public class Packet {
    public byte[] data;
    public int timestamp;
    public int width;
    public int height;

    public Packet(byte[] data, int timestamp, int width, int height) {
        this.data = data;
        this.timestamp = timestamp;
        this.width = width;
        this.height = height;
    }
}
