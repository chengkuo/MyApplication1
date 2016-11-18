package com.example.a52374.myapplication.avtivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.adapter.Mainadpter;
import com.example.a52374.myapplication.fragment.Live;
import com.example.a52374.myapplication.fragment.Session;
import com.example.a52374.myapplication.fragment.fragment_TXL;
import com.example.a52374.myapplication.mybean.MsgBean;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;


//主界面
public class MainActivity extends AppCompatActivity {
    private ViewPager vp;
    private TabLayout tab;
    private ArrayList<Fragment> list = new ArrayList<>();
    private Mainadpter mainadpter;
    private FragmentManager manager;
    private long last = 0;
    private fragment_TXL fragment1;//通讯界面

    //  private SystemMessageObserver sobserver;  //监听 好友验证 通知
    //  private SystemMessage message;           //接收 好友验证通知 的信息
    // private     AddFriendNotify addfn;        //好友通知对象

    Observer<List<IMMessage>> incomingMessageObserver  =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                    Log.i("tmd", "onEvent: 收到消息");
                    String act = "";
                    if (!act.equals(messages.get(messages.size()-1).getFromAccount())){
                        String account_from = messages.get(messages.size()-1).getFromAccount();
                        String msg = messages.get(messages.size()-1).getContent();
                        MsgBean mb = new MsgBean(account_from,msg);
                        ArrayList<MsgBean> mblist = new ArrayList<>();
                        mblist.add(mb);
                    }



                }
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        manager = getSupportFragmentManager();
        // inittab();
        initdata();
        initadapter();
        registerSystemObserver(true);
        statechange();
        TongBustate();
    }

    //监听用户状态
    public void statechange() {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
                new Observer<StatusCode>() {
                    public void onEvent(StatusCode status) {
                        Log.i("tmd", "用户当前状态  User status changed to: " + status);
                        if (status.toString().equals("NET_BROKEN")) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                        if (status.wontAutoLogin()) {
                            // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
                            Toast.makeText(MainActivity.this, "登录失败请重新登录", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                }, true);
    }

    public void TongBustate() {
        NIMClient.getService(AuthServiceObserver.class).observeLoginSyncDataStatus(new Observer<LoginSyncStatus>() {
            @Override
            public void onEvent(LoginSyncStatus status) {
                if (status == LoginSyncStatus.BEGIN_SYNC) {
//                    LogUtil.i(TAG, "login sync data begin");
                    Log.i("tmd", "onEvent: login sync data begin");
                } else if (status == LoginSyncStatus.SYNC_COMPLETED) {
//                    LogUtil.i(TAG, "login sync data completed");
                    Log.i("tmd", "onEvent: login sync data completed");
                }
            }
        }, true);
    }


    private void registerSystemObserver(boolean b) {

        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(systemMessageObserver, b);
    }

    private Observer<SystemMessage> systemMessageObserver = new Observer<SystemMessage>() {
        @Override
        public void onEvent(SystemMessage message) {
            if (message.getType() == SystemMessageType.AddFriend) {
                final AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
                if (attachData != null) {
                    // 针对不同的事件做处理
                    if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT) {
                        // 对方直接添加你为好友
                        Toast.makeText(MainActivity.this, attachData.getAccount() + "添加你为好友了", Toast.LENGTH_SHORT).show();
                        NIMClient.getService(FriendService.class).ackAddFriendRequest(attachData.getAccount(), true);// 通过对方的好友请求
                        fragment1.change(attachData.getAccount());

                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
                        // 对方通过了你的好友验证请求
                        Toast.makeText(MainActivity.this, attachData.getAccount() + "通过了你的加好友请求", Toast.LENGTH_SHORT).show();
                        fragment1.change(attachData.getAccount());

                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                        // 对方拒绝了你的好友验证请求
                        Toast.makeText(MainActivity.this, attachData.getAccount() + "拒绝了你的好友请求", Toast.LENGTH_SHORT).show();
                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                        // 对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。
                        // 通过message.getContent()获取好友验证请求的附言
                        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this)
                                .setTitle(attachData.getAccount() + "请求加你为好友")
                                .setMessage(message.getContent());

                        ab.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NIMClient.getService(FriendService.class).ackAddFriendRequest(attachData.getAccount(), true);// 通过对方的好友请求
                                Log.i("tmd", "111111111");
                                //NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(attachData.getAccount());

                                fragment1.change(attachData.getAccount());

                                dialog.dismiss();
                            }
                        });
                        ab.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NIMClient.getService(FriendService.class).ackAddFriendRequest(attachData.getAccount(), false);


                                dialog.dismiss();
                            }
                        });
                        ab.show();
                    }
                }
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemadd:
                startActivity(new Intent(MainActivity.this, AddfriendActivity.class));
                break;
            case R.id.itemback:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void initadapter() {
        mainadpter = new Mainadpter(manager, list);
        vp.setAdapter(mainadpter);
        tab.setupWithViewPager(vp);
    }

    private void initdata() {
        Session fragment = new Session();

        list.add(fragment);
        fragment1 = new fragment_TXL();
        list.add(fragment1);
        Live fragment2 = new Live();
        list.add(fragment2);

    }

    private void inittab() {
        tab.setTabMode(TabLayout.MODE_SCROLLABLE);
        tab.setTabGravity(TabLayout.GRAVITY_FILL);
        tab.addTab(tab.newTab().setText("会话").setTag(0));
        tab.addTab(tab.newTab().setText("通讯").setTag(1));
        tab.addTab(tab.newTab().setText("直播").setTag(2));
    }


    private void initview() {
        vp = (ViewPager) findViewById(R.id.mainvp);
        tab = (TabLayout) findViewById(R.id.maintb);
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        long during = currentTime - last;
        if (during < 2000) {
            finish();
        } else {
            Toast.makeText(MainActivity.this, "再按一次退出！", Toast.LENGTH_SHORT).show();
        }
        last = currentTime;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ArrayList<IMMessage> messages = (ArrayList<IMMessage>)
//                getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT); // 可以获取消息的发送者，跳转到指定的单聊、群聊界面。
//        Log.i("tmd", "onResume: "+messages.size());
//        // 进入聊天界面，建议放在onResume中
//        NIMClient.getService(MsgService.class).setChattingAccount(messages.get(0).getFromAccount(), SessionTypeEnum.P2P);

    }

}
