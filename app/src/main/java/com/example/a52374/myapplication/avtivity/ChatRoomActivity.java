package com.example.a52374.myapplication.avtivity;

import android.database.DataSetObservable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.adapter.ChatRoomPagerAdapter;
import com.example.a52374.myapplication.fragment.chatroom.FragmentAnchor;
import com.example.a52374.myapplication.fragment.chatroom.FragmentPerson;
import com.example.a52374.myapplication.fragment.chatroom.FragmentTalk;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private final static String EXTRA_ROOM_ID = "ROOM_ID";
    private final static String IMAGE_VIEW = "IMAGE_VIEW";
    private static final int[] imageRes = {R.drawable.room_cover_36, R.drawable.room_cover_37, R.drawable.room_cover_49,
            R.drawable.room_cover_50, R.drawable.room_cover_57, R.drawable.room_cover_58, R.drawable.room_cover_64,
            R.drawable.room_cover_72};

    private ImageView iv, back;
    private TabLayout chatRoomTab;
    private ViewPager chatRoomBody;
    private FragmentManager manager;
    private ChatRoomPagerAdapter pagerAdapter;
    public static FragmentTalk fragmentTalk;
    public static FragmentAnchor fragmentAnchor;
    public static FragmentPerson fragmentPerson;

    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private AbortableFuture<EnterChatRoomResultData> enterRequest;

    /**
     * 聊天室基本信息
     */
    private String roomId;
    private ChatRoomInfo roomInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        int i = getIntent().getIntExtra(IMAGE_VIEW, 0);
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
        endTalkText(incomingChatRoomMsg, true);
    }

    private void endTalkText(Observer<List<ChatRoomMessage>> incomingChatRoomMsg, boolean register) {
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
        manager = getSupportFragmentManager();
    }

    private void initData() {

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ROOM_ID, roomId);
        fragmentTalk = new FragmentTalk();
        fragmentAnchor = new FragmentAnchor();
        fragmentPerson = new FragmentPerson();
        fragmentTalk.setArguments(bundle);
        fragmentAnchor.setArguments(bundle);
        fragmentPerson.setArguments(bundle);
        fragmentList.add(fragmentTalk);
        fragmentList.add(fragmentAnchor);
        fragmentList.add(fragmentPerson);
    }

    private void initAdapter() {
        pagerAdapter = new ChatRoomPagerAdapter(manager, fragmentList);
        chatRoomBody.setAdapter(pagerAdapter);
        chatRoomTab.setupWithViewPager(chatRoomBody);
    }


    /**
     * 进入聊天室
     */
    void initRoom() {
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
                if (i == 404) {
                    Toast.makeText(ChatRoomActivity.this, "聊天室不存在", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });

//
//        /**
//         * 获取聊天室成员信息
//         *
//         * @param roomId          聊天室id
//         * @param memberQueryType 成员查询类型。见{@link MemberQueryType}
//         * @param time            查询固定成员列表用ChatRoomMember.getUpdateTime,
//         *                        查询游客列表用ChatRoomMember.getEnterTime，
//         *                        填0会使用当前服务器最新时间开始查询，即第一页，单位毫秒
//         * @param limit           条数限制
//         * @return InvocationFuture 可以设置回调函数。回调中返回成员信息列表
//         */
//        NIMClient.getService(ChatRoomService.class)
//                .fetchRoomMembers(roomId, MemberQueryType.NORMAL, updateTime, 10)
//                .setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
//                    @Override
//                    public void onResult(int code, List<ChatRoomMember> result, Throwable exception) {
//                        for (ChatRoomMember member : result) {
//                            updateTime = member.getUpdateTime();
//                        }
//                        Log.i("cmd",String.valueOf(code));
//                    }
//                });
    }
}
