package com.frank.book_info_service.controller;

import com.frank.book_info_service.model.Book;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

    @GetMapping("/book/{bookId}")
    public Book getBookInfo(@PathVariable Long bookId) {
        return new Book(
                bookId,
                "Example Book"
        );
    }

}
