package com.ericlam.test.config.implement;

import com.ericlam.test.config.api.Component;
import com.ericlam.test.config.api.Configuration;
import com.ericlam.test.config.api.Inject;
import com.ericlam.test.config.api.Resource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@ToString
@Resource(locate = "/test.yml")
public class TestConfig implements Configuration {
    @Inject private String name;
    @Inject private String version;
    @Inject private int number;
    @Inject private List<String> authors;
    @Inject
    private Server server;
    @Inject
    private Map<String, @Component Server> servers;
}
