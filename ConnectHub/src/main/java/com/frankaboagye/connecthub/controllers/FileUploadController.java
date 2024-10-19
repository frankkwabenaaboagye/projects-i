package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.exceptions.StorageFileNotFoundException;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class FileUploadController {

    private final StorageServiceInterface storageServiceImplementation;

    @GetMapping("/tryOut")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute(
                "files",
                storageServiceImplementation
                        .loadAll()
                        .map( path -> MvcUriComponentsBuilder
                                            .fromMethodName(
                                                    FileUploadController.class,
                                                    "serveFile",
                                                    path.getFileName().toString()
                                            )
                                            .build()
                                            .toUri()
                                            .toString()
                        )
                        .collect(Collectors.toList()));

        return "uploadFormTryOut";
    }

    @GetMapping("/d_files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageServiceImplementation.loadAsResource(filename);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\""
                )
                .body(file);
    }


    @GetMapping("/files/{fileName:.+}")
    @ResponseBody // added this
    public ResponseEntity<Resource> displayFile(@PathVariable String fileName) {
        Resource file = storageServiceImplementation.loadAsResource(fileName);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF) // Or other content types based on file extension
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"") // inline ensures it's displayed
                .body(file);
    }


    @PostMapping("/handleform")
    public String handleFileUpload(
            @RequestParam("theUploadedFile") MultipartFile theUploadedFile,  RedirectAttributes redirectAttributes
    ) {

        storageServiceImplementation.store(theUploadedFile);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + theUploadedFile.getOriginalFilename() + "!");

        return "redirect:/myDefaults";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/myDefaults")
    @ResponseBody
    public String myDefaults() {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <title>My Defaults</title>" +
                "</head>" +
                "<body>" +
                "    <h1>Welcome to My Defaults Page</h1>" +
                "</body>" +
                "</html>";
    }
}
