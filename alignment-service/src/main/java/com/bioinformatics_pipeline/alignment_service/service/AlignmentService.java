package com.bioinformatics_pipeline.alignment_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bioinformatics_pipeline.alignment_service.dto.AlignmentCompleteEvent;
import com.bioinformatics_pipeline.alignment_service.dto.FileIngestionEvent;

@Service
public class AlignmentService {

    private static final Logger log = LoggerFactory.getLogger(AlignmentService.class);
    private static final String PRODUCER_TOPIC = "alignment-complete";

    @Autowired
    private KafkaTemplate<String, AlignmentCompleteEvent> kafkaTemplate;

    @KafkaListener(topics = "raw-file-registered", groupId = "alignment_group")
    public void handleFileRegisteredEvent(FileIngestionEvent event) {
        log.info(String.format("Received File Ingestion Event -> %s", event));

        // --- Simulate a long-running bioinformatics task ---
        log.info("Starting alignment process for sample: " + event.sampleId());
        try {
            // Simulate work being done for 5 seconds
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Alignment process finished for sample: " + event.sampleId());

        // In the next step, we will produce a new event
        String alignedFileUri = event.fileUri().replace("raw", "processed") + ".bam";
        AlignmentCompleteEvent nextEvent = new AlignmentCompleteEvent(alignedFileUri, event.sampleId());

        log.info(String.format("Producing message -> %s", nextEvent));
        kafkaTemplate.send(PRODUCER_TOPIC, nextEvent);
    }
}
