package de.lmu.sep.loveletter.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {

    private final int port;
    private final Map<String, ClientHandler> clients = new HashMap<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(this, socket)).start();
            }
        }
    }

    public synchronized boolean registerClient(String name, ClientHandler handler) {
        if (clients.containsKey(name)) {
            return false;
        }
        clients.put(name, handler);
        broadcast("SERVER", name + " joined the room");
        return true;
    }

    public synchronized void removeClient(String name) {
        clients.remove(name);
        broadcast("SERVER", name + " left the room");
    }

    public synchronized void broadcast(String from, String message) {
        for (ClientHandler handler : clients.values()) {
            handler.sendMessage(from + ": " + message);
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatServer(5000).start();
    }
}
