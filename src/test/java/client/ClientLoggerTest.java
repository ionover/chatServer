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

    private static final Path LOG_PATH = Paths.get("src/test/resources/file.log");
    private static final Pattern TIMESTAMPED_MSG =
            Pattern.compile("^\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}] .+$");

    @BeforeEach
    void setup() throws Exception {
        Files.createDirectories(LOG_PATH.getParent());
        Files.deleteIfExists(LOG_PATH);
        ClientLogger.setLogFile(LOG_PATH.toString());
    }

    @Test
    void logStartup_appendsStartupLine() throws Exception {
        ClientLogger.logStartup("localhost", 12345);

        assertTrue(Files.exists(LOG_PATH), "Лог-файл должен быть создан");
        List<String> lines = Files.readAllLines(LOG_PATH);
        assertEquals(1, lines.size(), "Должна быть ровно одна строка после logStartup");

        assertEquals(
                "Client started, connecting to localhost:12345",
                lines.getFirst(),
                "Сообщение о старте должно быть корректным"
        );
    }

    @Test
    void logMessage_appendsTimestampedMessages() throws Exception {
        ClientLogger.logMessage("msg1");
        ClientLogger.logMessage("msg2");

        List<String> lines = Files.readAllLines(LOG_PATH);
        assertEquals(2, lines.size(), "Должно быть две записи после двух вызовов logMessage");

        assertTrue(
                TIMESTAMPED_MSG.matcher(lines.get(0)).matches(),
                "Первая запись должна содержать валидный таймстамп"
        );
        assertTrue(lines.get(0).endsWith("msg1"), "Первая запись должна заканчиваться на 'msg1'");

        assertTrue(
                TIMESTAMPED_MSG.matcher(lines.get(1)).matches(),
                "Вторая запись должна содержать валидный таймстамп"
        );
        assertTrue(lines.get(1).endsWith("msg2"), "Вторая запись должна заканчиваться на 'msg2'");
    }
}
