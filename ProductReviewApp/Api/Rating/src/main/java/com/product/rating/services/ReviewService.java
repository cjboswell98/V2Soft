package com.product.rating.services;

import com.product.rating.domain.Client;
import com.product.rating.domain.FileData;
import com.product.rating.domain.Image;
import com.product.rating.domain.Review;
import com.product.rating.repository.ClientRepository;
import com.product.rating.repository.FileDataRepository;
import com.product.rating.repository.ImageRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;


@Service
public class ReviewService {

    private static final Logger logger = LogManager.getLogger(ReviewService.class);
    public static final Logger lowReviewsLogger = LogManager.getLogger("lowReviews");
    private final MongoTemplate mongoTemplate;
    private final ClientRepository clientRepository;
    private final ReviewRepository reviewRepository;
    private final String FOLDER_PATH = "C:\\Users\\cboswell\\OneDrive - V2SOFT INC\\Desktop\\Images";

    @Autowired
    private FileDataRepository fileDataRepository;
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    public ReviewService(ClientRepository clientRepository, MongoTemplate mongoTemplate, ReviewRepository reviewRepository) {
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

    public List<Review> viewAllReviews() {
        List<Review> result = new ArrayList<>();
        Set<String> collectionNames = mongoTemplate.getCollectionNames();

        for (String collectionName : collectionNames) {
            try {
                logger.info("Fetching data from collection: {}", collectionName);
                Query query = new Query();
                List<Review> collectionData = mongoTemplate.find(query, Review.class, collectionName);
                result.addAll(collectionData);
            } catch (Exception e) {
                logger.error("Error fetching data from collection {}: {}", collectionName, e.getMessage());
            }
        }
        return result;
    }

    public List<Review> viewReviewsInCollection(String collectionName) {
        try {
            logger.info("Fetching data from collection: {}", collectionName);
            Query query = new Query();
            List<Review> collectionData = mongoTemplate.find(query, Review.class, collectionName);
            return collectionData;
        } catch (Exception e) {
            logger.error("Error fetching data from collection {}: {}", collectionName, e.getMessage());
            return new ArrayList<>(); // Return an empty ArrayList instead of Collections.emptyList()
        }
    }

    private int reviewCounter = 1; // Initialize the counter

    public String addReview(Review newReview) {
        try {
            List<Client> clients = clientRepository.findAll(Sort.by(Sort.Direction.DESC, "clientId"));
            List<Review> reviews = reviewRepository.findAll(Sort.by(Sort.Direction.DESC, "reviewId"));

            if (!clients.isEmpty()) {
                String clientId = clients.get(0).getClientId();
                newReview.setClientId(clientId);
            } else {
                System.out.println("No clients found. Setting clientId for the first review.");
            }

            if (!reviews.isEmpty()) {
                int lastReviewId = reviews.get(0).getReviewId();
                reviewCounter = Math.max(reviewCounter, lastReviewId + 1);
            } else {
                System.out.println("Reviews list is empty. Resetting counter to 1.");
                reviewCounter = 1; // Reset the counter to 1
            }

            newReview.setReviewId(reviewCounter);
            reviewRepository.save(newReview);

            reviewCounter++; // Increment the counter

            return "Review added successfully with reviewId: " + newReview.getReviewId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to add review: " + e.getMessage());
        }
    }



    public String uploadImage(MultipartFile file) throws IOException { // Corrected method name
        String imageId= UUID.randomUUID().toString();
        Image imageData = imageRepository.save(Image.builder()
                        .id(imageId)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes())).build());

        if (imageData != null) {
            return imageId;
        }

        return null;
    }



    public byte[] downloadImage(String fileName) {
        Query query = new Query(Criteria.where("name").is(fileName));
        query.limit(1); // Limit the result to one document

        List<Image> dbImages = mongoTemplate.find(query, Image.class, collectionName);

        if (!dbImages.isEmpty()) {
            Image firstImage = dbImages.get(0);
            return ImageUtils.decompressImage(firstImage.getImageData());
        } else {
            // Handle the case where no images with the specified name were found
            return null; // You can return null or handle it based on your requirements.
        }
    }



    public String uploadImagetoFileSystem(MultipartFile file) throws IOException {
        String filePath = FOLDER_PATH + file.getOriginalFilename();

        FileData fileData = fileDataRepository.save(FileData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(filePath).build());

        file.transferTo(new File(filePath));

        if (fileData != null) {
            return "file uploaded successfully: " + filePath;
        }

        return null;
    }

   public void deleteReview(int reviewId) {
        reviewRepository.deleteByReviewId(reviewId);
   }
    public ResponseEntity<Review> updateReviewById(String collectionName, String id, Review updatedReview) {
        try {
            Query query = new Query(Criteria.where("_id").is(id));
            Review existingReview = mongoTemplate.findOne(query, Review.class, collectionName);

            if (existingReview == null) {
                return ResponseEntity.notFound().build();
            }

            logger.info("Updating Review with ID: {}", id);

            // Add the existingReview to the historyList before updating it
            existingReview.addToHistory(existingReview);

            // Update the existingReview with the properties from updatedReview
            existingReview.setProductName(updatedReview.getProductName());
            existingReview.setFirstName(updatedReview.getFirstName());
            existingReview.setLastName(updatedReview.getLastName());
            existingReview.setZipCode(updatedReview.getZipCode());
            existingReview.setRateCode(updatedReview.getRateCode());
            existingReview.setComments(updatedReview.getComments());
            existingReview.setDateTime(updatedReview.getDateTime());

            // Save the updated existingReview to replace the existing document
            mongoTemplate.save(existingReview, collectionName);

            logger.info("Review with ID {} updated successfully");

            return ResponseEntity.ok(existingReview);
        } catch (Exception e) {
            logger.error("Error updating data by ID in collection {}: {}", collectionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public List<Review> getLatestReviews(int limit) {
        Query query = new Query()
                .with(Sort.by(Sort.Order.asc("_id")))
                .limit(limit);
        return mongoTemplate.find(query, Review.class, collectionName);
    }
//datetime

    public void logLowRatingReview(Review review) {
        // Extract the rating code from the review
        int ratingCode = review.getRateCode();

        // Check if the rating code is less than 3
        if (ratingCode < 3) {
            // Log the low-rated review
            lowReviewsLogger.info("Low-rated review with rating code: " + ratingCode);
        }
    }

    public List<Review> findReviewsByRateCode(int rateCode) {
        Query query = new Query(Criteria.where("rateCode").is(rateCode));
        return mongoTemplate.find(query, Review.class, collectionName); // Use the injected collection name
    }


}

