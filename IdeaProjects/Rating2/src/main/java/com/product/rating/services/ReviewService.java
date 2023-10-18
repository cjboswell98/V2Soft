package com.product.rating.services;

import com.product.rating.domain.Client;
import com.product.rating.domain.ReviewDomain;
import com.product.rating.repository.ClientRepository;
import com.product.rating.repository.ReviewRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RatingService {

    private static final Logger logger = LogManager.getLogger(RatingService.class);
    public static final Logger lowReviewsLogger = LogManager.getLogger("lowReviews");
    private final MongoTemplate mongoTemplate;
    private final ClientRepository clientRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public RatingService(ClientRepository clientRepository, MongoTemplate mongoTemplate, ReviewRepository reviewRepository) {
        this.clientRepository = clientRepository;
        this.mongoTemplate = mongoTemplate;
        this.reviewRepository = reviewRepository;
    }

    @Value("${collection.name}")
    private String collectionName; // Inject the collection name from application.properties

    @Value("${customlog.dateformat}")
    private String customLogDateFormat; // Inject the custom log date format

    @Value("${customlog.messageformat}")
    private String customLogMessageFormat; // Inject the custom log message format


    public void createCollection(String collectionName) {
        try {
            mongoTemplate.createCollection(this.collectionName); // Use the injected collection name
            logger.info("Created collection: {}", this.collectionName); // Use the injected collection name
        } catch (Exception e) {
            logger.error("Error creating collection {}: {}", this.collectionName, e.getMessage()); // Use the injected collection name
        }
    }

    public void deleteCollection(String collectionName) {
        try {
            mongoTemplate.dropCollection(collectionName);
            logger.info("Deleted collection: {}", collectionName);
        } catch (Exception e) {
            logger.error("Error deleting collection {}: {}", collectionName, e.getMessage());
        }
    }

    public List<ReviewDomain> viewAllReviews() {
        List<ReviewDomain> result = new ArrayList<>();
        Set<String> collectionNames = mongoTemplate.getCollectionNames();

        for (String collectionName : collectionNames) {
            try {
                logger.info("Fetching data from collection: {}", collectionName);
                Query query = new Query();
                List<ReviewDomain> collectionData = mongoTemplate.find(query, ReviewDomain.class, collectionName);
                result.addAll(collectionData);
            } catch (Exception e) {
                logger.error("Error fetching data from collection {}: {}", collectionName, e.getMessage());
            }
        }
        return result;
    }

    public List<ReviewDomain> viewReviewsInCollection(String collectionName) {
        try {
            logger.info("Fetching data from collection: {}", collectionName);
            Query query = new Query();
            List<ReviewDomain> collectionData = mongoTemplate.find(query, ReviewDomain.class, collectionName);
            return collectionData;
        } catch (Exception e) {
            logger.error("Error fetching data from collection {}: {}", collectionName, e.getMessage());
            return Collections.emptyList();
        }
    }

    public String addReview(ReviewDomain newReview) {
        try {
            List<Client> clients = clientRepository.findAll(Sort.by(Sort.Direction.DESC, "clientId"));
            if (clients.isEmpty()) {
                throw new NoSuchElementException("No clients found in the collection");
            }
            String clientId = clients.get(0).getClientId(); // Fetch the most recent client ID
            newReview.setClientId(clientId);
            newReview.setReviewId(UUID.randomUUID().toString()); // Generate a random ID for the review

            // Any additional logic or validation before adding the review can be added here

            // Assuming you have the necessary repository injected
            // Add the new review to the collection
            reviewRepository.save(newReview);

            return "Review added successfully with reviewId: " + newReview.getReviewId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to add review: " + e.getMessage());
        }
    }

//test by token

//    public ResponseEntity<String> insertRating(String collectionName, RatingDomain rating, String clientId) {
//        try {
//            LocalDateTime currentDateTime = LocalDateTime.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(customLogDateFormat); // Use the injected custom log date format
//            String formattedDateTime = currentDateTime.format(formatter);
//            rating.setDateTime(formattedDateTime);
//
//            rating.setReviewId(clientId); // Set the client ID as the ID for the rating
//
//            mongoTemplate.insert(rating, collectionName);
//
//            return ResponseEntity.ok("Inserted Rating with ID: " + rating.getReviewId() + " for Client ID: " + clientId + " at " + rating.getDateTime());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert Rating: " + e.getMessage());
//        }
//    }


    public boolean deleteReviewById(String collectionName, String reviewId) {
        try {
            // Deleting from the first collection
            Query ratingQuery = new Query(Criteria.where("_id").is(reviewId)); // Change the field to "_id"
            ReviewDomain existingRating = mongoTemplate.findOne(ratingQuery, ReviewDomain.class, collectionName);

            if (existingRating != null) {
                mongoTemplate.remove(existingRating, collectionName);
            }

            // No need to delete from the second collection based on reviewId

            return true;
        } catch (Exception e) {
            lowReviewsLogger.error("Error deleting data by ID: {}", e.getMessage());
            return false;
        }
    }

    public ResponseEntity<ReviewDomain> updateReviewById(String collectionName, String id, ReviewDomain updatedRating) {
        try {
            Query query = new Query(Criteria.where("_id").is(id));
            ReviewDomain existingRating = mongoTemplate.findOne(query, ReviewDomain.class, collectionName);

            if (existingRating == null) {
                return ResponseEntity.notFound().build();
            }

            logger.info("Updating Rating with ID: {}", id);

            // Add the existingRating to the historyList before updating it
            existingRating.addToHistory();

            // Update the existingRating with the properties from updatedRating
            existingRating.setProductName(updatedRating.getProductName());
            existingRating.setFirstName(updatedRating.getFirstName());
            existingRating.setLastName(updatedRating.getLastName());
            existingRating.setZipCode(updatedRating.getZipCode());
            existingRating.setRateCode(updatedRating.getRateCode());
            existingRating.setComments(updatedRating.getComments());
            existingRating.setDateTime(updatedRating.getDateTime());

            // Save the updated existingRating to replace the existing document
            mongoTemplate.save(existingRating, collectionName);

            logger.info("Rating with ID {} updated successfully");

            return ResponseEntity.ok(existingRating);
        } catch (Exception e) {
            logger.error("Error updating data by ID in collection {}: {}", collectionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public List<ReviewDomain> getLatestReviews(int limit) {
        Query query = new Query()
                .with(Sort.by(Sort.Order.asc("_id")))
                .limit(limit);
        return mongoTemplate.find(query, ReviewDomain.class, collectionName);
    }
//datetime

    public void logLowRatingReview(ReviewDomain review) {
        // Extract the rating code from the review
        int ratingCode = review.getRateCode();

        // Check if the rating code is less than 3
        if (ratingCode < 3) {
            // Log the low-rated review
            lowReviewsLogger.info("Low-rated review with rating code: " + ratingCode);
        }
    }

    public List<ReviewDomain> findReviewsByRateCode(int rateCode) {
        Query query = new Query(Criteria.where("rateCode").is(rateCode));
        return mongoTemplate.find(query, ReviewDomain.class, collectionName); // Use the injected collection name
    }


}

