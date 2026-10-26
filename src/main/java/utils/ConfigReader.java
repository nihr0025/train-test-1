package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to read properties from config.properties file with System property overrides.
 */
public class ConfigReader {

    private static Properties properties;
    private static final String DEFAULT_CONFIG_PATH = "src/test/resources/config.properties";

    static {
        loadProperties();
    }

    private static synchronized void loadProperties() {
        properties = new Properties();
        try (InputStream input = new FileInputStream(DEFAULT_CONFIG_PATH)) {
            properties.load(input);
        } catch (IOException e) {
            // Also attempt loading from classpath if path fails
            try (InputStream cpInput = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (cpInput != null) {
                    properties.load(cpInput);
                } else {
                    System.err.println("Could not load config.properties: " + e.getMessage());
                }
            } catch (IOException ex) {
                System.err.println("Could not load config.properties from classpath: " + ex.getMessage());
            }
        }
    }

    public static String getProperty(String key) {
        String sysVal = System.getProperty(key);
        if (sysVal != null && !sysVal.trim().isEmpty()) {
            return sysVal;
        }
        if (properties == null) {
            loadProperties();
        }
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val != null && !val.trim().isEmpty()) ? val : defaultValue;
    }

    public static int getIntProperty(String key, int defaultValue) {
        String val = getProperty(key);
        if (val != null) {
            try {
                return Integer.parseInt(val.trim());
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String val = getProperty(key);
        if (val != null) {
            return Boolean.parseBoolean(val.trim());
        }
        return defaultValue;
    }

    public static String getAppUrl() {
        String url = getProperty("baseUrl");
        if (url == null || url.trim().isEmpty()) {
            url = getProperty("app.url", "https://train-mate-blond.vercel.app/");
        }
        return url;
    }

    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }

    public static boolean isHeadless() {
        return getBooleanProperty("headless", false);
    }
}
