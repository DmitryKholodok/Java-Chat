package com.kholodok.server;

import com.kholodok.client.Client;
import com.kholodok.message.ClientMessage;
import com.kholodok.message.ServerMessage;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int DEFAULT_PORT = 11001;
    private static final String IP = "127.0.0.1";
    private static final int THREAD_COUNT = 5;
    private static final byte MSG_SAVE_COUNT = 10;

    private int port;
    private ServerSocket serverSocket;
    private List<Connection> connections;
    private ExecutorService executorService;
    private MessagesHistory<ServerMessage> messagesHistory;


    public Server() {
        this(DEFAULT_PORT);
    }

    private Server(int port) {

        this.port = port;
        messagesHistory = new MessagesHistory(MSG_SAVE_COUNT);

        try {

            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("ServerSocket is ready!");

            work();

        } catch (IOException e) {
            System.out.println("ServerSocket create error.");
        }
    }

    private void work() {

        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        connections = Collections.synchronizedList(new ArrayList<Connection>());

        try {

            while (true) {

                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    System.err.println("Socket create error.");
                }

                Connection connection = new Connection(socket);
                connections.add(connection);

                executorService.execute(connection);
            }

        } finally {

            closeAllConnections();
            executorService.shutdown();
        }

    }

    private void closeAllConnections() {

        try {

            synchronized (connections) {

                Iterator<Connection> iterator = connections.iterator();
                while (iterator.hasNext()) {
                   iterator.next().close();
                }
            }

        } catch (Exception ex) {
            System.err.println("Closing error.");
        }

    }

    //except unnecessaryConn
    private void sendToAllClient(Connection unnecessaryConn, ServerMessage serverMessage)
            throws IOException {
        synchronized (connections) {
            for (Connection connection : connections) {
                if (connection == unnecessaryConn) continue;
                connection.out.writeObject(serverMessage);
            }
        }
    }

    private class Connection implements Runnable {

        private ObjectInputStream in;
        private ObjectOutputStream out;
        private Socket socket;

        private Connection(Socket socket) {

            this.socket = socket;

            try {

                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        @Override
        public void run() {

            writeLastMsgs();

            String userName = "";
            Object msg = null;
            try {

                // saving the user info
                userName = initUser();

                while (true) {

                    ClientMessage clientMessage = (ClientMessage) in.readObject();
                    ServerMessage serverMessage = new ServerMessage(clientMessage);

                    if (serverMessage.getClientMessage().equals("exit")) break;

                    messagesHistory.addMsgToList(serverMessage);

                    Server.this.sendToAllClient(this, serverMessage);
                }

            } catch (IOException ex){
                System.err.println("Error!");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    destroyUser(userName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                close();
            }
        }

        private String initUser() throws IOException, ClassNotFoundException {
            ClientMessage clientMsg = (ClientMessage) in.readObject();
            String name = clientMsg.getUserName();
            sendJustServerMsg(name + " is here.");
            return name;
        }

        private void sendJustServerMsg(String serverMsg) throws IOException {
            ServerMessage clientIsHereMsg = new ServerMessage(null);
            clientIsHereMsg.setMsg(serverMsg);
            sendToAllClient(this, clientIsHereMsg);
        }

        private void destroyUser(String name) throws IOException {
            sendJustServerMsg(name + "  went out.");
        }

        private void writeLastMsgs() {
            List<ServerMessage> lastMsgs = messagesHistory.getMsgList();
            try {
                if (lastMsgs != null) {
                    for (ServerMessage serverMessage : lastMsgs)
                        out.writeObject(serverMessage);
                }
            } catch (IOException ex)
            {
                ex.fillInStackTrace();
            }

        }

        public void close() {

            try {

                in.close();
                out.close();
                socket.close();

                synchronized (connections) {
                    connections.remove(this);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        new Server();
    }
}