package com.impetus.mailsender.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesUtil {

    static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    @Value("${external.properties.path}")
    private String externalPropertiesPath;

    /** Use this API for downloading properties dynamically. It will reload properties every time reading a properties. WARNING! Do not use this API
     * until u need to re-load properties on every read operation.
     * 
     * @param name
     * @param propertyFile
     * @return
     * @throws FileNotFoundException
     * @throws IOException */
    public String getProperty(final String name) throws FileNotFoundException, IOException {
        Properties props = new Properties();
        logger.debug("externalPropertiesPath :" + externalPropertiesPath);
        props.load(new FileInputStream(externalPropertiesPath));
        logger.debug("external properties :" + props);
        return props.getProperty(name);
    }

    public String getExternalPropertiesPath() {
        return externalPropertiesPath;
    }

    public void setExternalPropertiesPath(String externalPropertiesPath) {
        this.externalPropertiesPath = externalPropertiesPath;
    }

}
