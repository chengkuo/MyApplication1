package com.example.a52374.myapplication.mybean;

import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

/**
 * Created by a452542253 on 2016/11/16.
 */

public class MsgBean implements UserInfoProvider.UserInfo {
    private String account;
    private String Msg;

    public String getAccount() {
        return account;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getAvatar() {
        return null;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public MsgBean(String account, String msg) {
        this.account = account;
        Msg = msg;
    }
}
