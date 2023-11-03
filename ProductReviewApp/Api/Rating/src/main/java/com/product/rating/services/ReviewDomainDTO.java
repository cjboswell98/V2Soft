package com.product.rating.services;

import com.product.rating.domain.Review;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

public class ReviewDomainDTO {
    @Id
    private int reviewId;
    private String clientId;
    private String productName;
    private String firstName;
    private String lastName;
    private String zipCode;
    private int rateCode;
    private String comments;
    private String dateTime;

    @Field("historyList")
    private final List<Review> historyList = new ArrayList<>();

    @Field("images")
    private final String reviewImage = new String();

    // Getters and Setters (Including Current Fields)


    public void setHistoryList(List<Review> historyList) {
        this.historyList.clear();
        this.historyList.addAll(historyList);
    }
    // ID

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    // Product Name
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    // First Name
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Last Name
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Zip Code
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    // Rate Code
    public int getRateCode() {
        return rateCode;
    }

    public void setRateCode(int rateCode) {
        this.rateCode = rateCode;
    }

    // Comments
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // Date Time
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    // Get the history list
    public List<Review> getHistoryList() {
        return historyList;
    }

    public void addToHistory() {
        Review newVersion = new Review(reviewId, clientId,  productName, firstName, lastName, zipCode, rateCode, comments, dateTime);
        historyList.add(newVersion);
    }

    @Override
    public String toString() {
        return "RatingDomain{" +
                "reviewId='" + reviewId + '\'' +
                ", productName='" + productName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", rateCode=" + rateCode +
                ", comments='" + comments + '\'' +
                ", dateTime='" + dateTime + '\'' ;
    }
}
