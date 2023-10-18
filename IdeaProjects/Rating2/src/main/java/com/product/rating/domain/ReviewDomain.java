package com.product.rating.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "reviews")
public class RatingDomain {
    private String clientId;
    @Id
    private String reviewId;
    private String productName;
    private String firstName;
    private String lastName;
    private String zipCode;
    private int rateCode;
    private String comments;
    private String dateTime;

    @Field("historyList")
    private final List<RatingDomain> historyList = new ArrayList<>();



    public RatingDomain() {
    }

    public RatingDomain(String reviewId, String clientId, String productName, String firstName, String lastName, String zipCode, int rateCode, String comments, String dateTime) {
        this.reviewId = reviewId;
        this.clientId = clientId;
        this.productName = productName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.zipCode = zipCode;
        this.rateCode = rateCode;
        this.comments = comments;
        this.dateTime = dateTime;
    }


    public void setHistoryList(List<RatingDomain> historyList) {
        this.historyList.clear();
        this.historyList.addAll(historyList);
    }
    // ID

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
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
    public List<RatingDomain> getHistoryList() {
        return historyList;
    }

    public void addToHistory() {
        RatingDomain newVersion = new RatingDomain(reviewId, clientId, productName, firstName, lastName, zipCode, rateCode, comments, dateTime);
        historyList.add(newVersion);
    }

    @Override
    public String toString() {
        return "RatingDomain{" +
                "clientId='" + clientId + '\'' +
                ", reviewId='" + reviewId + '\'' +
                ", productName='" + productName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", rateCode=" + rateCode +
                ", comments='" + comments + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", historyList=" + historyList +
                '}';
    }
}
