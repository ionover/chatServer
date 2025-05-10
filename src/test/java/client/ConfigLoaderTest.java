package client;

import org.example.client.ConfigLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {

    @TempDir
    Path tmpDir;

    @Test
    void whenAllPropertiesPresent_thenLoadSuccessfully() throws Exception {
        Path cfg = tmpDir.resolve("settings.txt");
        Files.writeString(cfg, """
                host=example.com
                port=9876
                """);

        ConfigLoader loader = new ConfigLoader(cfg);
        assertEquals("example.com", loader.getHost(), "должен прочитать host из файла");
        assertEquals(9876, loader.getPort(), "должен прочитать port из файла");
    }

    @Test
    void whenHostMissing_thenIllegalArgumentException() throws Exception {
        Path cfg = tmpDir.resolve("settings.txt");
        Files.writeString(cfg, "port=12345\n");

        ConfigLoader loader = new ConfigLoader(cfg);
        assertThrows(
                IllegalArgumentException.class,
                loader::getHost,
                "должен падать при отсутствии свойства host"
        );
    }
}
