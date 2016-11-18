package com.example.a52374.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.adapter.ChatRoomAdpter;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 52374 on 2016/11/15.
 */
public class Live extends Fragment {
    GridView gridView;
    List<ChatRoomInfo> items = new ArrayList<ChatRoomInfo>();
    ChatRoomAdpter myAdpter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chat_room, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initAdpter(items,getContext());

    }

    private void initAdpter(List<ChatRoomInfo> items, Context context) {
        myAdpter = new ChatRoomAdpter(items,context);
        gridView.setAdapter(myAdpter);
    }


    private void initView(View view) {
        gridView  = (GridView) view.findViewById(R.id.chat_room_gridview);
    }

}
