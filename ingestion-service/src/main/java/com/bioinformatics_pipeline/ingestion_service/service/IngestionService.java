package com.bioinformatics_pipeline.ingestion_service.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bioinformatics_pipeline.ingestion_service.dto.FileIngestionEvent;

@Service
public class IngestionService {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IngestionService.class);
    private static final String TOPIC = "raw-file-registered";

    @Autowired
    private KafkaTemplate<String, FileIngestionEvent> kafkaTemplate;

    public void sendMessage(FileIngestionEvent event) {
        log.info(String.format("Producing message -> %s", event));
        this.kafkaTemplate.send(TOPIC, event);
    }
}
