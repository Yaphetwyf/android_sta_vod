package com.example.administrator.android_sta_vod.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.bean.Term;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import java.util.List;

/**
 * Created by Administrator on 2017/5/11.
 */

public class Sta_terminal_Adapter extends BaseAdapter {
    private List<Term> termList;
    private MyClickListener mListener;
    public Sta_terminal_Adapter(List<Term> termList, MyClickListener listener) {
        this.termList = termList;
        this.mListener=listener;
    }

    @Override
    public int getCount() {
        if(termList==null){
            return 0;
        }
        return termList.size();
    }

    @Override
    public Object getItem(int i) {
        return termList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            viewHolder=new ViewHolder();
            view= Ui_utils.inflate(R.layout.item_sta_terminal);
            viewHolder.tv_sta_term_list= (TextView) view.findViewById(R.id.tv_sta_vod_list);
            viewHolder.btn_delete_terminal= (Button) view.findViewById(R.id.btn_delete_termianl);
            viewHolder.ll_terminal_list= (LinearLayout) view.findViewById(R.id.ll_terminal_list);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
           viewHolder.tv_sta_term_list.setText(termList.get(position).getName());
        viewHolder.btn_delete_terminal.setOnClickListener(mListener);
        viewHolder.btn_delete_terminal.setTag(position);
        if(termList.get(position).isSpeaking()){
             viewHolder.ll_terminal_list.setBackgroundColor(Ui_utils.get_color(R.color.red));
        }else {
            viewHolder.ll_terminal_list.setBackgroundColor(Ui_utils.get_color(R.color.transparent));
        }
        return view;
    }



    class ViewHolder{
        TextView tv_sta_term_list;
        Button btn_delete_terminal;
        LinearLayout ll_terminal_list;
    }
    public static abstract class MyClickListener implements View.OnClickListener {
           @Override
               public void onClick(View v) {
                        myOnClick((Integer) v.getTag(), v);
                    }
               public abstract void myOnClick(int position, View v);
    }
}
