package com.frank.book_catalog_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class BookCatalogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookCatalogServiceApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Bean
	public WebClient.Builder webClientBuilder(){
		return WebClient.builder();
	}




}
