package com.frank.book_catalog_service.controller;

import com.frank.book_catalog_service.model.Book;
import com.frank.book_catalog_service.model.BookCatalog;
import com.frank.book_catalog_service.model.Rating;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class BookCatalogController {

    private RestTemplate restTemplate;
    private WebClient.Builder webClientBuilder;

    @GetMapping("/{userId}")
    public List<BookCatalog> getCatalog(@PathVariable String userId) {

        List<Rating> ratings = Arrays.asList(
                new Rating("b7709", 9),
                new Rating("b9901", 2),
                new Rating("b0882", 1),
                new Rating("b7881", 10)
        );


        return ratings.stream().map((rating)->{

                // rest template
            // Book book = restTemplate.getForObject("http://localhost:8083/books/" + rating.getBookId(), Book.class);

            Book book = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8083/books/" + rating.getBookId())
                    .retrieve()
                    .bodyToMono( Book.class)
                    .block();



            return BookCatalog.builder()
                    .name(book.getName())
                    .description("this is a description")
                    .rating(rating.getRating())
                    .build();

        }).collect(Collectors.toList());



    }
}
