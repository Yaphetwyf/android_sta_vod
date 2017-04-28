package com.example.administrator.android_sta_vod.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.android_sta_vod.R;
import com.example.administrator.android_sta_vod.bean.Area;
import com.example.administrator.android_sta_vod.bean.Term;
import com.example.administrator.android_sta_vod.utils.Ui_utils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/27.
 */

public class Terminal_expandlist_adapter extends BaseExpandableListAdapter {
    //一级目录
    private ArrayList<Area> father_list;
    //二级目录
    private ArrayList<ArrayList<Term>> child_list;
    //被选中的设备
    private ArrayList<Term> checked_term_list = new ArrayList<>();
    LayoutInflater inflater;
    public Terminal_expandlist_adapter(ArrayList<Area> father_list, ArrayList<ArrayList<Term>> child_list) {
        this.father_list = father_list;
        this.child_list = child_list;
        inflater = LayoutInflater.from(Ui_utils.get_context());
        isliss = new boolean[father_list.size()];
        for (int i = 0; i < isliss.length; i++)
        {
            isliss[i] = false;
        }
    }
    public void set_data(ArrayList<Area> father_list
            , ArrayList<ArrayList<Term>> child_list)
    {
        this.father_list = father_list;
        this.child_list = child_list;
        notifyDataSetChanged();
    }

    private final boolean[] isliss;
    public ArrayList<Term> get_terms()
    {
        return checked_term_list;
    }
    public void set_checked_list(ArrayList<Term> checked_term_list)
    {
        this.checked_term_list = checked_term_list;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getGroupCount() {
        return father_list.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return child_list.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return father_list.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return child_list.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        AreaHolder areaHolder;
        if(view==null){
           view= inflater.inflate(R.layout.item_terminal_group,null);
            areaHolder=new AreaHolder();
            areaHolder.tv_item_area_group = (TextView) view.findViewById(R.id.tv_item_area_name);
            view.setTag(areaHolder);
        }else{
            areaHolder= (AreaHolder) view.getTag();
        }
            areaHolder.tv_item_area_group.setText(father_list.get(i).getName());
        return view;
    }
    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        isliss[groupPosition] = false;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        TermHolder termHolder;
        if(view==null){
            view=inflater.inflate(R.layout.item_terminal_child,null);
            termHolder=new TermHolder();
            termHolder.cb_item_term_child= (CheckBox) view.findViewById(R.id.cb_item_term_child);
            termHolder.tv_item_term_name= (TextView) view.findViewById(R.id.tv_item_term_name);
            termHolder.ll_item_child= (LinearLayout) view.findViewById(R.id.ll_item_child);
            view.setTag(termHolder);
        }else {
            termHolder= (TermHolder) view.getTag();
        }
        String name = child_list.get(i).get(i1).getName();
        if(termHolder.tv_item_term_name!=null){
            termHolder.tv_item_term_name.setText(child_list.get(i).get(i1).getName());
            Log.d("tv_item_term_name","+++++++++tv_item_term_name");
        }

        if (i1 == child_list.get(i).size() - 1)
        {
            isliss[i] = true;
        }
        termHolder.ll_item_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(termHolder.cb_item_term_child.isChecked()){
                    termHolder.cb_item_term_child.setChecked(false);
                    if (checked_term_list.contains(child_list.get(i).get(i1)))
                    {
                        Log.d("checked_term_list", "delete" + child_list.get(i).get(i1).getName());
                        checked_term_list.remove(child_list.get(i).get(i1));
                    }
                }else {
                    termHolder.cb_item_term_child.setChecked(true);
                    if (!checked_term_list.contains(child_list.get(i).get(i1)))
                    {
                        Log.d("checked_term_list", "add" + child_list.get(i).get(i1).getName());
                        checked_term_list.add(child_list.get(i).get(i1));
                    }
                }
            }
        });
        if (checked_term_list.contains(child_list.get(i).get(i1)))
        {
            termHolder.cb_item_term_child.setChecked(true);
        } else
        {
            termHolder.cb_item_term_child.setChecked(false);
        }
        return view;
    }
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
    class AreaHolder{
        TextView tv_item_area_group;
    }
    class TermHolder{
        LinearLayout ll_item_child;
        CheckBox cb_item_term_child;
        TextView tv_item_term_name;
    }
}
