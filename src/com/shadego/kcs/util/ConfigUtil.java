package com.shadego.kcs.util;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigUtil {
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
    
    private static Properties property;
    
    public static String loadProperty(String name) {
        if (property == null) {
            property = new Properties();
            try {
//                property.load(ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties"));
                property.load(new FileInputStream("config.properties"));
            } catch (Exception e) {
                logger.error(e.getMessage());
                return null;
            }
        }
        String str = property.getProperty(name);
        return str == null ? "" : str.trim();
    }
}
