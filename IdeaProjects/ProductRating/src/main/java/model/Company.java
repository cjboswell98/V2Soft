package model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.processing.Generated;

@Document(collection = "companies")
public class Company {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String zipcode;
    private boolean ratingCode;
    private String reviewComments;

    // Constructors, getters, setters
//    public Company(int id, String firstName, String lastName, String zipcode, int ratingCode, String reviewComments) {}

    public Company(String id, String firstName, String lastName, String zipcode, boolean ratingCode, String reviewComments) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.zipcode = zipcode;
        this.ratingCode = ratingCode;
        this.reviewComments = reviewComments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public boolean getRatingCode() {
        return ratingCode;
    }

    public void setRatingCode(boolean ratingCode) {
        this.ratingCode = ratingCode;
    }

    public String getReviewComments() {
        return reviewComments;
    }

    public void setReviewComments(String reviewComments) {
        this.reviewComments = reviewComments;
    }
}
