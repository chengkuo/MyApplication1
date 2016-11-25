package com.example.a52374.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.avtivity.LoginActivity;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;

import java.util.List;

/**
 * Created by 吴广庆 on 2016/11/22.
 */

public class ChatRoomTalkAdapter extends BaseAdapter {
    List<ChatRoomMessage> messages;
    Context context;

    public ChatRoomTalkAdapter(List<ChatRoomMessage> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    public void setMessages(List<ChatRoomMessage> messages, Activity activity) {
        if(messages.get(0).getMsgType().getSendMessageTip()=="通知消息"){
            Toast.makeText(activity,"欢迎"+messages.get(0).getFromAccount()+"进入聊天室",Toast.LENGTH_SHORT).show();
        }else {
            this.messages.addAll(messages);
        }

    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HoldView hv;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_room_talk_text, parent, false);
            hv = new HoldView();
            hv.name= (TextView) convertView.findViewById(R.id.name);
            hv.nameTalk= (TextView) convertView.findViewById(R.id.name_talk_text);
            convertView.setTag(hv);
        }else {
            hv = (HoldView) convertView.getTag();
        }
        hv.name.setText(messages.get(position).getFromNick());
        hv.nameTalk.setText(messages.get(position).getContent());
        return convertView;
    }
    class HoldView{
        TextView name,nameTalk;
    }
}
