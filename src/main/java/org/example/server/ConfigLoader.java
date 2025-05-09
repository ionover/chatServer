package org.example.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final String CONFIG_FILE = "settings.txt";
    private static final Properties props = new Properties();

    static {
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("Не удалось загрузить настройки из " + CONFIG_FILE);
            System.out.println(e.getMessage());

            System.exit(1);
        }
    }

    public static int getPort() {
        return Integer.parseInt(props.getProperty("port").trim());
    }
}

