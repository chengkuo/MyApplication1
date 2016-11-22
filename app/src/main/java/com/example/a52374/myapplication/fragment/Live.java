package com.example.a52374.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.adapter.ChatRoomsAdapter;
import com.example.a52374.myapplication.avtivity.ChatRoomActivity;
import com.example.a52374.myapplication.mybean.ChatRoomHttpClient;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 52374 on 2016/11/15.
 */
public class Live extends Fragment {
    GridView gridView;

    List<ChatRoomInfo> items = new ArrayList<ChatRoomInfo>();
    ChatRoomsAdapter myAdpter;


    // code
    private static final int RESULT_CODE_SUCCESS = 200;

    // api
    private static final String API_NAME_CHAT_ROOM_LIST = "homeList";

    // header
    private static final String HEADER_KEY_APP_KEY = "appkey";

    // result
    private static final String RESULT_KEY_RES = "res";
    private static final String RESULT_KEY_MSG = "msg";
    private static final String RESULT_KEY_TOTAL = "total";
    private static final String RESULT_KEY_LIST = "list";
    private static final String RESULT_KEY_NAME = "name";
    private static final String RESULT_KEY_CREATOR = "creator";
    private static final String RESULT_KEY_STATUS = "status";
    private static final String RESULT_KEY_ANNOUNCEMENT = "announcement";
    private static final String RESULT_KEY_EXT = "ext";
    private static final String RESULT_KEY_ROOM_ID = "roomid";
    private static final String RESULT_KEY_BROADCAST_URL = "broadcasturl";
    private static final String RESULT_KEY_ONLINE_USER_COUNT = "onlineusercount";

    private final static String EXTRA_ROOM_ID = "ROOM_ID";
    private final static String IMAGE_VIEW = "IMAGE_VIEW";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_rooms_gridview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initAdpter(items, getContext());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(),ChatRoomActivity.class);
                intent.putExtra(EXTRA_ROOM_ID,items.get(position).getRoomId());
                intent.putExtra(IMAGE_VIEW,position);
                startActivity(intent);
            }
        });


    }

    private void initAdpter(List<ChatRoomInfo> items, Context context) {
        myAdpter = new ChatRoomsAdapter(items, context);
        gridView.setAdapter(myAdpter);
    }


    private void initView(View view) {
        gridView = (GridView) view.findViewById(R.id.chat_room_gridview);

        //获取聊天室
        ChatRoomHttpClient.getInstance().fetchChatRoomList(
                new ChatRoomHttpClient.ChatRoomHttpCallback<List<ChatRoomInfo>>() {
            @Override
            public void onSuccess(List<ChatRoomInfo> rooms) {
                if (items.isEmpty()) {
                    items.addAll(rooms);
                }

                onFetchDataDone(true);
            }

            @Override
            public void onFailed(int code, String errorMsg) {
                onFetchDataDone(false);
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "fetch chat room list failed, code="
                            + code, Toast.LENGTH_SHORT);
                }

                Log.d("cmd", "fetch chat room list failed, code:" + code
                        + " errorMsg:" + errorMsg);
            }
        });
    }
    private void onFetchDataDone(boolean success) {


        if (success) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myAdpter.notifyDataSetChanged();
                }
            });
        }
    }

}