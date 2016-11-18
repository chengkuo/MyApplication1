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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 52374 on 2016/11/14.
 */
// session中listview的适配器
public class Sslvadpter extends BaseAdapter {
    private ArrayList<RecentContact> list;
    private  Context context;
    private RecentContact rc;


    public Sslvadpter(ArrayList<RecentContact> list , Context context){
        this.list=list;
        this.context=context;
    }
  // 时间转换的方法
    private String settime(long c){
     long tm=System.currentTimeMillis();//获得当前时间
        Date date=new Date(c);
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm:ss");
        if (tm-c>86400000){
          return  sdf1.format(date);
        }

       else {
            return  sdf2.format(date);
        }
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
          hv.tv3.setText(settime(rc.getTime()));

        return convertView;
    }

    class HoldView {
        ImageView iv;
        TextView tv1,tv2,tv3;
    }
}
