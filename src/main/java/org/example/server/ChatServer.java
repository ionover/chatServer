package org.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private final int port;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        ServerLogger.logStartup(port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                clients.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Ошибка в работе сервера: " + e.getMessage());
        }
    }

    // Рассылает сообщение всем подключённым
    public void broadcast(String username, String message) {
        ServerLogger.logMessage(username, message);
        for (ClientHandler c : clients) {
            c.send(username, message);
        }
    }

    // Убирает клиента из списка при отключении
    public void removeClient(ClientHandler c) {
        clients.remove(c);
    }

    public static void main(String[] args) {
        int port = ConfigLoader.getPort();
        new ChatServer(port).start();
    }
}
