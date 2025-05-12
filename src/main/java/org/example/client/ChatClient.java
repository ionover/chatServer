package org.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ChatClient {
    public static void main(String[] args) {
        Path configPath = Path.of("src/main/resources/client/settings.txt");

        try {
            // Загрузка конфигурации
            ConfigLoader loader = new ConfigLoader(configPath);
            String host = loader.getHost();
            int port = loader.getPort();
            ClientLogger.logStartup(host, port);

            // Установка соединения и запуск сессии
            try (Socket socket = new Socket(host, port);
                 BufferedReader serverIn = new BufferedReader(
                         new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter serverOut = new PrintWriter(
                         new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                 BufferedReader userIn = new BufferedReader(
                         new InputStreamReader(System.in, StandardCharsets.UTF_8))
            ) {
                ChatSession session = new ChatSession(serverIn, serverOut, userIn);

                session.start();
            }

        } catch (IOException e) {
            System.err.println("Ошибка клиента: " + e.getMessage());
        }
    }
}
