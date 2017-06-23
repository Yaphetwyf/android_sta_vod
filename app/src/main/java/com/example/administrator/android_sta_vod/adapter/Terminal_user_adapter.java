package com.example.administrator.android_sta_vod.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.bean.Term;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import java.util.List;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class Terminal_user_adapter extends BaseAdapter {

    private List<Term> userList;
    public Terminal_user_adapter(List<Term> userList) {
        this.userList=userList;
    }

    @Override
    public int getCount() {
        if(userList!=null){
            return userList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){

            convertView= Ui_utils.inflate(R.layout.activity_call_grid_item);
            viewHolder=new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name_item);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image_item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(userList.get(position).getName());
        Log.d("terminal_state",userList.get(position).getStatus()+"");
        if(userList.get(position).getStatus().equals("1")){
            viewHolder.image.setImageResource(R.drawable.terminal_online);
        }else if(userList.get(position).getStatus().equals("3")||userList.get(position).getStatus().equals("2")){
            viewHolder.image.setImageResource(R.drawable.term_working);
        }else {
            viewHolder.image.setImageResource(R.drawable.terminal_offline);
        }
        return convertView;
    }
    class ViewHolder
    {
        public TextView name;
        public ImageView image;
    }
}
