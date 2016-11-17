package com.example.a52374.myapplication.avtivity;

import android.database.DataSetObservable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.cantent.Container;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private static final int MESSAGE_CAPACITY = 500;
    private final static String EXTRA_ROOM_ID = "ROOM_ID";
    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    private static final String TAG = ChatRoomActivity.class.getSimpleName();
    /**
     * 聊天室基本信息
     */
    private String roomId;
    private ChatRoomInfo roomInfo;

    private LinkedList<IMMessage> items;

//    private ChatRoomFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }

    Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }

            boolean needRefresh = false;
            List<ChatRoomMessage> addedListItems = new ArrayList<>(messages.size());

            for(ChatRoomMessage message : messages){
                if(isMyMessage(message)){
                    saveMessage(message,false);
                    addedListItems.add(message);
                    needRefresh = true;
                }
                if (needRefresh) {
                    mDataSetObservable.notifyChanged();
                }

                message.toString();
            }


    }
        public void saveMessage(final IMMessage message, boolean addFirst) {
            if (message == null) {
                return;
            }

            if (items.size() >= MESSAGE_CAPACITY) {
                items.poll();
            }

            if (addFirst) {
                items.add(0, message);
            } else {
                items.add(message);
            }
        }
};


    public boolean isMyMessage(ChatRoomMessage message) {
        return message.getSessionType() == SessionTypeEnum.ChatRoom
                && message.getSessionId() != null
                && message.getSessionId().equals(roomId);
    }
}
