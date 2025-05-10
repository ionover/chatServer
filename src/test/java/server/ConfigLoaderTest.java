package server;

import org.example.server.ConfigLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {

    @TempDir
    Path tmpDir;

    @Test
    void whenGoodSettingsProvided_thenPortIsParsed() throws Exception {
        Path cfg = tmpDir.resolve("server/settings.txt");
        Files.writeString(cfg, "port=555\n");

        ConfigLoader loader = new ConfigLoader(cfg);
        assertEquals(555, loader.getPort(), "порт должен прочитаться из файла");
    }

    @Test
    void whenEmptySettingsProvided_thenNumberFormatException() throws Exception {
        Path cfg = tmpDir.resolve("server/settings.txt");
        Files.createFile(cfg);

        ConfigLoader loader = new ConfigLoader(cfg);
        assertThrows(
                NumberFormatException.class,
                loader::getPort,
                "должны падать на неверном формате числа при пустом файле"
        );
    }
}
