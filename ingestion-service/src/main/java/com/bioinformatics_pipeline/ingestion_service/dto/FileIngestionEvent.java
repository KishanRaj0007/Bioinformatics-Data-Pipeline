package com.bioinformatics_pipeline.ingestion_service.dto;

public record FileIngestionEvent(
    String fileUri,
    String sampleId,
    String fileType
) {}
