package com.ericlam.test.config.implement;

import com.ericlam.test.config.api.Component;
import com.ericlam.test.config.api.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Component
@NoArgsConstructor
@ToString
@Getter
public class Server {

    @Inject private String name;
    @Inject private String ip;
    @Inject private String version;
}
