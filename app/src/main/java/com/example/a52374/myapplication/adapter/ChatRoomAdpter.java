package com.example.a52374.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a52374.myapplication.ChatRoomHttpClient;
import com.example.a52374.myapplication.R;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 吴广庆 on 2016/11/17.
 */

public class ChatRoomAdpter extends BaseAdapter {
    ChatRoomHttpClient chatRoomHttpClient = ChatRoomHttpClient.getInstance();
    List<ChatRoomInfo> list = chatRoomHttpClient.josnSyuri();
    Context context;
    ChatRoomInfo chatRoomInfo;
    private static final int[] imageRes = {R.drawable.room_cover_36, R.drawable.room_cover_37, R.drawable.room_cover_49,
            R.drawable.room_cover_50, R.drawable.room_cover_57, R.drawable.room_cover_58, R.drawable.room_cover_64,
            R.drawable.room_cover_72};
    public ChatRoomAdpter(List<ChatRoomInfo> list, Context context){
        this.list = list;
        this.context = context;
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
        final HoldView hv;
        if (convertView!=null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_chatroom, parent, false);
           hv = new HoldView();
            hv.chat_room_window = (ImageView) convertView.findViewById(R.id.chat_room_window);
            hv.nowchat_room= (TextView) convertView.findViewById(R.id.nowchat_room);
            hv.chat_room_name= (TextView) convertView.findViewById(R.id.chat_room_name);
            hv.user_count= (TextView) convertView.findViewById(R.id.user_count);
        }else {
            hv = (HoldView) convertView.getTag();
        }
        chatRoomInfo = list.get(position);
        hv.chat_room_window.setImageResource(imageRes[position]);
        hv.nowchat_room.setText(chatRoomInfo.getName());
        Log.i("cmd",chatRoomInfo.getAnnouncement());
        return convertView;
    }

    class HoldView{
        ImageView chat_room_window;
        TextView nowchat_room,chat_room_name,user_count;
    }
}