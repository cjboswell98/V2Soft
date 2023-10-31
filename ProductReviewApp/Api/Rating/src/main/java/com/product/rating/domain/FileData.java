package com.product.rating.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document("FILE_DATA")
@Builder
public class FileData {

    @Id
    private int id;

    private String name;
    private String type;
    private String filePath;
}
