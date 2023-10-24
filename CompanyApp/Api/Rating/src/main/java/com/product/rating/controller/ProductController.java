package com.product.rating.controller;

// Import necessary packages and libraries
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

    // Service and repository dependencies
    private final ClientService clientService;
    private final ReviewService reviewService;
    private final MongoTemplate mongoTemplate;
    private final ClientRepository clientRepository;

    // Logger declarations
    public static final Logger lowReviewsLogger = LogManager.getLogger("lowReviews");
    private static final Logger formattedReviewLogger = LogManager.getLogger("formattedReview");

    // Constructor for dependency injection
    @Autowired
    public ProductController(ReviewService reviewService, MongoTemplate mongoTemplate, ClientService clientService, ClientRepository clientRepository) {
        this.reviewService = reviewService;
        this.mongoTemplate = mongoTemplate;
        this.clientService = clientService;
        this.clientRepository = clientRepository;
    }

    // Injected values from application.properties
    @Value("${collection.name}")
    private String collectionName; // Inject the collection name from application.properties

    @Value("${customlog.dateformat}")
    private String customLogDateFormat; // Inject the custom log date format

    @Value("${customlog.messageformat}")
    private String customLogMessageFormat; // Inject the custom log message format

    // REST API endpoint to create a new collection
    @PostMapping("/createCollection")
    public ResponseEntity<String> createCollection(@RequestParam String collectionName) {
        // Try to create a new collection
        try {
            reviewService.createCollection(this.collectionName); // Use the injected collection name
            return new ResponseEntity<>("Collection created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating collection: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // REST API endpoint to delete a collection
    @DeleteMapping("/deleteCollection/{collectionName}")
    public ResponseEntity<String> deleteCollection(@PathVariable String collectionName) {
        // Try to delete a collection
        try {
            reviewService.deleteCollection(this.collectionName); // Use the injected collection name
            return new ResponseEntity<>("Collection deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting collection: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // C (R) U D
    // REST API endpoint to view all reviews
    @GetMapping("/viewAllReviews")
    public ResponseEntity<List<ReviewDomain>> viewAllReviews() {
        // Retrieve all reviews
        List<ReviewDomain> ratings = reviewService.viewAllReviews();
        if (ratings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(ratings, HttpStatus.OK);
        }
    }

    // C (R) U D
    // REST API endpoint to view reviews in a specific collection
    @GetMapping("/viewReviews/{collectionName}")
    public ResponseEntity<List<ReviewDomain>> viewReviewsInCollection(@PathVariable String collectionName) {
        // Retrieve reviews from a specific collection
        List<ReviewDomain> reviews = reviewService.viewReviewsInCollection(collectionName);
        if (reviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        }
    }

    // C (R) U D
    // REST API endpoint to view the latest reviews
    @GetMapping("/viewLatestReviews")
    public ResponseEntity<List<ReviewDomain>> viewLatestReviews(@RequestParam(name = "limit", defaultValue = "10") int limit) {
        // Retrieve the latest reviews with an optional limit
        List<ReviewDomain> latestReviews = reviewService.getLatestReviews(limit);

        if (latestReviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(latestReviews, HttpStatus.OK);
        }
    }

    // C (R) U D
    // REST API endpoint to view reviews by rate code
    @GetMapping("/viewByRateCode")
    public ResponseEntity<List<ReviewDomain>> viewReviewsByRateCode(@RequestParam int rateCode) {
        // Retrieve reviews by a specific rate code
        List<ReviewDomain> reviews = reviewService.findReviewsByRateCode(rateCode);

        if (reviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        }
    }

    // (C) R U D
    // REST API endpoint to add a new review
    @PostMapping("/addReview")
    public ResponseEntity<String> addReview(@RequestBody ReviewDomain newReview) {
        // Try to add a new review
        try {
            String result = reviewService.addReview(newReview);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add review: " + e.getMessage());
        }
    }

    // C R (U) D
    // REST API endpoint to update a review
    @PutMapping("/updateReview/{id}")
    public ResponseEntity<ReviewDomain> updateReview(@PathVariable String id, @RequestBody ReviewDomain updatedRating) {
        return reviewService.updateReviewById(collectionName, id, updatedRating);
    }

    // C R U (D)
    // REST API endpoint to delete a review with authorization
    @PreAuthorize("@authorizationServiceImpl.verifyToken(#token, #clientId, 'delete')")
    @DeleteMapping("/deleteReview")
    public ResponseEntity<Object> deleteReview(@RequestHeader(name = "Authorization") String token, @RequestHeader(name = "clientId") String clientId, @RequestHeader(name = "reviewId") String reviewId) {
        return reviewService.deleteSpecificReview(clientId, reviewId);
    }

    // Below are Endpoints for Client
    // REST API endpoint to create a new client
    @PostMapping("/newClient")
    public String newClient(@RequestBody Map<String, String> request) {
        String firstName = request.get("firstName");
        String lastName = request.get("lastName");
        String username = request.get("username");
        String password = request.get("password");
        return clientService.createNewClient(firstName, lastName, username, password);
    }

    // REST API endpoint to get all clients
    @GetMapping("/viewClients")
    public List<Client> getAllClients() {
        return clientService.findAllClients();
    }

    // REST API endpoint to verify login information
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
