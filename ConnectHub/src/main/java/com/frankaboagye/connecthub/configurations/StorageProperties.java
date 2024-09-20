package com.frankaboagye.connecthub.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration // register this
@ConfigurationProperties("storage")
public class StorageProperties {

    // folder location for storing files: folder at the src level
    private String location = "src/main/resources/upload-dir";
}
