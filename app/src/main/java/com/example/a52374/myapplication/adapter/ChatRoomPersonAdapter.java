package com.example.a52374.myapplication.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.image.BitmapCup;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by 吴广庆 on 2016/11/25.
 */

public class ChatRoomPersonAdapter extends BaseAdapter {
    private List<ChatRoomMember> memberList;
    private Context context;
    public ChatRoomPersonAdapter(List<ChatRoomMember> memberList, Context context) {
        this.memberList = memberList;
        this.context = context;
    }

    public void setMemberList(List<ChatRoomMember> memberList) {
        this.memberList.addAll(memberList);
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final HoldView hv;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_room_person, parent, false);
            hv =new HoldView();
            hv.personPhoto= (ImageView) convertView.findViewById(R.id.person_photo);
            hv.personName= (TextView) convertView.findViewById(R.id.name_talk_text);
            convertView.setTag(hv);
        }else {
            hv = (HoldView) convertView.getTag();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(memberList.get(position).getAvatar())
                            .openConnection();
                    conn.connect();
                    if (conn.getResponseCode()==200){
                        InputStream is = conn.getInputStream();

                        hv.personPhoto.setImageBitmap(BitmapCup.bitmap(BitmapFactory.decodeStream(is)));

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        hv.personName.setText(memberList.get(position).getNick());
        return convertView;
    }class HoldView{
        ImageView personPhoto;
        TextView personName;
    }
}
