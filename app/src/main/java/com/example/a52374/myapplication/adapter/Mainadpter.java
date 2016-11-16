package com.example.a52374.myapplication.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by 52374 on 2016/11/15.
 */
public class Mainadpter extends FragmentPagerAdapter {
      private ArrayList<Fragment> list;

    public Mainadpter(FragmentManager fm,ArrayList<Fragment> list) {
        super(fm);
        this.list=list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0){
            return  "会话";
        }
        else  if(position==1){
            return  "通讯录";
        } else if(position==2){
            return  "直播";
        }
        return  null;
    }
}
