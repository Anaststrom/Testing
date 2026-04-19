package de.lmu.sep.loveletter.chat;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final ChatServer server;
    private final Socket socket;
    private PrintWriter out;
    private String name;

    public ClientHandler(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            )
        ) {
            out = new PrintWriter(socket.getOutputStream(), true);

            // Nickname abfragen, bis einer frei ist
            while (true) {
                out.println("Enter your nickname:");
                String candidate = in.readLine();
                if (candidate == null) {
                    return;
                }
                if (server.registerClient(candidate, this)) {
                    name = candidate;
                    out.println("welcome " + name);
                    break;
                } else {
                    out.println("name already taken, try another");
                }
            }

            // Nachrichten lesen
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("bye")) {
                    break;
                }
                server.broadcast(name, line);
            }

        } catch (IOException ignored) {
        } finally {
            if (name != null) {
                server.removeClient(name);
            }
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
}
