package org.example.server;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.net.ServerSocket;
import java.net.Socket;

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

    public void broadcast(String username, String message) {
        ServerLogger.logMessage(username, message);
        for (ClientHandler c : clients) {
            c.send(username, message);
        }
    }

    public void removeClient(ClientHandler c) {
        clients.remove(c);
    }

    public static void main(String[] args) {
        try {
            // путь к порту settings.txt
            Path configPath = Path.of("src/main/resources/settings.txt");
            ConfigLoader loader = new ConfigLoader(configPath);
            int port = loader.getPort();

            // Запускаем сервер
            new ChatServer(port).start();

        } catch (IOException e) {
            System.err.println("Не удалось загрузить конфигурацию: " + e.getMessage());

            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("Некорректное значение порта: " + e.getMessage());

            System.exit(2);
        }
    }
}
