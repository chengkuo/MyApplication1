package com.example.a52374.myapplication.fragment;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.adapter.Sslvadpter;
import com.example.a52374.myapplication.avtivity.DuiHuaActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 52374 on 2016/11/14.
 */

//最近会话界面
public class Session extends Fragment {

    private ListView  lv;
    private ArrayList<RecentContact> list=new ArrayList<>();
    private Sslvadpter adpter;
    private Context context;
    private boolean register=true;
    private Map<String,ArrayList<IMMessage>> map=new HashMap<>();//存储主界面发过来的信息
    private ArrayList<IMMessage> list1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.session,container,false);

    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         context=getContext();
        initview(view);
        initdata();
        initadapter();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(context, DuiHuaActivity.class);
                String account =list.get(position).getFromAccount();
                ArrayList<IMMessage> al =map.get(account);
               i.putExtra("account",account);
               i.putExtra(account,al);
                startActivity(i);
               map.remove(account);
             }
        });




        //  注册/注销观察者
        NIMClient.getService(MsgServiceObserve.class)
                .observeRecentContact(messageObserver, register);
    }

    //  创建观察者对象
    Observer<List<RecentContact>> messageObserver =
            new Observer<List<RecentContact>>() {
                @Override
                public void onEvent(List<RecentContact> messages) {
                    list.clear();
                    list.addAll(messages);
                    Log.i("tttttt","观察者"+list.get(list.size()-1).getContent());
                    adpter.notifyDataSetChanged();
                }
            };

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
                        list.clear();
                        list.addAll(recents);

                        adpter.notifyDataSetChanged();
                    }
                });

    }

    private void initview(View view) {
        lv= (ListView) view.findViewById(R.id.sslv);

    }

      //接受其他页面传递的数据
     public  void change(List<IMMessage> imMessages,String account){
         ArrayList<IMMessage> al=null;
         al=map.get(account);
         if(al!=null){
             al.addAll(imMessages);
             map.put(account,al);
         }else{
            al=new ArrayList<>();
            al.addAll(imMessages);
            map.put(account,al);
         }

     }


    //LIST排序的规则
      private static Comparator<RecentContact> contactComparator=new Comparator<RecentContact>() {
        @Override
        public int compare(RecentContact lhs, RecentContact rhs) {
             long time=lhs.getTime()-rhs.getTime();


            return time==0 ? 1 :(time>0?-1:1);
        }
    };

    //list排序的方法
     private void  sortlist(ArrayList<RecentContact> list){
         if(list.size()>0){
             Collections.sort(list,contactComparator);
         }
     }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NIMClient.getService(MsgServiceObserve.class)
                .observeRecentContact(messageObserver, false);
    }
}
