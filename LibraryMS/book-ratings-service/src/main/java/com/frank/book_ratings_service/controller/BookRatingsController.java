package com.frank.book_ratings_service.controller;

import com.frank.book_ratings_service.model.Rating;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

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

    @GetMapping("/users/{userId}")
    public List<Rating> getUserRatings(@PathVariable String userId) {

        return Arrays.asList(
                new Rating("b7709", 9),
                new Rating("b9901", 2),
                new Rating("b0882", 1),
                new Rating("b7881", 10)
        );

    }


}
