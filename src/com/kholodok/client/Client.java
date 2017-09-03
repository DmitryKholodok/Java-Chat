package com.kholodok.client;

import com.kholodok.User;
import com.kholodok.message.ClientMessage;
import com.kholodok.message.ServerMessage;
import com.kholodok.message.UserStatus;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final int DEFAULT_PORT = 11001;
    private static final String IP = "127.0.0.1";

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;
    private User user;
    private Scanner scanner;

    private Client() {

        try {

            socket = new Socket(IP, DEFAULT_PORT);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            scanner = new Scanner(System.in);
            userInit();

            work();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void userInit() throws IOException {

        System.out.println("Enter your name: ");
        user = new User(scanner.nextLine());

    }

    private void work() throws IOException {

        ConnectionIn connectionIn = new ConnectionIn();
        connectionIn.start();

        // init clientMessage
        ClientMessage clientMessage = createMessage("");

        // sendBaseUserInfoToServer
        out.writeObject(clientMessage);

        String str = "";
        while (!str.equals("exit")) {
            str = scanner.nextLine();
            if (str.equals("changeStatus")) changeUserStatus();
            else {
                clientMessage = createMessage(str);
                out.writeObject(clientMessage);
            }
        }
        connectionIn.setStop();
    }

    private void changeUserStatus() throws IOException {
        UserStatus userStatus;
        switch (scanner.nextInt()) {
            case 0:
                userStatus = UserStatus.EAT;
                break;
            case 1:
                userStatus = UserStatus.SLEEP;
                break;
            case 2:
                userStatus = UserStatus.SLEEP;
                break;
            case 3:
                userStatus = UserStatus.NOTHING;
                break;
            default:
                throw new IOException("Incorrect user status!");
        }
        user.setUserStatus(userStatus);
    }

    private ClientMessage createMessage(String msg) {
        ClientMessage clientMessage = new ClientMessage(
                user.getName(), user.getUserIp(), msg, user.getUserStatus());
        return clientMessage;
    }

    private void close() {

        try {

            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private class ConnectionIn extends Thread {

        private boolean isStop; //  default - false

        public void setStop() {
            isStop = true;
        }

        private ConnectionIn() { isStop = false; }

        @Override
        public void run() {

            Object msg = null;
            try {

                while (!isStop) {
                    while((msg = in.readObject()) == null) {
                        if (isStop) return;
                    }
                    System.out.println(((ServerMessage)msg).toString());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }

}