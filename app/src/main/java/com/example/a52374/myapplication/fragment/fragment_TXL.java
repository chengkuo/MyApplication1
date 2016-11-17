package com.example.a52374.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.avtivity.DuiHuaActivity;
import com.example.a52374.myapplication.avtivity.PersondataActivity;
import com.example.a52374.myapplication.mybean.Bean_TXL;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 52374 on 2016/11/16.
 */
public class fragment_TXL extends Fragment{
    private String[] xitong = {"验证提醒", "讨论组", "高级群", "黑名单", "我的电脑"};
    private int[] xitongid = {R.drawable.icon_verify_remind, R.drawable.ic_secretary, R.drawable.ic_advanced_team, R.drawable.ic_black_list, R.drawable.ic_my_computer};
    private ListView lv_xitong;
    private ArrayList<Bean_TXL> data = new ArrayList<>();
    private MyAdapter adapter;
    private Context context;
    private ListView lv_haoyou;
    private  ArrayList<String> list=new ArrayList<>();//所有好友的账号
    private static ArrayList<NimUserInfo> data_haoyou =new ArrayList<>();//所有好友
    private static MyAdapter_haoyou adapter_haoyou;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_txl, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context=getActivity();
        initView(view);
        //初始化系统的listview中的数据
        initData();
        //初始化系统中的adapter
        initAdapter();
        //初始化好友
        initdata_hao();
        //初始化好友Adapter
        initAdapter_haoyou();

        //设置好友点击事件
        lv_haoyou.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context,PersondataActivity.class);
                intent.putExtra("user",data_haoyou.get(position));
                //intent.putExtra("account",data_haoyou.get(position).getAccount());
                startActivity(intent);


            }
        });

    }

    private void initAdapter_haoyou() {
        adapter_haoyou = new MyAdapter_haoyou();

        lv_haoyou.setAdapter(adapter_haoyou);
    }

    private void initdata_hao() {
      /*  for (int i = 0; i < 10; i++) {
            data_haoyou.add("item   " + i);
        }*/
        list .addAll ( NIMClient.getService(FriendService.class).getFriendAccounts()); // 获取所有好友帐号
        if(list.size()>0){
        data_haoyou .addAll( NIMClient.getService(UserService.class).getUserInfoList(list)) ; }// 获取所有好友用户资料
    }

    private void initAdapter() {
        adapter = new MyAdapter();
        lv_xitong.setAdapter(adapter);
    }

    private void initData() {


        for (int i = 0; i < xitongid.length; i++) {
            data.add(new Bean_TXL(xitong[i], xitongid[i]));
        }
    }

    private void initView(View view) {
        lv_xitong = (ListView) view.findViewById(R.id.lv_xitong);
        lv_haoyou = (ListView) view.findViewById(R.id.lv_haoyou);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.txl_lv_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.iv.setImageResource(data.get(position).getImg());
            holder.tv.setText(data.get(position).getName());

            return convertView;
        }


        class ViewHolder {
            ImageView iv;
            TextView tv;

            public ViewHolder(View rootView) {
                iv = (ImageView) rootView.findViewById(R.id.iv_touxiang);
                tv = (TextView) rootView.findViewById(R.id.tv_name);
            }

        }
    }

    class MyAdapter_haoyou extends BaseAdapter {

        @Override
        public int getCount() {
            return data_haoyou.size();
        }

        @Override
        public Object getItem(int position) {
            return data_haoyou.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.txl_lv_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mTvName.setText(data_haoyou.get(position).getName());
            holder.mIvTouxiang.setImageResource(R.mipmap.ic_launcher);
            return convertView;
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
   // 添加好友后更新页面
    public static   void change(String account)
    {
        ArrayList<String> accounts=new ArrayList<>();
        accounts.add(account);
        NIMClient.getService(UserService.class).fetchUserInfo(accounts)
                .setCallback(new RequestCallback<List<NimUserInfo>>() {
                    @Override
                    public void onSuccess(List<NimUserInfo> nimUserInfos) {
                         NimUserInfo user=nimUserInfos.get(0);
                        data_haoyou.add(user);
                    }

                    @Override
                    public void onFailed(int i) {

                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }

                     });
        adapter_haoyou.notifyDataSetChanged();
    }
   // 删除好友更新页面
    public static void delete(NimUserInfo user){
         for(int i=0;i<data_haoyou.size()-1;i++){
              if(user.getAccount().equalsIgnoreCase(data_haoyou.get(i).getAccount())){
                  data_haoyou.remove(i);
                  Log.i("tmd","删除好友成功 ") ;
                  break;
              }
          }
        adapter_haoyou.notifyDataSetChanged();
    }
}
