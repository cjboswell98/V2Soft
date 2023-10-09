package com.product.rating.controller;

import com.product.rating.domain.RatingDomain;
import com.product.rating.model.RatingModel;
import com.product.rating.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.product.rating.services.RatingService.lowReviewsLogger;

@RestController
@RequestMapping("/reviews")
public class ProductController {

    private final RatingService ratingService;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ProductController(RatingService ratingService, MongoTemplate mongoTemplate) {
        this.ratingService = ratingService;
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("/createCollection")
    public ResponseEntity<String> createCollection(@RequestParam String collectionName) {
        try {
            ratingService.createCollection(collectionName);
            return new ResponseEntity<>("Collection created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating collection: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteCollection/{collectionName}")
    public ResponseEntity<String> deleteCollection(@PathVariable String collectionName) {
        try {
            ratingService.deleteCollection(collectionName);
            return new ResponseEntity<>("Collection deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting collection: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //C (R) U D
    @GetMapping("/viewReviews")
    public ResponseEntity<List<RatingModel>> viewAllReviews() {
        List<RatingModel> ratings = ratingService.viewAllReviews();
        if (ratings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(ratings, HttpStatus.OK);
        }
    }

    @GetMapping("/viewLatestReviews")
    public ResponseEntity<List<RatingModel>> viewLatestReviews() {
        List<RatingModel> latestReviews = ratingService.viewLatestReviews(10); // Change 10 to the desired number of reviews to retrieve

        if (latestReviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(latestReviews, HttpStatus.OK);
        }
    }


    // (C) R U D
    @PostMapping("/insertReview")
    public ResponseEntity<String> insertRating(@RequestBody RatingModel rating) {
        try {
            String collectionName = "Reviews";
            // Your rating insertion logic here

            // Log an ERROR message for low_reviews.log if the rating is low
            if (rating.getRateCode() < 3) {
                lowReviewsLogger.info("Low rating inserted with RateCode: " + rating.getRateCode());
            }

            return ratingService.insertRating(collectionName, rating);
        } catch (Exception e) {
            // Handle exceptions if necessary
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert Rating: " + e.getMessage());
        }
    }


    @PutMapping("/updateReview/{id}")
    public ResponseEntity<RatingDomain> updateReview(
            @PathVariable String id,
            @RequestBody RatingDomain updatedRating
    ) {
        String collectionName = "Reviews"; // Collection name where reviews are stored
        return ratingService.updateReviewById(collectionName, id, updatedRating);
    }



    // C R U (D)
    @DeleteMapping("/deleteReview/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable String id) {
        String collectionName = "Reviews";
        boolean deleted = ratingService.deleteReviewById(collectionName, id);
        if (deleted) {
            return ResponseEntity.ok("Deleted Rating with ID: " + id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
