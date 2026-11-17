package com.ise.myntra.utils;

import java.io.InputStream;
import java.util.Properties;

/** Reads config.properties; any key can be overridden with -Dkey=value. */
public final class Config {
    private static final Properties P = new Properties();
    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) P.load(in);
        } catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }
    private Config() {}
    public static String get(String key) { return System.getProperty(key, P.getProperty(key)); }
    public static int getInt(String key) { return Integer.parseInt(get(key)); }
}
