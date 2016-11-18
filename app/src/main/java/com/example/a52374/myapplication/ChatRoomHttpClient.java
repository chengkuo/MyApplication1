package com.example.a52374.myapplication;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * 网易云信Demo聊天室Http客户端。第三方开发者请连接自己的应用服务器。
 * <p/>
 * Created by huangjun on 2016/1/18.
 */
public class ChatRoomHttpClient {
    private static final String TAG = ChatRoomHttpClient.class.getSimpleName();

    // code
    private static final int RESULT_CODE_SUCCESS = 200;

    // api
    private static final String API_NAME_CHAT_ROOM_LIST = "https://app.netease.im/api/chatroom/homeList";

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
    private HttpClient client;
    private ClientConnectionManager connManager;
    String response;

    // 最大连接数
    public final static int MAX_CONNECTIONS = 10;

    // 获取连接的最大等待时间
    public final static int WAIT_TIMEOUT = 5 * 1000;

    // 每个路由最大连接数
    public final static int MAX_ROUTE_CONNECTIONS = 10;

    // 连接超时时间
    public final static int CONNECT_TIMEOUT = 5 * 1000;

    // 读取超时时间
    public final static int READ_TIMEOUT = 10 * 1000;


    /*public interface ChatRoomHttpCallback<T> {
        void onSuccess(T t);

        void onFailed(int code, String errorMsg);
    }*/

    private static ChatRoomHttpClient instance;

    public static synchronized ChatRoomHttpClient getInstance() {
        if (instance == null) {
            instance = new ChatRoomHttpClient();
        }


        return instance;
    }

    public ChatRoomHttpClient() {
        HttpParams httpParams = new BasicHttpParams();
        // 设置最大连接数
        ConnManagerParams.setMaxTotalConnections(httpParams, MAX_CONNECTIONS);
        // 设置获取连接的最大等待时间
        ConnManagerParams.setTimeout(httpParams, WAIT_TIMEOUT);
        // 设置每个路由最大连接数
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(MAX_ROUTE_CONNECTIONS));
        // 设置连接超时时间
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECT_TIMEOUT);
        // 设置读取超时时间
        HttpConnectionParams.setSoTimeout(httpParams, READ_TIMEOUT);

        SchemeRegistry registry = new SchemeRegistry();
        connManager = new ThreadSafeClientConnManager(httpParams, registry);
        client = new DefaultHttpClient(connManager, httpParams);
        String url = API_NAME_CHAT_ROOM_LIST;

        Map<String, String> headers = new HashMap<>(1);
        String appKey = "d3fd56ecfb24e55343517474262c459b";
        headers.put(HEADER_KEY_APP_KEY, appKey);

        response = get(url, headers);

    }

//    /**
//     * 向网易云信Demo应用服务器请求聊天室列表
//     */
//    public void fetchChatRoomList(final ChatRoomHttpCallback<List<ChatRoomInfo>> callback) {
//        String url = API_NAME_CHAT_ROOM_LIST;
//
//        Map<String, String> headers = new HashMap<>(1);
//        String appKey = "d3fd56ecfb24e55343517474262c459b";
//        headers.put(HEADER_KEY_APP_KEY, appKey);
//
//        response = get(url, headers);
//
//
//    }
    private String get(String url, Map<String, String> headers) {
        HttpResponse response;
        HttpGet request;
        try {
            request = new HttpGet(url);

            // add request headers
            request.addHeader("charset", "utf-8");
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    request.addHeader(header.getKey(), header.getValue());
                }
            }

            // execute
            response = client.execute(request);

            // response
            StatusLine statusLine = response.getStatusLine();
            if (statusLine == null) {
                Log.e(TAG, "StatusLine is null");
            }
            int statusCode = statusLine.getStatusCode();
            if (statusCode < 200 || statusCode > 299) {
            }
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            Log.e(TAG, "Get data error", e);
            if (e instanceof UnknownHostException) {
            } else if(e instanceof SSLPeerUnverifiedException) {
            }
            return "408";
        }
    }

    //chatRoomjosn获取
    public List<ChatRoomInfo> josnSyuri(){
        try {
            // ret 0
            JSONObject res = JSONObject.parseObject(response);
            // res 1
            int resCode = res.getIntValue(RESULT_KEY_RES);
            if (resCode == RESULT_CODE_SUCCESS) {
                // msg 1
                JSONObject msg = res.getJSONObject(RESULT_KEY_MSG);
                List<ChatRoomInfo> roomInfoList = null;
                if (msg != null) {
                    // total 2
                    roomInfoList = new ArrayList<>(msg.getIntValue(RESULT_KEY_TOTAL));

                    // list 2
                    JSONArray rooms = msg.getJSONArray(RESULT_KEY_LIST);
                    for (int i = 0; i < rooms.size(); i++) {
                        // room 3
                        JSONObject room = rooms.getJSONObject(i);
                        ChatRoomInfo roomInfo = new ChatRoomInfo();
                        roomInfo.setName(room.getString(RESULT_KEY_NAME));
                        roomInfo.setCreator(room.getString(RESULT_KEY_CREATOR));
                        roomInfo.setValidFlag(room.getIntValue(RESULT_KEY_STATUS));
                        roomInfo.setAnnouncement(room.getString(RESULT_KEY_ANNOUNCEMENT));
                        roomInfo.setExtension(ExtensionHelper.getMapFromJsonString(room.getString(RESULT_KEY_EXT)));
                        roomInfo.setRoomId(room.getString(RESULT_KEY_ROOM_ID));
                        roomInfo.setBroadcastUrl(room.getString(RESULT_KEY_BROADCAST_URL));
                        roomInfo.setOnlineUserCount(room.getIntValue(RESULT_KEY_ONLINE_USER_COUNT));
                        roomInfoList.add(roomInfo);
                    }
                }
                Log.i(TAG,roomInfoList.toString());
                return roomInfoList;
            }
        } catch (JSONException e) {
           Log.e(TAG,e.getMessage());
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
        return new ArrayList<>();
    }
   public interface NimHttpCallback {
        void onResponse(String response, int code, String errorMsg);
    }
}

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
}
