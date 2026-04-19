package de.lmu.sep.loveletter.chat;

import java.io.*;
import java.net.Socket;

public class ChatClient {

    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 5000;

        try (
            Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader stdin = new BufferedReader(
                new InputStreamReader(System.in)
            )
        ) {
            Thread listener = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException ignored) {}
            });
            listener.setDaemon(true);
            listener.start();

            String input;
            while ((input = stdin.readLine()) != null) {
                out.println(input);
                if ("bye".equalsIgnoreCase(input)) {
                    break;
                }
            }
        }
    }
}
