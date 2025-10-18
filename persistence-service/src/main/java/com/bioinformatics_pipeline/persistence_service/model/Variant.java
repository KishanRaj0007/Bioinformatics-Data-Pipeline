package com.bioinformatics_pipeline.persistence_service.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document("variants")
@Data
@NoArgsConstructor
public class Variant {
    @Id
    private String id; // e.g., "chr1:12345:A:T"
    private String chromosome;
    private long position;
    private String refAllele;
    private String altAllele;
    private List<String> sampleIds; // A list of sample IDs that have this variant

    public Variant(String chromosome, long position, String refAllele, String altAllele, List<String> sampleIds) {
        this.id = String.format("%s:%d:%s:%s", chromosome, position, refAllele, altAllele);
        this.chromosome = chromosome;
        this.position = position;
        this.refAllele = refAllele;
        this.altAllele = altAllele;
        this.sampleIds = sampleIds;
    }
}
