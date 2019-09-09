package com.ericlam.test.config.api;

import java.util.HashMap;
import java.util.Map;

public class ConfigAPI {

    private Map<String, Class<? extends Configuration>> ymls = new HashMap<>();

    public static ConfigAPI init(){
        return new ConfigAPI();
    }

    private ConfigAPI(){}

    public ConfigAPI register(String yml, Class<? extends Configuration> type){
        ymls.put(yml, type);
        return this;
    }

    public ConfigManager dump(){
        return new ConfigManager(ymls);
    }
}
