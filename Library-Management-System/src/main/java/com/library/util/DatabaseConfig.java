package com.library.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {

    private static final String CONFIG_PATH = "config/db.properties";

    private static String url;
    private static String username;
    private static String password;

    static {
        try {
            loadProperties();
        } catch (IOException e) {
            System.err.println("Failed to load DB config from " + CONFIG_PATH);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void loadProperties() throws IOException {
        Properties props = new Properties();
        InputStream input = new FileInputStream(CONFIG_PATH);
        props.load(input);
        url = props.getProperty("db.url");
        username = props.getProperty("db.username");
        password = props.getProperty("db.password");
    }

    public static String getUrl() {
        return url;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }
}

