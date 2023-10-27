package com.product.rating.services.dtosConverters;

import com.product.rating.domain.ReviewDomain;

public class ReviewDomainConverter {

    public static ReviewDomainDTO convertToDto(ReviewDomain ReviewDomain) {
        ReviewDomainDTO dto = new ReviewDomainDTO();
        dto.setReviewId(ReviewDomain.getReviewId());
        dto.setFirstName(ReviewDomain.getFirstName());
        dto.setLastName(ReviewDomain.getLastName());
        dto.setZipCode(ReviewDomain.getZipCode());
        dto.setRateCode(ReviewDomain.getRateCode());
        dto.setComments(ReviewDomain.getComments());
        dto.setDateTime(ReviewDomain.getDateTime());
        return dto;
    }

    public static ReviewDomain convertToEntity(ReviewDomainDTO dto) {
        ReviewDomain entity = new ReviewDomain();
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
