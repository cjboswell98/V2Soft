package com.product.rating.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "reviews")
public class ReviewDomain {

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
    private List<ReviewDomain> historyList = new ArrayList<>();


    public ReviewDomain() {
    }

    public ReviewDomain(int reviewId, String clientId, String productName, String firstName, String lastName, String zipCode, int rateCode, String comments, String dateTime) {
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


    public void setHistoryList(List<ReviewDomain> historyList) {
        this.historyList.clear();
        this.historyList.addAll(historyList);
    }

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getRateCode() {
        return rateCode;
    }

    public void setRateCode(int rateCode) {
        this.rateCode = rateCode;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<ReviewDomain> getHistoryList() {
        return historyList;
    }

    public void addToHistory(ReviewDomain reviewId) {
        historyList.add(reviewId);
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
