package com.kholodok.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int DEFAULT_PORT = 11000;
    private static final String IP = "127.0.0.1";
    private static final int THREAD_COUNT = 5;

    private int port;
    private ServerSocket serverSocket;
    private List<Connection> connections;
    private static ExecutorService executorService;

    private Server() {
        this(DEFAULT_PORT);
    }

    private Server(int port) {

        this.port = port;

        try {

            serverSocket = new ServerSocket(DEFAULT_PORT);

            work();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void work() {

        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        connections = Collections.synchronizedList(new ArrayList<Connection>());

        while (true) {

            try {

                Socket socket = serverSocket.accept();

                Connection connection = new Connection(socket);
                connections.add(connection);
                executorService.execute(connection);

            } catch (IOException e) {

                e.printStackTrace();

            } finally {

                closeAllConnections();
                executorService.shutdown();

            }
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
            ex.printStackTrace();
        }

    }

    private void sendToAllClient(Connection unnecessaryConn, String msg)  {
        for (Connection connection : connections) {
            if (connection == unnecessaryConn) continue;
            connection.out.println(msg);
        }
    }

    private class Connection implements Runnable {

        private BufferedReader in;
        private PrintWriter out;

        private Socket socket;

        private Connection(Socket socket) {

            this.socket = socket;

            try {

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }


        @Override
        public void run() {

            String msg;

            UUID uniqueName = UUID.randomUUID();
            Server.this.sendToAllClient(this, uniqueName + " came.");

            try {

                while (true) {
                    msg = in.readLine();
                    if (msg.equals("exit")) break;
                    Server.this.sendToAllClient(this,
                            uniqueName + " : " + msg);
                }

                Server.this.sendToAllClient(this,
                        uniqueName + " has left." );

            } catch (IOException ex){
                ex.fillInStackTrace();
            } finally {
                close();
            }
        }

        public void close() {

            try {

                in.close();
                out.close();
                socket.close();

                connections.remove(this);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {
        new Server();
    }
}