package com.product.rating.controller;

// Import necessary packages and libraries
import com.product.rating.domain.Client;
import com.product.rating.domain.Image;
import com.product.rating.domain.Review;
import com.product.rating.repository.ClientRepository;
import com.product.rating.services.ClientService;
import com.product.rating.services.ReviewService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<List<Review>> viewAllReviews() {
        // Retrieve all reviews
        List<Review> ratings = reviewService.viewAllReviews();
        if (ratings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(ratings, HttpStatus.OK);
        }
    }

    // C (R) U D
    // REST API endpoint to view reviews in a specific collection
    @GetMapping("/viewReviews/{collectionName}")
    public ResponseEntity<List<Review>> viewReviewsInCollection(@PathVariable String collectionName) {
        // Retrieve reviews from a specific collection
        List<Review> reviews = reviewService.viewReviewsInCollection(collectionName);
        if (reviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        }
    }

    // C (R) U D
    // REST API endpoint to view the latest reviews
    @GetMapping("/viewLatestReviews")
    public ResponseEntity<List<Review>> viewLatestReviews(@RequestParam(name = "limit", defaultValue = "10") int limit) {
        // Retrieve the latest reviews with an optional limit
        List<Review> latestReviews = reviewService.getLatestReviews(limit);

        if (latestReviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(latestReviews, HttpStatus.OK);
        }
    }

    // C (R) U D
    // REST API endpoint to view reviews by rate code
    @GetMapping("/viewByRateCode")
    public ResponseEntity<List<Review>> viewReviewsByRateCode(@RequestParam int rateCode) {
        // Retrieve reviews by a specific rate code
        List<Review> reviews = reviewService.findReviewsByRateCode(rateCode);

        if (reviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        }
    }

    // (C) R U D
    // REST API endpoint to add a new review
    @PostMapping("/addReview")
    public ResponseEntity<String> addReview(@RequestBody Review newReview) {
        // Try to add a new review
        try {
            String result = reviewService.addReview(newReview);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add review: " + e.getMessage());
        }
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("image")MultipartFile file) throws IOException {
       String uploadImage = reviewService.uploadImage(file);
       return ResponseEntity.status(HttpStatus.OK)
               .body(uploadImage);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName) {
        byte[] imageData = reviewService.downloadImage(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }

    // C R (U) D
    // REST API endpoint to update a review
    @PutMapping("/updateReview/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable String id, @RequestBody Review updatedRating) {
        return reviewService.updateReviewById(collectionName, id, updatedRating);
    }

    // C R U (D)
    // REST API endpoint to delete a review with authorization
    @DeleteMapping("/deleteReview/{reviewId}")
    public void deleteReview(@PathVariable("reviewId") int reviewId) {
        reviewService.deleteReview(reviewId);
    }

    // Below are Endpoints for Client
    // REST API endpoint to create a new client
    @PostMapping("/newClient")
    public String newClient(@RequestBody Map<String, String> request) {
        String firstName = request.get("firstName");
        String lastName = request.get("lastName");
        String username = request.get("username");
        String password = request.get("password");
        String role = request.get("role");
        return clientService.createNewClient(firstName, lastName,username, password, role);
    }

    // REST API endpoint to get all clients
    @GetMapping("/viewClients")
    public List<Client> getAllClients() {
        return clientService.findAllClients();
    }

    // REST API endpoint to verify login information
    @PostMapping("/verifyLogin")
    public ResponseEntity<String> verifyLogin(@RequestBody Map<String, String> request) {
        String firstName = request.get("firstName");
        String lastName = request.get("lastName");
        String username = request.get("username");
        String password = request.get("password");
        String role = request.get("role");

        if (clientService.verifyLoginInformation(firstName, lastName, username, password, role)) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
