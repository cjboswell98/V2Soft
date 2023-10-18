package com.product.rating.controller;

import com.product.rating.domain.Client;
import com.product.rating.domain.ReviewDomain;
import com.product.rating.repository.ClientRepository;
import com.product.rating.services.ClientService;
import com.product.rating.services.ReviewService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from your Angular app
public class ProductController {

    private final ClientService clientService;
    private final ReviewService reviewService;
    private final MongoTemplate mongoTemplate;
    public static final Logger lowReviewsLogger = LogManager.getLogger("lowReviews");
    private static final Logger formattedReviewLogger = LogManager.getLogger("formattedReview");
    private final ClientRepository clientRepository;

    @Autowired
    public ProductController(ReviewService reviewService, MongoTemplate mongoTemplate, ClientService clientService, ClientRepository clientRepository) {
        this.reviewService = reviewService;
        this.mongoTemplate = mongoTemplate;
        this.clientService = clientService;
        this.clientRepository = clientRepository;
    }

    @Value("${collection.name}")
    private String collectionName; // Inject the collection name from application.properties

    @Value("${customlog.dateformat}")
    private String customLogDateFormat; // Inject the custom log date format

    @Value("${customlog.messageformat}")
    private String customLogMessageFormat; // Inject the custom log message format


    @PostMapping("/createCollection")
    public ResponseEntity<String> createCollection(@RequestParam String collectionName) {
        try {
            reviewService.createCollection(this.collectionName); // Use the injected collection name
            return new ResponseEntity<>("Collection created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating collection: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteCollection/{collectionName}")
    public ResponseEntity<String> deleteCollection(@PathVariable String collectionName) {
        try {
            reviewService.deleteCollection(this.collectionName); // Use the injected collection name
            return new ResponseEntity<>("Collection deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting collection: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //C (R) U D
    @GetMapping("/viewAllReviews")
    public ResponseEntity<List<ReviewDomain>> viewAllReviews() {
        List<ReviewDomain> ratings = reviewService.viewAllReviews();
        if (ratings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(ratings, HttpStatus.OK);
        }
    }

    @GetMapping("/viewReviews/{collectionName}")
    public ResponseEntity<List<ReviewDomain>> viewReviewsInCollection(@PathVariable String collectionName) {
        List<ReviewDomain> reviews = reviewService.viewReviewsInCollection(collectionName);
        if (reviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        }
    }

    //C (R) U D
    @GetMapping("/viewLatestReviews")
    public ResponseEntity<List<ReviewDomain>> viewLatestReviews(@RequestParam(name = "limit", defaultValue = "10") int limit) {
        List<ReviewDomain> latestReviews = reviewService.getLatestReviews(limit);

        if (latestReviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(latestReviews, HttpStatus.OK);
        }
    }

    //C (R) U D
    @GetMapping("/viewByRateCode")
    public ResponseEntity<List<ReviewDomain>> viewReviewsByRateCode(@RequestParam int rateCode) {
        List<ReviewDomain> reviews = reviewService.findReviewsByRateCode(rateCode);

        if (reviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        }
    }

    //(C) R U D
    @PostMapping("/addReview")
    public ResponseEntity<String> addReview(@RequestBody ReviewDomain newReview) {
        try {
            String result = reviewService.addReview(newReview);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add review: " + e.getMessage());
        }
    }

    //C R (U) D
    @PutMapping("/updateReview/{id}")
    public ResponseEntity<ReviewDomain> updateReview(
            @PathVariable String id,
            @RequestBody ReviewDomain updatedRating
    ) {
        return reviewService.updateReviewById(collectionName, id, updatedRating);
    }

    //C R U (D)
    //spring expression language - @authorizationServiceImpl.verifyToken(#token, #clientId, 'delete')
    @PreAuthorize("@authorizationServiceImpl.verifyToken(#token, #clientId, 'delete')")
    @DeleteMapping("/deleteReview/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable String id) {
        boolean deleted = reviewService.deleteReviewById(collectionName, id);
        if (deleted) {
            return ResponseEntity.ok("Deleted Rating with ID: " + id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
//change clientId
@PreAuthorize("@authorizationServiceImpl.verifyToken(#token, #clientId, 'delete')")
@DeleteMapping("/deleteReview")
public ResponseEntity<String> deleteReview(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "ClientId") String clientId, @RequestHeader(name = "ReviewId") String reviewId) {
    boolean deleted = reviewService.deleteReviewById(collectionName, reviewId);
    if (deleted) {
        return ResponseEntity.ok("Deleted Rating with Review ID: " + reviewId);
    } else {
        return ResponseEntity.notFound().build();
    }
}

    //create a new client endpoint -- pass in client secret/secret
    @PostMapping("/newClient")
    public String newClient(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        return clientService.createNewClient(username, password);
    }

    @GetMapping("/viewClients")
    public List<Client> getAllClients() {
        // Assuming your client service has a method to fetch all clients sorted by the most recent
        return clientService.findAllClients();
    }

    @PostMapping("/verifyLogin")
    public ResponseEntity<String> verifyLogin(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (clientService.verifyLoginInformation(username, password)) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }



}
