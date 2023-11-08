// Java Program to Illustrate Creation Of
// Service Interface

package com.product.rating.email;

// Importing required classes


import com.product.rating.domain.EmailDetails;

// Interface
public interface EmailService {

    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);

    // Method
    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);
}
