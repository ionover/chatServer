package org.example.server;

import java.io.BufferedWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerLogger {
    private static String LOG_FILE = "src/main/resources/file.log";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void setLogFile(String path) {
        LOG_FILE = path;
    }

    // Вызывается при старте сервера
    public static void logStartup(int port) {
        logRaw("Server started on port " + port);
    }

    // Вызывается для любых событий/сообщений
    public static synchronized void logMessage(String username, String msg) {
        String timestamp = LocalDateTime.now().format(FMT);
        logRaw(String.format("[%s] %s: %s", timestamp, username, msg));
    }

    private static void logRaw(String line) {
        try (BufferedWriter bw = Files.newBufferedWriter(
                Paths.get(LOG_FILE),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,       // создаёт, если нет
                StandardOpenOption.APPEND)) {    // дописывает
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Не удалось записать лог: " + e.getMessage());
        }
    }
}
