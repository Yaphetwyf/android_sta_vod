package com.example.administrator.android_sta_vod.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * 视频实体对象
 */
public class AudioBean implements Serializable {

//    MediaStore.Audio.Media._ID ,
//    MediaStore.Audio.Media.TITLE,                   // 音频名称
//    MediaStore.Audio.Media.ARTIST,                  // 音频歌手
//    MediaStore.Audio.Media.ALBUM,                   // 音频专辑
//    MediaStore.Audio.Media.DATA                     // 音频保存的本地路径

    public int id;
    public String title;
    public String artist;
    public String album;
    public String path;


    /**
     * 把游标指向的那一条数据（position）转换成一个实体对象
     * @param cursor
     * @return
     */
    public static AudioBean fromCursor(Cursor cursor) {
        AudioBean bean = new AudioBean();

        bean.id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
        bean.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        bean.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        bean.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
        bean.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

        return bean;
    }
}












