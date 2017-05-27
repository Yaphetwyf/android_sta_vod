package com.example.administrator.android_sta_vod.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.bean.Mp3;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import java.util.List;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

public class Remote_play_list_adapter extends BaseAdapter {
    private List<Mp3> play_list;

    private int selectedPosition = -1;// 选中的位置
    private LinearLayout ll_select_music;
    public Remote_play_list_adapter(List<Mp3> play_list) {
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
          viewHolder.npb_domnload= (NumberProgressBar) convertView.findViewById(R.id.npb_down_load);
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
       int progress = play_list.get(position).getProgress();
        int max = play_list.get(position).getMax();
        Log.d("progress",""+progress);
        Log.d("max",""+max);
        viewHolder.npb_domnload.setVisibility(View.GONE);
        /*if(play_list.get(position).isDownload()){
            viewHolder.npb_domnload.setProgress(100);
        }else {
            viewHolder.npb_domnload.setProgress(play_list.get(position).getProgress());
            viewHolder.npb_domnload.setMax(play_list.get(position).getMax());
        }*/
        viewHolder.npb_domnload.setProgress(play_list.get(position).getProgress());
        viewHolder.npb_domnload.setMax(play_list.get(position).getMax());
        return convertView;
    }

    class ViewHolder{
        TextView tv_paly_list;
        NumberProgressBar npb_domnload;
        LinearLayout ll_select_music;
    }

}
