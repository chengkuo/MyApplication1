package com.example.a52374.myapplication.avtivity;

import android.database.DataSetObservable;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.adapter.ChatRoomPagerAdapter;
import com.example.a52374.myapplication.cantent.Container;
import com.example.a52374.myapplication.fragment.chatroom.FragmentAnchor;
import com.example.a52374.myapplication.fragment.chatroom.FragmentPerson;
import com.example.a52374.myapplication.fragment.chatroom.FragmentTalk;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private static final int MESSAGE_CAPACITY = 500;
    private final static String EXTRA_ROOM_ID = "ROOM_ID";
    private final static String IMAGE_VIEW = "IMAGE_VIEW";
    private static final int[] imageRes = {R.drawable.room_cover_36, R.drawable.room_cover_37, R.drawable.room_cover_49,
            R.drawable.room_cover_50, R.drawable.room_cover_57, R.drawable.room_cover_58, R.drawable.room_cover_64,
            R.drawable.room_cover_72};

    private ImageView iv,back;
    private TabLayout chatRoomTab;
    private ViewPager chatRoomBody;
    private FragmentManager manager;
    private ChatRoomPagerAdapter pagerAdapter;
   static FragmentTalk fragmentTalk;
    String text;
//    List<ChatRoomMessage> messageList;
    private ArrayList<Fragment> fragmentList=new ArrayList<>();
    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    private static final String TAG = ChatRoomActivity.class.getSimpleName();
    private AbortableFuture<EnterChatRoomResultData> enterRequest;
    /**
     * 聊天室基本信息
     */
    private String roomId;
    private ChatRoomInfo roomInfo;

    //private LinkedList<IMMessage> items;

//    private ChatRoomFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        int i = getIntent().getIntExtra(IMAGE_VIEW,0);
        initRoom();
        initView();
        initData();
        iv.setImageResource(imageRes[i]);
        initAdapter();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initTalkText();
    }

   private void initTalkText() {
        /*new FragmentTalk.TextCallback(){

           @Override
           public void getText(String s) {
               text = s;
               // 创建文本消息
               ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(
                       roomId, // 聊天室id
                       text // 文本内容
               );

                // 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
               NIMClient.getService(ChatRoomService.class).sendMessage(message,true);
           }
       };*/


        Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
            @Override
            public void onEvent(List<ChatRoomMessage> messages) {
                // 处理新收到的消息
                if (messages == null || messages.isEmpty()) {
                    return;
                }
                fragmentTalk.talkText(messages);
            }
        };
        endTalkText(incomingChatRoomMsg,true);
    }

    private void endTalkText(Observer<List<ChatRoomMessage>> incomingChatRoomMsg,boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class)
                .observeReceiveMessage(incomingChatRoomMsg, register);
    }


    /**
     * 初始化控件
     */
    private void initView() {
        iv = (ImageView) findViewById(R.id.chat_room_wall);
        back = (ImageView) findViewById(R.id.room_back);
        chatRoomTab = (TabLayout) findViewById(R.id.chat_room_tab);
        chatRoomBody = (ViewPager) findViewById(R.id.chat_room_body);
        manager=getSupportFragmentManager();
    }

    private void initData() {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ROOM_ID,roomId);
        fragmentTalk = new FragmentTalk();
        fragmentTalk.setArguments(bundle);
        fragmentList.add(fragmentTalk);
        fragmentList.add(new FragmentAnchor());
        fragmentList.add(new FragmentPerson());
    }

    private void initAdapter() {
        pagerAdapter = new ChatRoomPagerAdapter(manager,fragmentList);
        chatRoomBody.setAdapter(pagerAdapter);
        chatRoomTab.setupWithViewPager(chatRoomBody);
    }


    /**
     * 进入聊天室
     */
    void initRoom(){
        EnterChatRoomData data = new EnterChatRoomData(roomId);
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {
                    @Override
                    public void onSuccess(EnterChatRoomResultData enterChatRoomResultData) {
                        roomInfo = enterChatRoomResultData.getRoomInfo();
                        ChatRoomMember member = enterChatRoomResultData.getMember();
                        member.setRoomId(roomInfo.getRoomId());
                        Toast.makeText(ChatRoomActivity.this, "欢迎进入聊天室", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int i) {
                        int errorCode = NIMClient.getService(ChatRoomService.class).getEnterErrorCode(roomId);
                        if(i==404){
                            Toast.makeText(ChatRoomActivity.this, "聊天室不存在", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                });
    }


   private void initTalk(){



    }
}
