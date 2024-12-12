package com.frank.book_catalog_service.controller;

import com.frank.book_catalog_service.model.BookCatalog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/catalog")
public class BookCatalogController {

    @GetMapping("/{userId}")
    public List<BookCatalog> getCatalog(@PathVariable String userId) {
        return Collections.singletonList(
                new BookCatalog(
                        "Example Book",
                        "Fiction",
                        10
                )
        );
    }
}
