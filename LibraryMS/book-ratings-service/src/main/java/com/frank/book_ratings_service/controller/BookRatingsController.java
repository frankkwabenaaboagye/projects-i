package com.frank.book_ratings_service.controller;

import com.frank.book_ratings_service.model.Rating;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ratings")
public class BookRatingsController {

    @GetMapping("/rating/{bookId}")
    public Rating getRating(@PathVariable String bookId) {
        return new Rating(
                bookId,
                5
        );
    }


}
