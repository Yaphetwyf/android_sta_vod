package com.example.administrator.android_sta_vod.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.bean.AudioBean;


public class Audio_adapter extends CursorAdapter {

    public Audio_adapter(Context context, Cursor c) {
        // boolean autoRequery 自动重新查询, 设置为true，当数据改变时，列表才会自动刷新
        super(context, c, true);
    }

    // 创建列表项item
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // 1. 填充布局
      // View item = Global.inflate(R.layout.play_item, parent);
       View item = LayoutInflater.from(context).inflate(R.layout.play_item,parent,false);
                // 2.创建Holder
        ViewHolder holder = new ViewHolder();
        holder.tvTitle = (TextView) item.findViewById(R.id.tv_title);
        holder.tvArtist = (TextView) item.findViewById(R.id.tv_artist);

        // 3. setTag保存holder
        item.setTag(holder);

        // 4.返回列表项
        return item;
    }

    // 刷新列表项item子控件的显示
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // 1. 获取Holder
        ViewHolder holder = (ViewHolder) view.getTag();

        // 2. 获取列表项对应的javabean

        // 把游标指向的那一条数据（position）转换成一个实体对象
        AudioBean bean = AudioBean.fromCursor(cursor);

        // 3. 显示子控件
        holder.tvTitle.setText(bean.title);
        String str = bean.artist + " | " + bean.album;
        holder.tvArtist.setText(str);
    }


    private static class ViewHolder {
        TextView tvTitle;
        TextView tvArtist;
    }
}











