package com.example.a52374.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a52374.myapplication.R;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 52374 on 2016/11/22.
 */
public class Txlllvadapter  extends BaseExpandableListAdapter {
    private Context context;
 //   private Map<ArrayList<String>,ArrayList<NimUserInfo>> map;
      private  ArrayList<String> flist;     //1级列表数据源
      private  ArrayList<NimUserInfo> list;//我的好友数据源
    public  Txlllvadapter(Context context,ArrayList<NimUserInfo> list,ArrayList<String> flist){
         this.context=context;
         this.list=list;
        this.flist=flist;
    }

    @Override
    public int getGroupCount() {
        return flist.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(groupPosition==0){
              return list.size();
        }


        return 0;
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

        convertView= LayoutInflater.from(context).inflate(R.layout.txllvf,parent,false);
        TextView tv= (TextView) convertView.findViewById(R.id.txllvtv1);
        tv.setText(flist.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if(groupPosition==0){
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.txl_lv_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mTvName.setText(list.get(childPosition).getName());
        holder.mIvTouxiang.setImageResource(R.mipmap.ic_launcher);
        return  convertView;}
        return  null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    class ViewHolder {
        public ImageView mIvTouxiang;
        public TextView mTvName;

        public ViewHolder(View rootView) {
            this.mIvTouxiang = (ImageView) rootView.findViewById(R.id.iv_touxiang);
            this.mTvName = (TextView) rootView.findViewById(R.id.tv_name);
        }

    }
}
