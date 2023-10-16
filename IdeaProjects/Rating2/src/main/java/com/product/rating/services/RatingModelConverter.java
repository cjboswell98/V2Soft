package com.product.rating.services;

import com.product.rating.model.RatingModel;

public class RatingModelConverter {

    public static RatingDomainDTO convertToDto(RatingModel ratingModel) {
        RatingDomainDTO dto = new RatingDomainDTO();
        dto.setId(ratingModel.getId());
        dto.setFirstName(ratingModel.getFirstName());
        dto.setLastName(ratingModel.getLastName());
        dto.setZipCode(ratingModel.getZipCode());
        dto.setRateCode(ratingModel.getRateCode());
        dto.setComments(ratingModel.getComments());
        dto.setDateTime(ratingModel.getDateTime());
        return dto;
    }

    public static RatingModel convertToEntity(RatingDomainDTO dto) {
        RatingModel entity = new RatingModel();
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setZipCode(dto.getZipCode());
        entity.setRateCode(dto.getRateCode());
        entity.setComments(dto.getComments());
        entity.setDateTime(dto.getDateTime());
        return entity;
    }
}
