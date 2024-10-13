package com.frankaboagye.connecthub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConnectHubApplication {

    public static void main(String[] args) {
        try{

        SpringApplication.run(ConnectHubApplication.class, args);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
//    @Bean
//    CommandLineRunner init(StorageServiceInterface storageServiceImplementation) {
//        return (args) -> {
//            // storageServiceImplementation.deleteAll();
//            storageServiceImplementation.init();
//        };
//    }

    void populateDB(){
        // TODO
    }

}
