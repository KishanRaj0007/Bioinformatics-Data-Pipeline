package com.bioinformatics_pipeline.variant_calling_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bioinformatics_pipeline.variant_calling_service.dto.AlignmentCompleteEvent;
import com.bioinformatics_pipeline.variant_calling_service.dto.VariantsCalledEvent;

@Service
public class VariantCallingService {
    private static final Logger log = LoggerFactory.getLogger(VariantCallingService.class);
    private static final String PRODUCER_TOPIC = "variants-called";

    @Autowired
    private KafkaTemplate<String, VariantsCalledEvent> kafkaTemplate;

    @KafkaListener(topics = "alignment-complete", groupId = "variant_calling_group")
    public void handleAlignmentCompleteEvent(AlignmentCompleteEvent event) {
        log.info(String.format("Received Alignment Complete Event -> %s", event));

        log.info("Starting variant calling process for sample: " + event.sampleId());
        try {
            Thread.sleep(7000); // Simulate a longer task
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Variant calling process finished for sample: " + event.sampleId());

        String vcfFileUri = event.alignedFileUri().replace(".bam", ".vcf");
        VariantsCalledEvent nextEvent = new VariantsCalledEvent(vcfFileUri, event.sampleId());

        log.info(String.format("Producing message -> %s", nextEvent));
        kafkaTemplate.send(PRODUCER_TOPIC, nextEvent);
    }
}
