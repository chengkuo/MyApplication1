package com.example.a52374.myapplication.avtivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

public class DuiHuaActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rv;
    private ArrayList<MsgBean> msglist = new ArrayList<>();
    private Button mButSendMsg;
    private EditText et_msg;
    private MyAdapter adapter;
    private String account;
    private Observer<List<IMMessage>> incomingMessageObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dui_hua);
        account = getIntent().getStringExtra("account");
        Log.i("tmd", " 取出account ：onCreate: "+account);
        initView();
        rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);

        initData();

        initAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
    }

    private void initAdapter() {
        adapter = new MyAdapter();
        rv.setAdapter(adapter);
    }

    private void initData() {
     incomingMessageObserver =
                new Observer<List<IMMessage>>() {
                    @Override
                    public void onEvent(List<IMMessage> messages) {
                        // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                        String account = messages.get(0).getFromAccount();
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < messages.size(); i++) {
                            sb.append(messages.get(i).getContent());
                        }
                        msglist.add(new MsgBean(account,sb.toString()));
                        adapter.notifyDataSetChanged();
                    }
                };
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rlv);
        mButSendMsg = (Button) findViewById(R.id.but_sendMsg);
        mButSendMsg.setOnClickListener(this);
        et_msg = (EditText) findViewById(R.id.et_msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_sendMsg:
                if (!et_msg.getText().toString().equals("")){

                    msglist.add(new MsgBean("",et_msg.getText().toString()));
                    sendMsg(account,et_msg.getText().toString());
                    Log.i("tmd", "onClick: "+account);
                    et_msg.setText("");
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(DuiHuaActivity.this,"聊天内容不能为空！！！",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public int getItemViewType(int position) {
            String str = msglist.get(position).getAccount();
            if (str.equals("")){
                return 0;
            }else {
                return 1;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = null;
            switch (viewType){
                case 0:
                    holder = new MyHolder_to(LayoutInflater.from(DuiHuaActivity.this).inflate(R.layout.msg_item_to,parent,false));
                    break;
                case 1:
                    holder = new MyHolder_from(LayoutInflater.from(DuiHuaActivity.this).inflate(R.layout.msg_item_from,parent,false));
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                MsgBean mb = msglist.get(position);
            switch (getItemViewType(position)){
                case 0:
                    MyHolder_to holder_to = (MyHolder_to) holder;
                    holder_to.iv.setImageResource(R.mipmap.ic_launcher);
                    holder_to.tv.setText(mb.getMsg());
                    break;
                case 1:
                    MyHolder_from holder_from = (MyHolder_from) holder;
                    holder_from.iv.setImageResource(R.mipmap.ic_launcher);
                    holder_from.tv.setText(mb.getMsg());
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return msglist.size();
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

    public void sendMsg(String sendID,String msg){

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
        NIMClient.getService(MsgService.class).sendMessage(message,false).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DuiHuaActivity.this,"消息发送成功！",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int i) {
                Toast.makeText(DuiHuaActivity.this,"发送失败！",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }
}
