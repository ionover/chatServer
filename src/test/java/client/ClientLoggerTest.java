package client;

import org.example.client.ClientLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ClientLoggerTest {

    private static final Path LOG_PATH = Paths.get("file.log");
    private static final Pattern TIMESTAMPED_MSG =
            Pattern.compile("^\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}] .+$");

    @BeforeEach
    void cleanLogFile() throws Exception {
        Files.deleteIfExists(LOG_PATH);
    }

    @Test
    void logStartup_appendsStartupLine() throws Exception {
        ClientLogger.logStartup("localhost", 12345);

        assertTrue(Files.exists(LOG_PATH), "должен создаться файл лога");
        List<String> lines = Files.readAllLines(LOG_PATH);

        assertEquals(1, lines.size(), "должна быть ровно одна строка");
        assertEquals(
                "Client started, connecting to localhost:12345",
                lines.getFirst(),
                "строка должна точно соответствовать шаблону логирования старта"
        );
    }

    @Test
    void logMessage_appendsTimestampedMessages() throws Exception {
        ClientLogger.logMessage("msg1");
        ClientLogger.logMessage("msg2");

        List<String> lines = Files.readAllLines(LOG_PATH);
        assertEquals(2, lines.size(), "должно быть две строки после двух вызовов");

        assertTrue(
                TIMESTAMPED_MSG.matcher(lines.get(0)).matches(),
                "первая строка должна содержать корректный таймстамп и сообщение"
        );
        assertTrue(
                TIMESTAMPED_MSG.matcher(lines.get(1)).matches(),
                "вторая строка должна содержать корректный таймстамп и сообщение"
        );

        assertTrue(lines.get(0).endsWith("msg1"), "первая строка должна заканчиваться на msg1");
        assertTrue(lines.get(1).endsWith("msg2"), "вторая строка должна заканчиваться на msg2");
    }
}
