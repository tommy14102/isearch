package com.zznode.opentnms.isearch.otnRouteService.util;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class StaticPropertiesHander {

    private static PropertiesConfiguration  configuration = null;  
    
    private static final String CONFIG_FILE = "ors.properties";  
  
    private StaticPropertiesHander() {}  
  
    private static PropertiesConfiguration getIntance() {  
        if (configuration == null) {  
            try {  
                configuration = new PropertiesConfiguration(CONFIG_FILE);  
            } catch (ConfigurationException e) {
            	e.printStackTrace();
            }  
        }  
        return configuration;  
    }  
  
    public static String getProperty(String propertyName, String defaultValue) {  
        return getIntance().getString(propertyName, defaultValue);  
    }  
  
    public static String getProperty(String propertyName) {  
        return getIntance().getString(propertyName);  
    }  
    
    public static void setProperty(String propertyName,String value) {  
        getIntance().setProperty(propertyName, value);
    }  

}
