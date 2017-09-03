package com.kholodok.message;

import java.io.Serializable;
import java.util.Date;

public class ServerMessage implements Serializable{

    private final ClientMessage clientMessage;
    private transient final Date DateOfReceivingByTheServer;
    private String serverMsg = "";

    public ServerMessage(ClientMessage clientMessage) {
        this.clientMessage = clientMessage;
        this.DateOfReceivingByTheServer = new Date(System.currentTimeMillis());
    }

    public String getMsg() { return serverMsg; }
    public ClientMessage getClientMessage() { return clientMessage; }
    public Date getDateOfReceivingByTheServer() { return DateOfReceivingByTheServer; }

    public void setMsg(String serverMsg) { this.serverMsg = serverMsg; }

    @Override
    public String toString() {
        if (clientMessage != null) return clientMessage.toString();
        return serverMsg;
    }
}
