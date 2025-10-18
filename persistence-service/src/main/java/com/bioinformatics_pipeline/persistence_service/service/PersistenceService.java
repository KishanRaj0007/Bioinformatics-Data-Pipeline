package com.bioinformatics_pipeline.persistence_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.bioinformatics_pipeline.persistence_service.dto.VariantsCalledEvent;
import com.bioinformatics_pipeline.persistence_service.model.Sample;
import com.bioinformatics_pipeline.persistence_service.model.Variant;
import com.bioinformatics_pipeline.persistence_service.repository.SampleRepository;
import com.bioinformatics_pipeline.persistence_service.repository.VariantRepository;

@Service
public class PersistenceService {

    private static final Logger log = LoggerFactory.getLogger(PersistenceService.class);

    @Autowired
    private SampleRepository sampleRepository;
    @Autowired
    private VariantRepository variantRepository;

    @KafkaListener(topics = "variants-called", groupId = "persistence_group")
    public void handleVariantsCalledEvent(VariantsCalledEvent event) {
        log.info(String.format("Received Variants Called Event -> %s", event));

        // 1. Save the sample information
        Sample sample = new Sample(event.sampleId(), event.vcfFileUri());
        sampleRepository.save(sample);
        log.info("Saved sample metadata for: " + event.sampleId());

        // 2. For simulation, save a placeholder variant associated with this sample
        // In a real system, you would parse the VCF file to get thousands of variants
        Variant placeholderVariant = new Variant("chr1", 12345, "A", "T", List.of(event.sampleId()));
        variantRepository.save(placeholderVariant);
        log.info("Saved placeholder variant for sample: " + event.sampleId());
    }
}
