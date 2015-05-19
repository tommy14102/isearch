package com.zznode.opentnms.isearch.otnRouteService.util;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import com.zznode.opentnms.isearch.otnRouteService.consts.ConstBusiness;

public class PropertiesHander {

    private static PropertiesConfiguration  configuration = null;  
    
    private static final String CONFIG_FILE = "ors-switch.properties";  
  
    private PropertiesHander() {}  
  
    private static PropertiesConfiguration getIntance() {  
        if (configuration == null) {  
            try {  
                configuration = new PropertiesConfiguration(CONFIG_FILE);  
            } catch (ConfigurationException e) {
            	e.printStackTrace();
            }  
            FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();  
            // 文件自动重加载延时设置为30秒，只有当文件修改之后才会重新加载（根据文件最后修改时间判断）  
            strategy.setRefreshDelay(30000);  
            configuration.setReloadingStrategy(strategy);  
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
