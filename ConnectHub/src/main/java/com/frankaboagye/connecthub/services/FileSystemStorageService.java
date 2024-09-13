package com.frankaboagye.connecthub.services;

import com.frankaboagye.connecthub.configurations.StorageProperties;
import com.frankaboagye.connecthub.exceptions.StorageException;
import com.frankaboagye.connecthub.exceptions.StorageFileNotFoundException;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageServiceInterface {

    private final Path rootLocation;

    public FileSystemStorageService(StorageProperties storageProperties) {
        if(storageProperties.getLocation().trim().isEmpty()){
            throw new StorageException("File upload location can not be Empty.");
        }
        this.rootLocation = Paths.get(storageProperties.getLocation());


    }

    @Override
    public void init() {
        try{
            Files.createDirectories(rootLocation);
        } catch(IOException e){
            String message =  "Could not initialize storage or Could not create the directory at " + rootLocation.toString();
            throw new StorageException(message, e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        try{
            if(file.isEmpty()){
                throw new StorageException("Failed to store empty file.");
            }

            Path destinationFile = this.rootLocation.resolve(file.getOriginalFilename()).normalize().toAbsolutePath();

            if(!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())){
                throw new StorageException("Cannot store file outside current directory.");
            }

            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

        }catch (IOException e){
            throw new StorageException("Failed to store file.", e);
        }

    }


    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try{

            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());

            if(resource.exists() && resource.isReadable()){
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }

        }catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }


    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());

    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }
}
