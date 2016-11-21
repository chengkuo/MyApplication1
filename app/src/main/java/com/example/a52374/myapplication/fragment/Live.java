package com.example.a52374.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.adapter.ChatRoomAdpter;
import com.example.a52374.myapplication.mybean.ChatRoomHttpClient;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 52374 on 2016/11/15.
 */
public class Live extends Fragment {
    GridView gridView;

    List<ChatRoomInfo> items = new ArrayList<ChatRoomInfo>();
    ChatRoomAdpter myAdpter;


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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chat_room, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        ChatRoomHttpClient.getInstance().fetchChatRoomList(new ChatRoomHttpClient.ChatRoomHttpCallback<List<ChatRoomInfo>>() {
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
                    Toast.makeText(getActivity(), "fetch chat room list failed, code=" + code, Toast.LENGTH_SHORT);
                }

                Log.d("cmd", "fetch chat room list failed, code:" + code
                        + " errorMsg:" + errorMsg);
            }
        });

        initAdpter(items, getContext());

    }

    private void initAdpter(List<ChatRoomInfo> items, Context context) {
        myAdpter = new ChatRoomAdpter(items, context);

        gridView.setAdapter(myAdpter);
    }


    private void initView(View view) {
        gridView = (GridView) view.findViewById(R.id.chat_room_gridview);

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
/*
//将map转换成josn
class ExtensionHelper{
    public static Map<String, Object> getMapFromJsonString(final String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }

        try {
            org.json.JSONObject json = new org.json.JSONObject(jsonStr);
            return recursiveParseJsonObject(json);
        } catch (org.json.JSONException e) {
            Log.e("tmd", "getMapFromJsonString exception =" + e.getMessage());
        }

        return null;
    }

    private static Map<String, Object> recursiveParseJsonObject(org.json.JSONObject json) throws org.json.JSONException {
        if (json == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>(json.length());
        String key;
        Object value;
        Iterator<String> i = json.keys();
        while (i.hasNext()) {
            key = i.next();
            value = json.get(key);
            if (value instanceof org.json.JSONArray) {
                map.put(key, recursiveParseJsonArray((org.json.JSONArray) value));
            } else if (value instanceof org.json.JSONObject) {
                map.put(key, recursiveParseJsonObject((org.json.JSONObject) value));
            } else {
                map.put(key, value);
            }
        }

        return map;
    }


    private static List recursiveParseJsonArray(org.json.JSONArray array) throws org.json.JSONException {
        if (array == null) {
            return null;
        }

        List list = new ArrayList(array.length());
        Object value;
        for (int m = 0; m < array.length(); m++) {
            value = array.get(m);
            if (value instanceof org.json.JSONArray) {
                list.add(recursiveParseJsonArray((org.json.JSONArray) value));
            } else if (value instanceof org.json.JSONObject) {
                list.add(recursiveParseJsonObject((org.json.JSONObject) value));
            } else {
                list.add(value);
            }
        }

        return list;
    }

}*/