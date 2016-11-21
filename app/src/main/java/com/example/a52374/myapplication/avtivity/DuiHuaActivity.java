package com.example.a52374.myapplication.avtivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.cheng.myyunxin.mybean.MsgBean;
import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.mybean.MsgBean;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

public class DuiHuaActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rv;
    private static ArrayList<MsgBean> users = new ArrayList<>();
    private Button mButSendMsg;
    private EditText et_msg;
    private MyAdapter adapter;
    private TextView tv_duixiang;
    private String account;
    private static String last_Account = "";
    private static String LAST_MSG = "";
    private ImageLoader loader;

    Observer<List<IMMessage>> incomingMessageObserver =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                    Log.i("tmd", "onEvent: 收到消息");
                    String account_from = messages.get(messages.size() - 1).getFromAccount();

                    StringBuffer sb = new StringBuffer();
                    for (IMMessage meseg : messages) {
                        sb.append(meseg.getContent());
                    }
                    Log.i("tmd", "onEvent: 收到消息" + sb.toString());
                    if (account_from.equals(account)) {
                        users.add(new MsgBean(account_from, sb.toString()));
                        adapter.notifyDataSetChanged();
                        LAST_MSG = sb.toString();
                    }

                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dui_hua);
        Log.i("tmd", " 取出account ：onCreate: ");
        initView();
        account = getIntent().getStringExtra("account");
        //初始化Loader
        loader = ImageLoader.getInstance();
        //配置loader
        loader.init(ImageLoaderConfiguration.createDefault(this));
        Log.i("tmd", "onCreate: "+account);
        tv_duixiang.setText(account);
        //设置与当前聊天对象的消息不在提醒
//        MsgChange();
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
        List<IMMessage> msglist = (List<IMMessage>) getIntent().getSerializableExtra(account);
            Log.i("tmd", "@@@@@@@@@@@@@@@@@@@@@@@@@@onCreate: ");
        if (msglist != null){
            for (int i = 0; i < msglist.size(); i++) {
                String msg = msglist.get(i).getContent();
                MsgBean mm = new MsgBean(account,msg);
                users.add(mm);
                Log.i("tmd", "werwerewrewrerwonCreate:  dangqian 数据源大小为："+users.size());
            }

        }
        initAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        last_Account = account;

    }

    private void initAdapter() {
        if (!last_Account.equals(account)) {
            users.clear();
        }
        adapter = new MyAdapter();
        rv.setAdapter(adapter);
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rlv);
        mButSendMsg = (Button) findViewById(R.id.but_sendMsg);
        mButSendMsg.setOnClickListener(this);
        et_msg = (EditText) findViewById(R.id.et_msg);
        tv_duixiang = (TextView) findViewById(R.id.tv_duixiang);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_sendMsg:
                if (!et_msg.getText().toString().equals("")) {
//                    String account = getIntent().getStringExtra("account");
                    String msg = et_msg.getText().toString();
                    users.add(new MsgBean("", msg));
                    sendMsg(account, msg);
                    Log.i("tmd", "onClick: " + account);

                    et_msg.setText("");
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DuiHuaActivity.this, "聊天内容不能为空！！！", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        DisplayImageOptions options;
        public MyAdapter(){
            Log.i("tmd", "MyAdapter:  chushihua options");
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .displayer(new CircleBitmapDisplayer())
                    .build();
        }

        @Override
        public int getItemViewType(int position) {
            String str = users.get(position).getAccount();
            if (str.equals("")) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = null;
            switch (viewType) {
                case 0:
                    holder = new MyHolder_to(LayoutInflater.from(DuiHuaActivity.this).inflate(R.layout.msg_item_to, parent, false));
                    break;
                case 1:
                    holder = new MyHolder_from(LayoutInflater.from(DuiHuaActivity.this).inflate(R.layout.msg_item_from, parent, false));
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MsgBean mb = users.get(position);
            switch (getItemViewType(position)) {
                case 0:
                    MyHolder_to holder_to = (MyHolder_to) holder;
                    loader.displayImage(ImageDownloader.Scheme.DRAWABLE.wrap("R.drawable.avatar_def"),holder_to.iv,options);
//                    holder_to.iv.setImageResource(R.mipmap.ic_launcher);
                    holder_to.tv.setText(mb.getMsg());
                    break;
                case 1:
                    MyHolder_from holder_from = (MyHolder_from) holder;
                    loader.displayImage(ImageDownloader.Scheme.DRAWABLE.wrap("R.mipmap.avatar_def"),holder_from.iv,options);
//                    holder_from.iv.setImageResource(R.mipmap.ic_launcher);
                    holder_from.tv.setText(mb.getMsg());
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class MyHolder_from extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView tv;

            public MyHolder_from(View itemView) {
                super(itemView);
                iv = (ImageView) itemView.findViewById(R.id.iv_tx_item);
                tv = (TextView) itemView.findViewById(R.id.tv_msg_item);
            }
        }

        class MyHolder_to extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView tv;

            public MyHolder_to(View itemView) {
                super(itemView);
                iv = (ImageView) itemView.findViewById(R.id.iv_tx_to);
                tv = (TextView) itemView.findViewById(R.id.tv_msg_to);
            }
        }
    }

    public void sendMsg(String sendID, String msg) {

        IMMessage message = MessageBuilder.createTextMessage(
                sendID,
                SessionTypeEnum.P2P,
                msg
        );
        // 创建文本消息
//        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(
//                sendID, // 聊天室id
//                msg // 文本内容
//        );

// 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DuiHuaActivity.this, "消息发送成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int i) {
                Toast.makeText(DuiHuaActivity.this, "发送失败！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }

    public void changeSystemMsg() {

        NIMClient.getService(SystemMessageObserver.class)
                .observeReceiveSystemMsg(new Observer<SystemMessage>() {
                    @Override
                    public void onEvent(SystemMessage message) {
//                    NimUserInfo
                    }
                }, true);

    }

    //设置消息接收时提醒功能
    public void MsgChange() {
        NIMClient.getService(FriendService.class).setMessageNotify(account, false).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("tmd", "1111111onSuccess: ");
            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }
    //查询消息记录
    public void queryOldMsg(){
        if (LAST_MSG.equals("")){
            InvocationFuture<List<IMMessage>> oldList =  NIMClient.getService(MsgService.class).queryMessageListEx(MessageBuilder.createEmptyMessage(LAST_MSG,SessionTypeEnum.P2P,1000*60*60*24), QueryDirectionEnum.QUERY_OLD, 20, true);
            oldList.setCallback(new RequestCallback<List<IMMessage>>() {
                @Override
                public void onSuccess(List<IMMessage> imMessages) {
                    for (int i = 0; i < imMessages.size(); i++) {
                        Log.i("tmd", "onSuccess: 查询结果的大小为："+imMessages.size());
                    }
                }

                @Override
                public void onFailed(int i) {

                }

                @Override
                public void onException(Throwable throwable) {

                }
            });
        }

    }

}
