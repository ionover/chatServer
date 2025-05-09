package org.example.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigLoader {
    private final Properties props = new Properties();

    public ConfigLoader(Path configPath) throws IOException {
        try (var in = Files.newInputStream(configPath)) {
            props.load(in);
        }
    }

    public int getPort() {
        String raw = props.getProperty("port");
        if (raw == null || raw.isBlank()) {
            throw new NumberFormatException("Свойство 'port' отсутствует или пустое");
        }

        return Integer.parseInt(raw.trim());
    }
}
