package org.example.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ChatClient {
    private static final String EXIT_CMD = "/exit";

    public static void main(String[] args) {
        try {
            // Загрузка конфигурации
            ConfigLoader loader = loadConfig(Path.of("src/main/resources/client/settings.txt"));
            String host = loader.getHost();
            int port = loader.getPort();
            ClientLogger.logStartup(host, port);

            // Установка соединения и запуск клиента
            try (Socket socket = establishConnection(host, port);
                 BufferedReader serverIn = new BufferedReader(
                         new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter serverOut = new PrintWriter(
                         new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                 BufferedReader userIn = new BufferedReader(
                         new InputStreamReader(System.in, StandardCharsets.UTF_8))
            ) {
                performHandshake(serverIn, serverOut, userIn);
                Thread readerThread = startReaderThread(serverIn);
                readerThread.setDaemon(true);
                readerThread.start();

                mainLoop(userIn, serverOut);
            }

        } catch (Exception e) {
            System.err.println("Ошибка клиента: " + e.getMessage());
        }
    }

    /**
     * Загружает настройки из файла и возвращает ConfigLoader.
     */
    private static ConfigLoader loadConfig(Path configPath) throws IOException {
        return new ConfigLoader(configPath);
    }

    /**
     * Устанавливает TCP-соединение с сервером.
     */
    private static Socket establishConnection(String host, int port) throws IOException {
        return new Socket(host, port);
    }

    /**
     * Обменивается сообщениями приветствия с сервером:
     * - считывает приглашение ввести имя
     * - отправляет введённое имя
     */
    private static void performHandshake(BufferedReader serverIn,
                                         PrintWriter serverOut,
                                         BufferedReader userIn) throws IOException {
        String prompt = serverIn.readLine();
        System.out.println(prompt);
        ClientLogger.logMessage("System: " + prompt);

        String username = userIn.readLine();
        serverOut.println(username);
        ClientLogger.logMessage("[me] " + username);
    }

    /**
     * Создаёт и возвращает поток, который в фоновом режиме
     * читает и выводит входящие сообщения от сервера.
     */
    private static Thread startReaderThread(BufferedReader serverIn) {
        return new Thread(() -> {
            try {
                String line;
                while ((line = serverIn.readLine()) != null) {
                    System.out.println(line);
                    ClientLogger.logMessage(line);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    /**
     * Основной цикл чтения ввода пользователя и отправки сообщений на сервер.
     * Прекращает работу при вводе команды EXIT_CMD.
     */
    private static void mainLoop(BufferedReader userIn, PrintWriter serverOut) throws IOException {
        String input;
        while ((input = userIn.readLine()) != null) {
            serverOut.println(input);
            ClientLogger.logMessage("[me] " + input);
            if (EXIT_CMD.equalsIgnoreCase(input.trim())) {
                break;
            }
        }
    }
}
