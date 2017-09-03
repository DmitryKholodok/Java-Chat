package com.kholodok.message;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

public abstract class Message implements Serializable {

    private final String userName;
    private final String userIp;
    private final String msg;
    private final UserStatus userStatus;
    private final Date dateOfCreationOfTheMsg;

    public Message(String userName, String userIp, String msg, UserStatus userStatus) {
        this.userName = userName;
        this.userIp = userIp;
        this.msg = msg;
        this.userStatus = userStatus;
        this.dateOfCreationOfTheMsg = new Date(System.currentTimeMillis());
    }

    public String getUserName() { return userName; }
    public String getUserIp() { return userIp; }
    public UserStatus getUserStatus() { return userStatus; }
    public Date getDateOfCreationOfTheMsg() { return dateOfCreationOfTheMsg; }
    public String getMsg() { return msg; }

    @Override
    public String toString() {
        return "Message{" +
                "userName='" + userName + '\'' +
                ", userIp='" + userIp + '\'' +
                ", msg='" + msg + '\'' +
                ", userStatus=" + userStatus +
                ", dateOfCreationOfTheMsg=" + DateFormat.getDateInstance(DateFormat.FULL)
                        .format(dateOfCreationOfTheMsg) + '}';
    }
}
