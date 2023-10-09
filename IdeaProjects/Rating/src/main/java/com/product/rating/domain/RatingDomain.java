package com.product.rating.domain;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class RatingDomain {
    @Id
    private String id;

    private String productName;
    private String firstName;
    private String lastName;
    private String zipCode;
    private int rateCode;
    private String comments;
    private String dateTime;

    private History history = new History(); // Create a history object

    public RatingDomain() {}

    // Getters and Setters (Including Current Fields and History Fields)

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
        if (isDifferent(this.productName, productName)) {
            history.getProductNameHistory().add(this.productName);
        }
        this.productName = productName;
    }

    // First Name
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (isDifferent(this.firstName, firstName)) {
            history.getFirstNameHistory().add(this.firstName);
        }
        this.firstName = firstName;
    }

    // Last Name
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (isDifferent(this.lastName, lastName)) {
            history.getLastNameHistory().add(this.lastName);
        }
        this.lastName = lastName;
    }

    // Zip Code
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        if (isDifferent(this.zipCode, zipCode)) {
            history.getZipCodeHistory().add(this.zipCode);
        }
        this.zipCode = zipCode;
    }

    // Rate Code
    public int getRateCode() {
        return rateCode;
    }

    public void setRateCode(int rateCode) {
        if (this.rateCode != rateCode) {
            history.getRateCodeHistory().add(this.rateCode);
        }
        this.rateCode = rateCode;
    }

    // Comments
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        if (isDifferent(this.comments, comments)) {
            history.getCommentsHistory().add(this.comments);
        }
        this.comments = comments;
    }

    // Date Time
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        if (isDifferent(this.dateTime, dateTime)) {
            history.getDateTimeHistory().add(this.dateTime);
        }
        this.dateTime = dateTime;
    }

    // Get the history object
    public History getHistory() {
        return history;
    }

    // Helper Method to Check if Two Strings are Different
    private boolean isDifferent(String currentValue, String newValue) {
        return (currentValue != null && !currentValue.equals(newValue));
    }

    @Override
    public String toString() {
        return "RatingDomain{" +
                "id='" + id + '\'' +
                ", productName='" + productName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", rateCode=" + rateCode +
                ", comments='" + comments + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", history=" + history +
                '}';
    }

    public class History {
        private List<String> productNameHistory = new ArrayList<>();
        private List<String> firstNameHistory = new ArrayList<>();
        private List<String> lastNameHistory = new ArrayList<>();
        private List<String> zipCodeHistory = new ArrayList<>();
        private List<Integer> rateCodeHistory = new ArrayList<>();
        private List<String> commentsHistory = new ArrayList<>();
        private List<String> dateTimeHistory = new ArrayList<>();

        public List<String> getProductNameHistory() {
            return productNameHistory;
        }

        public List<String> getFirstNameHistory() {
            return firstNameHistory;
        }

        public List<String> getLastNameHistory() {
            return lastNameHistory;
        }

        public List<String> getZipCodeHistory() {
            return zipCodeHistory;
        }

        public List<Integer> getRateCodeHistory() {
            return rateCodeHistory;
        }

        public List<String> getCommentsHistory() {
            return commentsHistory;
        }

        public List<String> getDateTimeHistory() {
            return dateTimeHistory;
        }

        // Add history entries for each field
        public void addProductNameHistory(String entry) {
            productNameHistory.add(entry);
        }

        public void addFirstNameHistory(String entry) {
            firstNameHistory.add(entry);
        }

        public void addLastNameHistory(String entry) {
            lastNameHistory.add(entry);
        }

        public void addZipCodeHistory(String entry) {
            zipCodeHistory.add(entry);
        }

        public void addRateCodeHistory(Integer entry) {
            rateCodeHistory.add(entry);
        }

        public void addCommentsHistory(String entry) {
            commentsHistory.add(entry);
        }

        public void addDateTimeHistory(String entry) {
            dateTimeHistory.add(entry);
        }
    }


}
