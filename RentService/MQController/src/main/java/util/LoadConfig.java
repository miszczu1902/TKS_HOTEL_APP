package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoadConfig {
    public static String loadPropertyFromConfig(String variable) {
        try (InputStream input = new FileInputStream("rabbit.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty(variable);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }
}
