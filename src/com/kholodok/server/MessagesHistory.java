package com.kholodok.server;

import java.util.*;

public class MessagesHistory<E> {

    private static final byte limitCount = Byte.MAX_VALUE;

    private int count;
    private List<E> messageList;

    public MessagesHistory(int count) {
        this.count = count;
        messageList = Collections.synchronizedList(
                new ArrayList<E>());
    }

    private byte getMsgCount() {
        return (byte)messageList.size();

    }

    public void addMsgToList(E msg) {
        synchronized (messageList) {
            if (messageList.size() == limitCount)
                refreshList();
            messageList.add(msg);
        }
    }

    //synchronized
    private void refreshList() {
        List<E> tempList = new ArrayList<E>(
                messageList.subList(limitCount - count + 1, limitCount));
        messageList.clear();
        messageList.addAll(tempList);
    }

    public List<E> getMsgList() {
        synchronized (messageList) {
            if (getMsgCount() == 0) return null;
            return new ArrayList<E>(messageList.subList(
                    getMsgCount() > count ? getMsgCount() - count : 0,
                            getMsgCount()));
        }
    }


}
