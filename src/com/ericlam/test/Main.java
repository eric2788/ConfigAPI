package com.ericlam.test;

import com.ericlam.test.config.api.ConfigAPI;
import com.ericlam.test.config.api.ConfigManager;
import com.ericlam.test.config.implement.TestConfig;

public class Main {
    public static void main(String[] args) {
        ConfigManager manager = ConfigAPI.init().register("test.yml", TestConfig.class).dump();
        TestConfig config = manager.getConfigAs("test.yml", TestConfig.class);
        System.out.println(config.toString());
    }
}
