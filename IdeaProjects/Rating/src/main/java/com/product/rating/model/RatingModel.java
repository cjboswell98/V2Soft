package com.product.rating.model;

import org.springframework.data.annotation.Id;

public class RatingModel {
    @Id
    private String id;

    private String productName;
    private String firstName;
    private String lastName;
    private String zipCode;
    private int rateCode;
    private String comments;
    private String dateTime;

    public RatingModel() {
    }

    // Getters and Setters for Current Fields

    // ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    @Override
    public String toString() {
        return "RatingModel{" +
                "id='" + id + '\'' +
                ", productName='" + productName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", rateCode=" + rateCode +
                ", comments='" + comments + '\'' +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}