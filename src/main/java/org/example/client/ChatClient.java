package org.example.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ChatClient {
    private static final String EXIT_CMD = "/exit";

    public static void main(String[] args) {
        try {
            Path cfg = Path.of("src/main/resources/client/settings.txt");
            ConfigLoader loader = new ConfigLoader(cfg);
            String host = loader.getHost();
            int port = loader.getPort();
            ClientLogger.logStartup(host, port);

            try (Socket socket = new Socket(host, port);
                 BufferedReader serverIn = new BufferedReader(
                         new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter serverOut = new PrintWriter(
                         new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                 BufferedReader userIn = new BufferedReader(
                         new InputStreamReader(System.in, StandardCharsets.UTF_8))
            ) {
                //ввод имени
                String prompt = serverIn.readLine();
                System.out.println(prompt);
                ClientLogger.logMessage("System: " + prompt);

                String username = userIn.readLine();
                serverOut.println(username);
                ClientLogger.logMessage("[me] " + username);

                //входящие сообщения
                Thread readerThread = new Thread(() -> {
                    try {
                        String line;
                        while ((line = serverIn.readLine()) != null) {
                            System.out.println(line);
                            ClientLogger.logMessage(line);
                        }
                    } catch (IOException ignored) {
                    }
                });
                readerThread.setDaemon(true);
                readerThread.start();

                //Главный цикл
                String input;
                while ((input = userIn.readLine()) != null) {
                    serverOut.println(input);
                    ClientLogger.logMessage("[me] " + input);
                    if (EXIT_CMD.equalsIgnoreCase(input.trim())) {

                        break;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка клиента: " + e.getMessage());
        }
    }
}
