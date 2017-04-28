package com.example.administrator.android_sta_vod.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private int selectedPosition = -1;// 选中的位置
    private LinearLayout ll_select_music;
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

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
      if(convertView==null){
          convertView= Ui_utils.inflate(R.layout.item_play_list);
          viewHolder=new ViewHolder();
          viewHolder.tv_paly_list= (TextView) convertView.findViewById(R.id.tv_item_play_list);
          viewHolder.iv_down_over = (ImageView) convertView.findViewById(R.id.iv_down_music_over);
          viewHolder.ll_select_music= (LinearLayout) convertView.findViewById(R.id.ll_setlect_music);

          convertView.setTag(viewHolder);
      }else{
         viewHolder= (ViewHolder) convertView.getTag();
      }
        ll_select_music=viewHolder.ll_select_music;
        if (selectedPosition == position) {
            ll_select_music.setBackgroundColor(Color.GREEN);
        } else {
            ll_select_music.setBackgroundColor(Color.TRANSPARENT);
        }
        /*=================================================================*/
        viewHolder.tv_paly_list.setText(play_list.get(position).getName());
      if(play_list.get(position).isDownload()){
          viewHolder.iv_down_over.setVisibility(View.VISIBLE);
      }

       /* int progress = play_list.get(position).getProgress();
        viewHolder.pb_music_down.setProgress(play_list.get(position).getProgress());
        int max_length = play_list.get(position).getMax_length();
        viewHolder.pb_music_down.setMax(play_list.get(position).getMax_length());*/

        return convertView;
    }

    class ViewHolder{
        TextView tv_paly_list;
       ImageView iv_down_over;
        LinearLayout ll_select_music;
    }

}
