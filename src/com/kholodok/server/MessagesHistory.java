package com.kholodok.server;

import java.util.*;

public class MessagesHistory {

    private static final byte limitCount = Byte.MAX_VALUE;

    private int count;
    private List<String> messageList;

    public MessagesHistory(int count) {
        this.count = count;
        messageList = Collections.synchronizedList(
                new ArrayList<String>());
    }

    private byte getMsgCount() {
        return (byte)messageList.size();

    }

    public void addMsgToList(String msg) {
        synchronized (messageList) {
            if (messageList.size() == limitCount)
                refreshList();
            messageList.add(msg);
        }
    }

    //synchronized
    private void refreshList() {
        List<String> tempList = new ArrayList<String>(
                messageList.subList(limitCount - count + 1, limitCount));
        messageList.clear();
        messageList.addAll(tempList);
    }

    public List<String> getMsgList() {
        synchronized (messageList) {
            if (getMsgCount() == 0) return null;
            return new ArrayList<String>(messageList.subList(
                    getMsgCount() > count ? getMsgCount() - count : 0,
                            getMsgCount()));
        }
    }


}
