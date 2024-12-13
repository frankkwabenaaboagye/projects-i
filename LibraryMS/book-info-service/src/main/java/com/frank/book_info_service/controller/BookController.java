package com.frank.book_info_service.controller;

import com.frank.book_info_service.model.Book;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

    @GetMapping("/{bookId}")
    public Book getBookInfo(@PathVariable String bookId) {
        return new Book(
                bookId,
                getRandomBook()
        );
    }


    private String getRandomBook(){
        String[] books = {"Things Fall Apart", "No Longer At Ease", "Arrow of God"};
        int a = (int)(Math.random() * books.length);
        return books[a];
    }

}
