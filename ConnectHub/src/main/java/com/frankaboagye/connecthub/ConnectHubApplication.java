package com.frankaboagye.connecthub;

import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConnectHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConnectHubApplication.class, args);
    }
    @Bean
    CommandLineRunner init(StorageServiceInterface storageServiceImplementation) {
        return (args) -> {
            // storageServiceImplementation.deleteAll();
            storageServiceImplementation.init();
        };
    }

    void populateDB(){
        // TODO
    }

}
