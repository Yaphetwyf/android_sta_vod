package com.example.administrator.android_sta_vod.ui.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.adapter.Audio_adapter;
import com.example.administrator.android_sta_vod.base.Const;
import com.example.administrator.android_sta_vod.bean.AudioBean;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class Audio_activity extends Base_activity {
    private ListView listView;

    private Audio_adapter mAdapter;

    @Override
    public int get_layout_res() {
        return R.layout.activity_audio;

    }
    @Override
    public void init_view() {
        listView= (ListView) findViewById(R.id.ll_local_activity);
    }


    @Override
    public void init_listener() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                // 获取所有的视频数据，并传到播放界面
                ArrayList<AudioBean> audioDatas= getAllAudioDatas(cursor);
                Intent intent = new Intent(getApplicationContext(), Audio_play_activity.class);
                intent.putExtra(Const.KEY_POSITION, position);
                intent.putExtra(Const.KEY_DATA, audioDatas);

                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Audio_activity.this);
                builder.setMessage(R.string.are_you_sure);
                builder.setTitle(R.string.Prompt);

                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                        ArrayList<AudioBean> audioDatas= getAllAudioDatas(cursor);
                       String path =audioDatas.get(position).path;
                        File file=new File(path);

                        if(file!=null){
                          file.delete();
                            deleteMusic(path);
                        }
                        mAdapter.notifyDataSetChanged();

                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    private void deleteMusic(String path){
        getApplicationContext().getContentResolver().delete(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MediaStore.MediaColumns.DATA + "='" + path + "'", null
        );
    }
    /**
     * 获取所有的音频数据
     * @param cursor
     * @return
     */
    private ArrayList<AudioBean> getAllAudioDatas(Cursor cursor) {
        ArrayList<AudioBean> audioData = new ArrayList<AudioBean>();
        cursor.moveToFirst();

        do {
            audioData.add(AudioBean.fromCursor(cursor));
        } while(cursor.moveToNext());

        return audioData;
    }

    @Override
    public void init_data() {
        load_audio_data();
    }

    @Override
    public void onClick(View v, int btnId) {

    }
    /**
     * 读取本地音频文件
     */
    private void load_audio_data() {
        // 方式1：通过getContentResolver().query()同步查询数据
        // 方式2：通过AsyncQueryHandler类异步查询
        ContentResolver resolver = this.getContentResolver();
        AsyncQueryHandler queryHandler = new AsyncQueryHandler(resolver) {
            // 异步查询数据结束后调用
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                // 显示列表
                mAdapter = new Audio_adapter(getApplicationContext(), cursor);
                listView.setAdapter(mAdapter);
            }
        };

        int token = 0;                  // 相当于Message的what
        Object cookie = null;           // 相当于Message的obj
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;          // 要查询数据的uri
        String[] projection = {         // 要查询的字段（列）
                // user_id AS _id
                MediaStore.Audio.Media._ID ,
                MediaStore.Audio.Media.TITLE,                   // 音频名称
                MediaStore.Audio.Media.ARTIST,                  // 音频歌手
                MediaStore.Audio.Media.ALBUM,                   // 音频专辑
                MediaStore.Audio.Media.DATA                     // 音频保存的本地路径
        };
        String selection = null;                // 查询条件
        String[] selectionArgs = null;          // 查询条件参数
        String orderBy =  MediaStore.Audio.Media.TITLE + "  ASC ";  // 按视频名称的升序排列  DESC

        // 发起异步查询（在子线程中查询）
        queryHandler.startQuery(token, cookie, uri,
                projection, selection, selectionArgs ,orderBy);
    }
}
