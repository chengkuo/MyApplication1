package com.example.a52374.myapplication.fragment.chatroom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a52374.myapplication.image.BitmapCup;
import com.example.a52374.myapplication.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


/**
 * Created by 吴广庆 on 2016/11/22.
 */

public class FragmentAnchor extends Fragment {
    private final static String EXTRA_ROOM_ID = "ROOM_ID";
    private String roomId;
    private long updateTime;
    private List<ChatRoomMember> memberList;
    private TextView anchorName;
    private TextView member;
    private ImageView anchorImage;
    public Bitmap bitMap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_room_anchor, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        roomId = getArguments().getString(EXTRA_ROOM_ID);
        updateTime = 0;
        anchorImage = (ImageView) view.findViewById(R.id.anchor_photo);
        anchorName = (TextView) view.findViewById(R.id.anchor_name);
        member = (TextView) view.findViewById(R.id.member);
        anchorImage.setImageBitmap(BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.avatar_def));
    }


    private void initData() {

        /**
         * 获取聊天室成员信息
         *
         * @param roomId          聊天室id
         * @param memberQueryType 成员查询类型。见{@link MemberQueryType}
         * @param time            查询固定成员列表用ChatRoomMember.getUpdateTime,
         *                        查询游客列表用ChatRoomMember.getEnterTime，
         *                        填0会使用当前服务器最新时间开始查询，即第一页，单位毫秒
         * @param limit           条数限制
         * @return InvocationFuture 可以设置回调函数。回调中返回成员信息列表
         */
        NIMClient.getService(ChatRoomService.class)
                .fetchRoomMembers(roomId, MemberQueryType.NORMAL, updateTime, 10)
                .setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
                    @Override
                    public void onResult(int code, List<ChatRoomMember> result, Throwable exception) {
                        memberList = result;
                        for (final ChatRoomMember member : result) {

                            updateTime = member.getUpdateTime();
                            if(member.getMemberType()== MemberType.CREATOR){

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            HttpURLConnection conn = (HttpURLConnection) new URL(member.getAvatar())
                                                    .openConnection();
                                            conn.connect();
                                            if (conn.getResponseCode()==200){
                                                InputStream is = conn.getInputStream();
                                                bitMap = BitmapFactory.decodeStream(is);
                                                anchorImage.setImageBitmap(BitmapCup.bitmap(bitMap));

                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                anchorName.setText(member.getNick());
                            }
                        }
                        member.setText(String.valueOf(result.size()));
                        Log.i("cmd", String.valueOf(code));
                    }
                });
    }
}


