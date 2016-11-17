package com.example.a52374.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.adapter.Sslvadpter;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 52374 on 2016/11/14.
 */
public class Session extends Fragment {

    private ListView  lv;
    private ArrayList<RecentContact> list=new ArrayList<>();
    private Sslvadpter adpter;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.session,container,false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         context=getActivity();
        initview(view);
//        initdata();
//        initadapter();
    }

    private void initadapter() {
      adpter=new Sslvadpter(list,context);
      lv.setAdapter(adpter);

    }

    private void initdata() {
        NIMClient.getService(MsgService.class).queryRecentContacts()
                .setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
                    @Override
                    public void onResult(int code, List<RecentContact> recents, Throwable e) {
                        // recents参数即为最近联系人列表（最近会话列表）
                        Log.i("tmd",list.toString()+"!!!!!!!!!!!!");
                        list.addAll(recents);

                        adpter.notifyDataSetChanged();
                    }
                });

    }

    private void initview(View view) {
        lv= (ListView) view.findViewById(R.id.sslv);

    }
     public static void change(){

     }

}
