## LibraryMS

### Communication and Service Discovery

This is a demo project to understanding microservices

- Microservices
- Book Catalog Service
- Book Info Service
- Book Rating Service

### Lessons

- When you want change from something which is not an object to an Object,

  - provide a no args constructor

- Web client (recommended now) vs rest template

```java

@GetMapping("/{userId}")
public Mono<List<BookCatalog>> getCatalog(@PathVariable String userId) {

    List<Rating> ratings = Arrays.asList(
            new Rating("b7709", 9),
            new Rating("b9901", 2),
            new Rating("b0882", 1),
            new Rating("b7881", 10)
    );

    return Flux.fromIterable(ratings)
            .flatMap(rating ->
                webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8083/books/" + rating.getBookId())
                    .retrieve()
                    .bodyToMono(Book.class)
                    .map(book -> BookCatalog.builder()
                            .name(book.getName())
                            .description("this is a description")
                            .rating(rating.getRating())
                            .build())
            )
            .collectList(); // Converts Flux<BookCatalog> into Mono<List<BookCatalog>>
}


@GetMapping("/{userId}")
public List<BookCatalog> getCatalog(@PathVariable String userId) {
    List<Rating> ratings = Arrays.asList(
            new Rating("b7709", 9),
            new Rating("b9901", 2),
            new Rating("b0882", 1),
            new Rating("b7881", 10)
    );

    return ratings.parallelStream()
            .map(rating -> {
                Book book = webClientBuilder.build()
                        .get()
                        .uri("http://localhost:8083/books/" + rating.getBookId())
                        .retrieve()
                        .bodyToMono(Book.class)
                        .block(); // Still blocking, but now it's concurrent
                return BookCatalog.builder()
                        .name(book.getName())
                        .description("this is a description")
                        .rating(rating.getRating())
                        .build();
            })
            .collect(Collectors.toList());
}



```

- Have a wrapper object around your response
  - so that changes to a field or
  - general changes will not break anything for
  - consumers of the API

```java

List<Rating> ratings = restTemplate.getForObject("http://localhost:8082/ratings/users/"+ userId,  ???? );

/*
What goes in here??
That is the issue
we have to do something like this
*/
@GetMapping("/{userId}")
public List<BookCatalog> getCatalog(@PathVariable String userId) throws JsonProcessingException {

    ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:8082/ratings/users/" + userId,
            String.class
    );

    ObjectMapper objectMapper = new ObjectMapper();
    List<Rating> ratings = objectMapper.readValue(
            response.getBody(),
            new TypeReference<List<Rating>>() {}
    );
/*
just create a user ratings objects that will contain
the list of ratings
*/
```

---

- service discovery

  - a layer of abstraction

    - a discovery server

  - client side service discovery
  - server side service discovery

- Spring cloud uses client side service discovery

- Note:

  - Spring has done and is doing a lot of things for you

- Technology: Eureka - spring cloud uses it for service discovery

- others

  - Eureka
  - Ribbon
  - Hysterix
  - Zuul

- the layer of abostration will be the
  - eureka server (the discovery server)
- the indevidual services will be eureka clients
- and the client that is consuming should also be a eureka client

```java

// add the dependency

// aslo enable it

@EnableEurekaServer
public class DiscoveryServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServerApplication.class, args);
	}

}


// dont make eureka register with itself

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

```

- we want to have our 3 microservices register
- with eureka server

```xml

// under properties
<spring-cloud.version>2024.0.0</spring-cloud.version>

<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>


// note
@EnableEurekaClient
 the annotation is optional because Spring Boot will automatically enable the Eureka Client functionality if the necessary dependencies are included in your project (e.g., spring-cloud-starter-netflix-eureka-client).

```

- how does the client find the server and registers itself
- since we are using the default, it is able to

- but you can work around

```java
eureka.client.service-url.defaultZone=http://localhost:<theEurekaPort>/eureka/
```

- now lets consume it
  - we are consuming it inside of the catalog service

```java

// we are doing service discovery with load balancing

@Bean
@LoadBalanced
public RestTemplate restTemplate(){
    return new RestTemplate();
}


UserRating userRating = restTemplate.getForObject("http://localhost:8082/ratings/users/"+ userId, UserRating.class);
    // using eureka
UserRating userRating = restTemplate.getForObject("http://book-ratings-service/ratings/users/"+ userId, UserRating.class);


```

- Note:

```java

// use this to test the loadbalancing

java -jar app-0.0.1-SNAPSHOT.jar  // justing running the class with the main method inside of it

// you can change the port
java -Dserver.port=8206 -jar app-0.0.1-SNAPSHOT.jar

```

- more control

```java

// import org.springframework.cloud.client.discovery.DiscoveryClient;


DiscoveryClient discoveryClient;

// you can do anything from here on



```

- fault tolerance

  - what eureka clients do by defalt is
  - to ping the eureka server on regular basis
  - and send out `heart beats`

- what is the discovery server is down but the services are up?
- well the client(consuning it) -> picks it up from the `cache` - AMAZING!

---
