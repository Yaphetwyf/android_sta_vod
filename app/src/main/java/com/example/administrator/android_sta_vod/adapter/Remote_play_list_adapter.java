package com.example.administrator.android_sta_vod.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.bean.Mp3;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

public class Remote_play_list_adapter extends BaseAdapter {
    private List<Mp3> play_list;

    public Remote_play_list_adapter(ArrayList<Mp3> play_list) {
        this.play_list = play_list;
    }

    @Override
    public int getCount() {
        if(play_list!=null){
            return play_list.size();
        }
           return 0;
    }

    @Override
    public Object getItem(int position) {
        return play_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
      if(convertView==null){
          convertView= Ui_utils.inflate(R.layout.item_play_list);
          viewHolder=new ViewHolder();
          viewHolder.tv_paly_list= (TextView) convertView.findViewById(R.id.tv_item_play_list);
          convertView.setTag(viewHolder);
      }else{
         viewHolder= (ViewHolder) convertView.getTag();
      }
        viewHolder.tv_paly_list.setText(play_list.get(position).getName());
        return convertView;
    }
    class ViewHolder{
        TextView tv_paly_list;
    }
}
