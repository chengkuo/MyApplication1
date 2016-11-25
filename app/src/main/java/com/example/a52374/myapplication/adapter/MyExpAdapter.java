package com.example.a52374.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a52374.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by a452542253 on 2016/11/22.
 */

public class MyExpAdapter extends BaseExpandableListAdapter{
    private HashMap<String,ArrayList<String>> map;
    private Context context;
    private ArrayList<String> list = new ArrayList<>();
    public MyExpAdapter(HashMap<String, ArrayList<String>> map,Context context) {
        this.map = map;
        this.context = context;
        list.addAll(map.keySet());
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return map.get(list.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder;
        if (convertView == null){
         convertView = LayoutInflater.from(context).inflate(R.layout.msg_item_from,parent,false);
            holder = new GroupHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.tv_msg_item);
            convertView.setTag(holder);
        }else {
            holder = (GroupHolder) convertView.getTag();
        }
        holder.tv.setText(list.get(groupPosition));

        return convertView;
    }
    class GroupHolder {
        TextView tv;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.msg_item_to,parent,false);
            holder = new ChildHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.tv_msg_to);
            convertView.setTag(holder);
        }else {
            holder = (ChildHolder) convertView.getTag();
        }
        holder.tv.setText(map.get(list.get(groupPosition)).get(childPosition));
        return convertView;
    }
    class ChildHolder {
        ImageView iv;
        TextView tv;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
