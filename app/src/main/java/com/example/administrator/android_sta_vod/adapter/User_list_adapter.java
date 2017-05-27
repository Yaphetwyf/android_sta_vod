package com.example.administrator.android_sta_vod.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.bean.User;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import java.util.List;

/**
 * Created by Administrator on 2017/3/29 0029.
 */

public class User_list_adapter extends BaseAdapter {

    private List<User> userList;
    public User_list_adapter(List<User> userList) {
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
            viewHolder.nick_name= (TextView) convertView.findViewById(R.id.nick_name_item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(userList.get(position).getName());
        viewHolder.nick_name.setText("( "+userList.get(position).getNick_name()+" )");
        if(userList.get(position).getStatus().equals("1")){
            viewHolder.image.setImageResource(R.drawable.user_on_line);
        }else{
            viewHolder.image.setImageResource(R.drawable.cancel_user);
        }

        return convertView;
    }

class ViewHolder
{
    public TextView name;
    public ImageView image;
    public TextView nick_name;
}
}
