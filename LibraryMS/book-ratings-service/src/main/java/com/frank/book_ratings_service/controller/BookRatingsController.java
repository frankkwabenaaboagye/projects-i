package com.frank.book_ratings_service.controller;

import com.frank.book_ratings_service.model.Rating;
import com.frank.book_ratings_service.model.UserRating;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/ratings")
public class BookRatingsController {

    @Value("${server.port}")
    private String thePort;

    @GetMapping("/rating/{bookId}")
    public Rating getRating(@PathVariable String bookId) {
        return new Rating(
                bookId,
                5
        );
    }

    @GetMapping("/users/{userId}")
    public UserRating getUserRatings(@PathVariable String userId) {

        System.out.println("Using the port..: " + thePort);

        UserRating userRating  = new UserRating();

        userRating.setTheUserRating(
                Arrays.asList(
                        new Rating("b7709", 9),
                        new Rating("b9901", 2),
                        new Rating("b0882", 1),
                        new Rating("b7881", 10)
                )
        );

        return userRating;

    }


}
