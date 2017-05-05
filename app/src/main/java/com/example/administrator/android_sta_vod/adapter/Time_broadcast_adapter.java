package com.example.administrator.android_sta_vod.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.bean.Task;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */

public class Time_broadcast_adapter extends BaseAdapter {

    private List<Task> tasks;
    private int selectedPosition = -1;// 选中的位置
    private LinearLayout ll_select_music;
    public Time_broadcast_adapter(List<Task> tasks) {

        this.tasks = tasks;
    }


    @Override
    public int getCount() {
        if(tasks!=null){
            return tasks.size();
        }
        return 0;
    }
    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }
    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            convertView= Ui_utils.inflate(R.layout.item_time_broadcast);
            viewHolder=new ViewHolder();
            viewHolder.tv_task_name= (TextView) convertView.findViewById(R.id.tv_task_name);
            viewHolder.tv_task_state= (TextView) convertView.findViewById(R.id.tv_task_state);
            viewHolder.ll_select_music= (LinearLayout) convertView.findViewById(R.id.ll_setlect_music);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        ll_select_music=viewHolder.ll_select_music;
        if (selectedPosition == position) {
            ll_select_music.setBackgroundColor(Color.BLUE);
        } else {
            ll_select_music.setBackgroundColor(Color.TRANSPARENT);
        }

        viewHolder.tv_task_name.setText(tasks.get(position).getName());
        String status = tasks.get(position).getStatus();

        if(tasks.get(position).getStatus().equals("2")){
            viewHolder.tv_task_state.setText(Ui_utils.get_string(R.string.hand));
        }else if(tasks.get(position).getStatus().equals("1")){
            viewHolder.tv_task_state.setText(Ui_utils.get_string(R.string.auto));
        }else {
            viewHolder.tv_task_state.setText(Ui_utils.get_string(R.string.nrun));
        }

        return convertView;
    }
    class ViewHolder{
        TextView tv_task_name;
        TextView tv_task_state;
        LinearLayout ll_select_music;
    }
}
