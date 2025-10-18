package com.bioinformatics_pipeline.alignment_service.dto;

public record FileIngestionEvent(
    String fileUri,
    String sampleId,
    String fileType
) {}
