package com.kholodok.message;

public class ClientMessage extends Message {

    public ClientMessage(String userName, String userIp, String msg, UserStatus userStatus) {
        super(userName, userIp, msg, userStatus);
    }

}
