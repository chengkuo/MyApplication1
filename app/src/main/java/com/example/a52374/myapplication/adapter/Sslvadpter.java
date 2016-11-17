package com.example.a52374.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a52374.myapplication.R;
import com.netease.nimlib.sdk.msg.model.RecentContact;


import java.util.ArrayList;

/**
 * Created by 52374 on 2016/11/14.
 */
public class Sslvadpter extends BaseAdapter {
    private ArrayList<RecentContact> list;
    private  Context context;
    private RecentContact rc;

    public Sslvadpter(ArrayList<RecentContact> list , Context Context){
        this.list=list;
        this.context=context;
    }



    @Override
    public int getCount() {
       return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final  HoldView hv;
        if(convertView==null){
          convertView= LayoutInflater.from(context).inflate(R.layout.sessionitem,parent,false);
          hv=new HoldView();
            hv.iv= (ImageView) convertView.findViewById(R.id.ssitemiv);
            hv.tv1= (TextView) convertView.findViewById(R.id.ssitemtv1);
            hv.tv2= (TextView) convertView.findViewById(R.id.ssitemtv2);
            hv.tv3= (TextView) convertView.findViewById(R.id.ssitemtv3);
            convertView.setTag(hv);
        }else {
            hv= (HoldView) convertView.getTag();
         }
          rc=list.get(position);

          hv.tv1.setText(rc.getFromAccount());
          hv.tv2.setText(rc.getContent());
          hv.tv3.setText(rc.getTime()+"");

        return convertView;
    }

    class HoldView {
        ImageView iv;
        TextView tv1,tv2,tv3;
    }
}
