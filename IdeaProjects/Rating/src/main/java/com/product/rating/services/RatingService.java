package com.product.rating.services;

import com.product.rating.domain.RatingDomain;
import com.product.rating.model.RatingModel;
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

    @Value("${collection.name}")
    private String collectionName; // Inject the collection name from application.properties

    @Value("${customlog.dateformat}")
    private String customLogDateFormat; // Inject the custom log date format

    @Value("${customlog.messageformat}")
    private String customLogMessageFormat; // Inject the custom log message format

    @Autowired
    public RatingService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

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
            long entryCount = mongoTemplate.count(new Query(), collectionName);

            if (entryCount == 0) {
                currentId = 1;
            } else {
                currentId++;
            }

            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(customLogDateFormat); // Use the injected custom log date format
            String formattedDateTime = currentDateTime.format(formatter);
            rating.setDateTime(formattedDateTime);

            rating.setId(Integer.toString(currentId));

            mongoTemplate.insert(rating, collectionName);

            return ResponseEntity.ok("Inserted Rating with ID: " + rating.getId() + " at " + rating.getDateTime());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert Rating: " + e.getMessage());
        }
    }

    public boolean deleteReviewById(String collectionName, String id) {
        try {
            Query query = new Query(Criteria.where("_id").is(id));
            RatingModel existingRating = mongoTemplate.findOne(query, RatingModel.class, collectionName);

            if (existingRating == null) {
                return false;
            }

            mongoTemplate.remove(existingRating, collectionName);
            return true;
        } catch (Exception e) {
            lowReviewsLogger.error("Error deleting data by ID in collection {}: {}", collectionName, e.getMessage());
            return false;
        }
    }

    public ResponseEntity<RatingDomain> updateReviewById(String collectionName, String id, RatingDomain updatedRating) {
        try {
            String newId = updatedRating.getId();
            updatedRating.setId(id);
            Query query = new Query(Criteria.where("_id").is(id));
            RatingDomain existingRating = mongoTemplate.findOne(query, RatingDomain.class, collectionName);

            if (existingRating == null) {
                return ResponseEntity.notFound().build();
            }

            existingRating.setProductName(updatedRating.getProductName());
            existingRating.setFirstName(updatedRating.getFirstName());
            existingRating.setLastName(updatedRating.getLastName());
            existingRating.setRateCode(updatedRating.getRateCode());
            existingRating.setZipCode(updatedRating.getZipCode());
            existingRating.setComments(updatedRating.getComments());
            existingRating.setDateTime(updatedRating.getDateTime());

            existingRating.setId(newId);

            RatingDomain updatedReview = mongoTemplate.save(existingRating, collectionName);

            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            logger.error("Error updating data by ID in collection {}: {}", collectionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public List<RatingModel> getLatestReviews(int limit) {
        Query query = new Query()
                .with(Sort.by(Sort.Order.asc("timestamp")))
                .limit(limit);
        return mongoTemplate.find(query, RatingModel.class, collectionName);
    }


    public void logLowRatingReview(RatingModel review) {
        // Extract the rating code from the review
        int ratingCode = review.getRateCode();

        // Check if the rating code is less than 3
        if (ratingCode < 3) {
            // Log the low-rated review
            lowReviewsLogger.error("Low-rated review with rating code: " + ratingCode);
        }
    }





    public List<RatingModel> findReviewsByRateCode(int rateCode) {
        Query query = new Query(Criteria.where("rateCode").is(rateCode));
        return mongoTemplate.find(query, RatingModel.class, collectionName); // Use the injected collection name
    }


}

