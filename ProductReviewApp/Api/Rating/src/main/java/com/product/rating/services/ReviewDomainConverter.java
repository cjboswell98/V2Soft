package com.product.rating.services;

import com.product.rating.domain.Review;

public class ReviewDomainConverter {

    public static ReviewDomainDTO convertToDto(Review Review) {
        ReviewDomainDTO dto = new ReviewDomainDTO();
        dto.setReviewId(Review.getReviewId());
        dto.setFirstName(Review.getFirstName());
        dto.setLastName(Review.getLastName());
        dto.setZipCode(Review.getZipCode());
        dto.setRateCode(Review.getRateCode());
        dto.setComments(Review.getComments());
        dto.setDateTime(Review.getDateTime());
        return dto;
    }

    public static Review convertToEntity(ReviewDomainDTO dto) {
        Review entity = new Review();
        entity.setReviewId(dto.getReviewId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setZipCode(dto.getZipCode());
        entity.setRateCode(dto.getRateCode());
        entity.setComments(dto.getComments());
        entity.setDateTime(dto.getDateTime());
        return entity;
    }
}
