package com.product.rating.services;

import com.product.rating.domain.RatingDomain;
import com.product.rating.model.RatingModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RatingService {

    private static final Logger logger = LogManager.getLogger(RatingService.class);
    public static final Logger lowReviewsLogger = LogManager.getLogger("lowReviews");
    private final MongoTemplate mongoTemplate;
    private int currentId = 1;

    @Autowired
    public RatingService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void captureLowRatingReview(RatingModel rating) {
        int ratingCode = getRatingCodeFromReview(rating);
        if (ratingCode < 3) {
            // Log the low rating review using the "lowReviews" logger
            lowReviewsLogger.error("Low rating review received: {}", getReviewText(rating));
        } else {
            // Log a regular info message
            logger.info("Review received: {}", getReviewText(rating));
        }
    }
    public void logCustomRecord(String firstName, String lastName, String zipCode) {
        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);

        // Create the custom log message in the desired format
        String logMessage = "iConnectX|" + firstName + "|" + lastName + "|" + zipCode + "|" + formattedDateTime;

        // Log the custom record
        logger.info(logMessage);
    }


    public int getRatingCodeFromReview(RatingModel rating) {
        // Replace this logic with how you actually retrieve the rating code from the RatingModel
        return rating.getRateCode();
    }

    public String getReviewText(RatingModel rating) {
        // Replace this logic with how you actually retrieve the review text from the RatingModel
        return rating.getComments();
    }

    // Rest of your methods...

    public void createCollection(String collectionName) {
        try {
            mongoTemplate.createCollection(collectionName);
            logger.info("Created collection: {}", collectionName);
        } catch (Exception e) {
            logger.error("Error creating collection {}: {}", collectionName, e.getMessage());
            // Handle exceptions or rethrow them
        }
    }

    public void deleteCollection(String collectionName) {
        try {
            mongoTemplate.dropCollection(collectionName);
            logger.info("Deleted collection: {}", collectionName);
        } catch (Exception e) {
            logger.error("Error deleting collection {}: {}", collectionName, e.getMessage());
            // Handle exceptions or rethrow them
        }
    }

    public List<RatingModel> viewAllReviews() {
        List<RatingModel> result = new ArrayList<>();

        Set<String> collectionNames = mongoTemplate.getCollectionNames();

        for (String collectionName : collectionNames) {
            try {
                logger.info("Fetching data from collection: {}", collectionName);
                Query query = new Query();

                List<RatingModel> collectionData = mongoTemplate.find(query, RatingModel.class, collectionName);
                result.addAll(collectionData);
            } catch (Exception e) {
                logger.error("Error fetching data from collection {}: {}", collectionName, e.getMessage());
            }
        }
        return result;
    }

    public ResponseEntity<String> insertRating(String collectionName, RatingModel rating) {
        try {
            // Check the number of existing entries in the collection
            long entryCount = mongoTemplate.count(new Query(), collectionName);

            if (entryCount == 0) {
                // If there are no existing entries, reset the ID to 1
                currentId = 1;
            } else {
                // If there are existing entries, increment the ID
                currentId++;
            }

            // Set the current date and time as a formatted String
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            rating.setDateTime(formattedDateTime);

            // Set the ID for the new entry
            rating.setId(Integer.toString(currentId));

            // Insert the rating into the specified collection
            mongoTemplate.insert(rating, collectionName);

            // Data inserted successfully
            return ResponseEntity.ok("Inserted Rating with ID: " + rating.getId() + " at " + rating.getDateTime());
        } catch (Exception e) {
            // Handle exceptions if necessary
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert Rating: " + e.getMessage());
        }
    }

    public boolean deleteReviewById(String collectionName, String id) {
        try {
            // Create a query to find the document by the current ID
            Query query = new Query(Criteria.where("_id").is(id));

            // Find the existing document in the specified collection
            RatingModel existingRating = mongoTemplate.findOne(query, RatingModel.class, collectionName);

            if (existingRating == null) {
                return false; // Return false if the document is not found
            }

            // Delete the document
            mongoTemplate.remove(existingRating, collectionName);

            return true; // Return true if the deletion is successful
        } catch (Exception e) {
            logger.error("Error deleting data by ID in collection {}: {}", collectionName, e.getMessage());
            return false; // Return false if there's an error during deletion
        }
    }

    public ResponseEntity<RatingDomain> updateReviewById(String collectionName, String id, RatingDomain updatedRating) {
        try {
            // Extract the new ID from the updatedRating object
            String newId = updatedRating.getId();

            // Update the ID in the existingRating object
            updatedRating.setId(id);

            // Create a query to find the document by the current ID
            Query query = new Query(Criteria.where("_id").is(id));

            // Find the existing document in the specified collection
            RatingDomain existingRating = mongoTemplate.findOne(query, RatingDomain.class, collectionName);

            if (existingRating == null) {
                return ResponseEntity.notFound().build(); // Return a not found response
            }

            // Update the fields with the new values (excluding the ID)
            existingRating.setProductName(updatedRating.getProductName());
            existingRating.setFirstName(updatedRating.getFirstName());
            existingRating.setLastName(updatedRating.getLastName());
            existingRating.setRateCode(updatedRating.getRateCode());
            existingRating.setZipCode(updatedRating.getZipCode());
            existingRating.setComments(updatedRating.getComments());
            existingRating.setDateTime(updatedRating.getDateTime());

            // Update the ID with the new ID
            existingRating.setId(newId);

            // Save the updated document back to the collection
            RatingDomain updatedReview = mongoTemplate.save(existingRating, collectionName);

            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            logger.error("Error updating data by ID in collection {}: {}", collectionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return an internal server error response
        }
    }

    public List<RatingModel> viewLatestReviews(int limit) {
        // Create a query to retrieve the latest reviews based on a timestamp field
        Query query = new Query().with(Sort.by(Sort.Order.desc("timestamp"))).limit(limit);

        // Use the MongoTemplate to execute the query and retrieve the reviews

        return mongoTemplate.find(query, RatingModel.class);
    }
}
