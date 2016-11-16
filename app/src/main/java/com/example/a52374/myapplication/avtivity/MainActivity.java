package com.example.a52374.myapplication.avtivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.adapter.Mainadpter;
import com.example.a52374.myapplication.fragment.Live;
import com.example.a52374.myapplication.fragment.Session;
import com.example.a52374.myapplication.fragment.fragment_TXL;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ViewPager vp;
    private TabLayout tab;
    private ArrayList<Fragment> list=new ArrayList<>();
    private Mainadpter mainadpter;
    private FragmentManager manager;
  //  private SystemMessageObserver sobserver;  //监听 好友验证 通知
  //  private SystemMessage message;           //接收 好友验证通知 的信息
   // private     AddFriendNotify addfn;        //好友通知对象
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        manager=getSupportFragmentManager();
       // inittab();
        initdata();
        initadapter();
        registerSystemObserver(true);
    }

    private void registerSystemObserver(boolean b) {

        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(systemMessageObserver, b);
    }

   private   Observer<SystemMessage> systemMessageObserver=new Observer<SystemMessage>() {
       @Override
       public void onEvent(SystemMessage message) {
           if (message.getType() == SystemMessageType.AddFriend) {
               final AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
               if (attachData != null) {
                   // 针对不同的事件做处理
                   if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT) {
                       // 对方直接添加你为好友
                       Toast.makeText(MainActivity.this,"请求加好友",Toast.LENGTH_SHORT).show();
                   } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
                       // 对方通过了你的好友验证请求
                       Toast.makeText(MainActivity.this,attachData.getAccount()+"通过了你的加好友请求",Toast.LENGTH_SHORT).show();
                   } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                       // 对方拒绝了你的好友验证请求
                       Toast.makeText(MainActivity.this,attachData.getAccount()+"拒绝了你的好友请求",Toast.LENGTH_SHORT).show();
                   } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                       // 对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。
                       // 通过message.getContent()获取好友验证请求的附言
                       AlertDialog.Builder ab=new AlertDialog.Builder(MainActivity.this)
                               .setTitle(attachData.getAccount()+"请求加你为好友")
                               .setMessage(message.getContent());
                       ab.show();
                      ab.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               NIMClient.getService(FriendService.class).ackAddFriendRequest(attachData.getAccount(), true); // 通过对方的好友请求


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
                   }
               }
           }
       }
   };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()){
             case R.id.itemadd:
                 startActivity(new Intent(MainActivity.this,AddfriendActivity.class));
                 break;

         }

        return super.onOptionsItemSelected(item);
    }

    private void initadapter() {
        mainadpter=new Mainadpter(manager,list);
        vp.setAdapter(mainadpter);
        tab.setupWithViewPager(vp);
    }

    private void initdata() {
        Session  fragment=new Session();
        list.add(fragment);
        fragment_TXL fragment1=new fragment_TXL();
        list.add(fragment1);
        Live fragment2=new Live();
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
       vp= (ViewPager) findViewById(R.id.mainvp);
       tab= (TabLayout) findViewById(R.id.maintb);
    }



}
