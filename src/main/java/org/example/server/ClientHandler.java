package org.example.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler extends Thread {
    private final String EXIT_PHRASE = "/exit";

    private final Socket socket;
    private final ChatServer server;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            // Первым сообщением клиент должен прислать своё имя
            out.println("Введите ваше имя:");
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                socket.close();

                return;
            }
            System.out.println(username + " подключился.");

            // Рассылаем уведомление о входе в чат
            server.broadcast("System", username + " вошёл в чат");

            String line;
            while ((line = in.readLine()) != null) {
                if (EXIT_PHRASE.equalsIgnoreCase(line.trim())) {
                    break;
                }
                // Рассылаем всем
                server.broadcast(username, line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка в работе с клиентом " + username + ": " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignore) {
            }
            server.removeClient(this);
            System.out.println(username + " вышел.");

            server.broadcast("System", username + " покинул чат");
        }
    }

    // отправка одному клиенту
    public void send(String from, String msg) {
        out.println("[" + from + "] " + msg);
    }
}

