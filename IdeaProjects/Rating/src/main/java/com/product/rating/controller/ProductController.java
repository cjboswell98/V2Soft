package com.product.rating.controller;

import com.product.rating.domain.RatingDomain;
import com.product.rating.model.RatingModel;
import com.product.rating.services.RatingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.product.rating.services.RatingService.lowReviewsLogger;

@RestController
@RequestMapping("/reviews")
public class ProductController {

    private final RatingService ratingService;
    private final MongoTemplate mongoTemplate;
    public static final Logger lowReviewsLogger = LogManager.getLogger("lowReviews");
    private static final Logger formattedReviewLogger = LogManager.getLogger("formattedReview");


    @Value("${collection.name}")
    private String collectionName; // Inject the collection name from application.properties

    @Value("${customlog.dateformat}")
    private String customLogDateFormat; // Inject the custom log date format

    @Value("${customlog.messageformat}")
    private String customLogMessageFormat; // Inject the custom log message format

    @Autowired
    public ProductController(RatingService ratingService, MongoTemplate mongoTemplate) {
        this.ratingService = ratingService;
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("/createCollection")
    public ResponseEntity<String> createCollection(@RequestParam String collectionName) {
        try {
            ratingService.createCollection(this.collectionName); // Use the injected collection name
            return new ResponseEntity<>("Collection created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating collection: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteCollection/{collectionName}")
    public ResponseEntity<String> deleteCollection(@PathVariable String collectionName) {
        try {
            ratingService.deleteCollection(this.collectionName); // Use the injected collection name
            return new ResponseEntity<>("Collection deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting collection: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //C (R) U D
    @GetMapping("/viewReviews")
    public ResponseEntity<List<String>> viewAllReviewsFormatted() {
        List<RatingModel> ratings = ratingService.viewAllReviews();
        if (ratings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            // Create a list to store the formatted reviews
            List<String> formattedReviews = new ArrayList<>();

            // Format and log each review using the "formattedReview" logger
            for (RatingModel rating : ratings) {
                String formattedReview = String.format("%s|%s|%s|%s|%s", rating.getProductName(), rating.getFirstName(), rating.getLastName(), rating.getZipCode(), rating.getDateTime());

                // Log the formatted review using the "formattedReview" logger
                formattedReviewLogger.info(formattedReview);

                formattedReviews.add(formattedReview); // Add the formatted review to the list
            }

            // Return the list of formatted reviews
            return new ResponseEntity<>(formattedReviews, HttpStatus.OK);
        }
    }


    //C (R) U D
    @GetMapping("/viewLatestReviews")
    public ResponseEntity<List<RatingModel>> viewLatestReviews(@RequestParam(name = "limit", defaultValue = "10") int limit) {
        List<RatingModel> latestReviews = ratingService.getLatestReviews(limit);

        if (latestReviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(latestReviews, HttpStatus.OK);
        }
    }

    //C (R) U D
    @GetMapping("/viewByRateCode")
    public ResponseEntity<List<RatingModel>> viewReviewsByRateCode(@RequestParam int rateCode) {
        List<RatingModel> reviews = ratingService.findReviewsByRateCode(rateCode);

        if (reviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        }
    }

    //(C) R U D
    @PostMapping("/insertReview")
    public ResponseEntity<String> insertRating(@RequestBody RatingModel rating) {
        try {
            System.out.println("Rating is " + rating.getRateCode());

            if (rating.getRateCode() < 3) {
                lowReviewsLogger.info("Low rating inserted with RateCode: " + rating.getRateCode());
            }

            // Call the logLowRatingReview method here to log low-rated reviews
            ratingService.logLowRatingReview(rating);

            // Use the injected collection name
            return ratingService.insertRating(collectionName, rating);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert Rating: " + e.getMessage());
        }
    }


    //C R (U) D
    @PutMapping("/updateReview/{id}")
    public ResponseEntity<RatingDomain> updateReview(
            @PathVariable String id,
            @RequestBody RatingDomain updatedRating
    ) {
        return ratingService.updateReviewById(collectionName, id, updatedRating);
    }

    //C R U (D)
    @DeleteMapping("/deleteReview/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable String id) {
        boolean deleted = ratingService.deleteReviewById(collectionName, id);
        if (deleted) {
            return ResponseEntity.ok("Deleted Rating with ID: " + id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
