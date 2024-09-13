package com.frankaboagye.connecthub.interfaces;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageServiceInterface {

    void init();
    void store(MultipartFile file);
    Path load(String filename);
    Resource loadAsResource(String filename);
    void deleteAll();
    Stream<Path> loadAll();
}
