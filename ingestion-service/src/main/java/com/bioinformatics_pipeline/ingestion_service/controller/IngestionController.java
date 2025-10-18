package com.bioinformatics_pipeline.ingestion_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bioinformatics_pipeline.ingestion_service.dto.FileIngestionEvent;
import com.bioinformatics_pipeline.ingestion_service.service.IngestionService;

@RestController
@RequestMapping("/api/ingest")
@CrossOrigin(origins = "http://localhost:3000")
public class IngestionController {

    @Autowired
    private IngestionService producer;

    @PostMapping
    public void handleIngestionRequest(@RequestBody FileIngestionEvent event) {
        producer.sendMessage(event);
    }
}
