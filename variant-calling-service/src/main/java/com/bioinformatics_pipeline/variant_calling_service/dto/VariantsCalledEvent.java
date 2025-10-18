package com.bioinformatics_pipeline.variant_calling_service.dto;

public record VariantsCalledEvent(
    String vcfFileUri,
    String sampleId
) {}
