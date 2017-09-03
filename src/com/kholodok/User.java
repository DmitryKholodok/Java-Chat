package com.kholodok;

import com.kholodok.message.UserStatus;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class User {

    private final String name;
    private final String userIp;
    private UserStatus userStatus = UserStatus.NOTHING;

    public User(String name) throws UnknownHostException {
        this.name = name;
        this.userIp = InetAddress.getLocalHost().getHostAddress();
    }

    public String getName() {
        return name;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public String getUserIp() {
        return userIp;
    }
}
