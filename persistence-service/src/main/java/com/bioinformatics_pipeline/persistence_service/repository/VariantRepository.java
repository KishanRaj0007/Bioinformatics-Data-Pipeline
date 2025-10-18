package com.bioinformatics_pipeline.persistence_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bioinformatics_pipeline.persistence_service.model.Variant;

public interface VariantRepository extends MongoRepository<Variant, String> {
    
}
