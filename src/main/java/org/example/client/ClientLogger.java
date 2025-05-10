package org.example.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientLogger {
    private static String LOG_FILE = "src/main/resources/file.log";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void setLogFile(String path) {
        LOG_FILE = path;
    }

    public static void logStartup(String host, int port) {
        logRaw("Client started, connecting to " + host + ":" + port);
    }

    public static synchronized void logMessage(String msg) {
        String timestamp = LocalDateTime.now().format(FMT);
        logRaw(String.format("[%s] %s", timestamp, msg));
    }

    private static void logRaw(String line) {
        try (BufferedWriter bw = Files.newBufferedWriter(
                Paths.get(LOG_FILE),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Не удалось записать лог: " + e.getMessage());
        }
    }
}
