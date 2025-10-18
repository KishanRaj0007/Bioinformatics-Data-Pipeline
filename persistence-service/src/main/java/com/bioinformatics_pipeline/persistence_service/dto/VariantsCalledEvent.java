package com.bioinformatics_pipeline.persistence_service.dto;

public record VariantsCalledEvent(
    String vcfFileUri,
    String sampleId
) {}
