package com.bioinformatics_pipeline.persistence_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document("samples") // This maps the class to the "samples" collection in MongoDB
@NoArgsConstructor
@Data
public class Sample {
    @Id
    private String id;
    private String sampleId;
    private String vcfFileUri;

    // Constructors, Getters, and Setters
    public Sample(String sampleId, String vcfFileUri) {
        this.sampleId = sampleId;
        this.vcfFileUri = vcfFileUri;
    }

    // Getters and setters for all fields omitted using Lombok's @Data annotation
}
