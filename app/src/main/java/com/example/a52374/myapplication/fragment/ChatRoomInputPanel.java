package com.example.a52374.myapplication.fragment;

import android.view.View;


import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

import com.example.a52374.myapplication.cantent.Container;

/**
 * Created by zhoujianghua on 2016/6/8.
 */
public class ChatRoomInputPanel {
    Container container;
    public ChatRoomInputPanel(Container container, View view, boolean isTextAudioSwitchShow) {
        this.container = container;
    }

    public ChatRoomInputPanel(Container container, View view) {

    }


    protected IMMessage createTextMessage(String text) {
        return ChatRoomMessageBuilder.createChatRoomTextMessage(container.account, text);
    }
}
