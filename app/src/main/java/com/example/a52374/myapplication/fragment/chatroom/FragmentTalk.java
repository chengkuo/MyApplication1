package com.example.a52374.myapplication.fragment.chatroom;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.adapter.ChatRoomTalkAdapter;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessageExtension;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MemberPushOption;
import com.netease.nimlib.sdk.msg.model.NIMAntiSpamOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 吴广庆 on 2016/11/22.
 */

public class FragmentTalk extends Fragment {
    List<ChatRoomMessage> messageList;
    ListView listView;
    TextView talkText;
    Button save;
//    TextCallback callback;
    ChatRoomTalkAdapter adapter;
    private String roomId;
    ChatRoomMessage message;
    private final static String EXTRA_ROOM_ID = "ROOM_ID";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_room_talk, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initAdapter();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (talkText.getText() != null) {
                    // 创建文本消息
                    ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(
                            roomId, // 聊天室id
                            talkText.getText().toString() // 文本内容
                    );
//                    Toast.makeText(getContext(),"发送成功",Toast.LENGTH_SHORT).show();
                    // 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
                    NIMClient.getService(ChatRoomService.class).sendMessage(message, true);
                    messageList = new ArrayList<ChatRoomMessage>();
                    messageList.add(message);
                    adapter.setMessages(messageList,getActivity());
                    adapter.notifyDataSetChanged();
                    talkText.setText("");
                }
            }
        });
    }

    private void initAdapter() {
        adapter = new ChatRoomTalkAdapter(messageList, getContext());
        listView.setAdapter(adapter);
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.talk_list);
        talkText = (TextView) view.findViewById(R.id.et_msg);
        save = (Button) view.findViewById(R.id.but_sendMsg);
        roomId = getArguments().getString(EXTRA_ROOM_ID);
        messageList = new ArrayList<ChatRoomMessage>();
    }

    public void talkText(List<ChatRoomMessage> messages) {
        messageList = messages;
        if (adapter != null) {
            adapter.setMessages(messageList, getActivity());
            adapter.notifyDataSetChanged();
        }
    }
}
