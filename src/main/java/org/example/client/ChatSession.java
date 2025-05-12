package org.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ChatSession {
    private static final String EXIT_CMD = "/exit";

    private final BufferedReader serverIn;
    private final PrintWriter serverOut;
    private final BufferedReader userIn;

    public ChatSession(BufferedReader serverIn,
                       PrintWriter serverOut,
                       BufferedReader userIn) {
        this.serverIn = serverIn;
        this.serverOut = serverOut;
        this.userIn = userIn;
    }

    /**
     * Запускает сессию: handshake + фоновый reader + mainLoop.
     */
    public void start() throws IOException {
        performHandshake();
        Thread readerThread = createReaderThread();
        readerThread.setDaemon(true);
        readerThread.start();
        mainLoop();
    }

    private void performHandshake() throws IOException {
        String prompt = serverIn.readLine();
        System.out.println(prompt);
        ClientLogger.logMessage("System: " + prompt);

        String username = userIn.readLine();
        serverOut.println(username);
        ClientLogger.logMessage("[me] " + username);
    }

    private Thread createReaderThread() {
        return new Thread(() -> {
            try {
                String line;
                while ((line = serverIn.readLine()) != null) {
                    System.out.println(line);
                    ClientLogger.logMessage(line);
                }
            } catch (IOException e) {
                System.out.println("Connection closed: " + e.getMessage());
            }
        });
    }

    private void mainLoop() throws IOException {
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
