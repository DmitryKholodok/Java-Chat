package com.kholodok.client;

import com.kholodok.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final int DEFAULT_PORT = 11000;
    private static final String IP = "127.0.0.1";

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private User user;

    private Client() {

        try {

            socket = new Socket(IP, DEFAULT_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            user = new User();

            work();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void work() throws IOException {

        ConnectionIn connectionIn = new ConnectionIn();
        connectionIn.start();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your name: ");
        user.setName(scanner.nextLine());

        String str = "";

        while (!str.equals("exit")) {
            str = scanner.nextLine();
            sendMsgWithUserInfo(str);
        }
        connectionIn.setStop();
    }

    private void sendMsgWithUserInfo(String msg) {
        out.println(user.getName() + " : " + msg);
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

            String msg = "";
            try {

                while (!isStop) {
                    while((msg = in.readLine()) == null) {
                        if (isStop) return;
                    }
                    System.out.println(msg);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }

}