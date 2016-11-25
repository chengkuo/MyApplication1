package com.example.a52374.myapplication.fragment.chatroom;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.adapter.ChatRoomPersonAdapter;
import com.example.a52374.myapplication.adapter.ChatRoomTalkAdapter;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 吴广庆 on 2016/11/22.
 */

public class FragmentPerson extends Fragment {
    private final static String EXTRA_ROOM_ID = "ROOM_ID";
    private String roomId;
    private long updateTime;
    private List<ChatRoomMember> memberList;
    ChatRoomPersonAdapter myAdapter;
    ListView lv;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_room_person,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initAdapter();
        initData();
    }

    private void initAdapter() {
        myAdapter = new ChatRoomPersonAdapter(memberList, getContext());
        lv.setAdapter(myAdapter);
    }

    private void initView(View view) {
        roomId = getArguments().getString(EXTRA_ROOM_ID);
        lv = (ListView) view.findViewById(R.id.person_list);
        memberList = new ArrayList<>();
        updateTime = 0;
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
                .fetchRoomMembers(roomId, MemberQueryType.GUEST, updateTime, 1000)
                .setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
                    @Override
                    public void onResult(int code, List<ChatRoomMember> result, Throwable exception) {
                        if (myAdapter!=null){
                            myAdapter.setMemberList(result);
                            myAdapter.notifyDataSetChanged();
                        }
                        Log.i("cmd", String.valueOf(code));
                    }
                });
    }
    class HoldView{
        TextView name,nameTalk;
    }
}
