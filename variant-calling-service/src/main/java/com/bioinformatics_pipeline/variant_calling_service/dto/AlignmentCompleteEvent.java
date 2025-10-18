package com.bioinformatics_pipeline.variant_calling_service.dto;

public record AlignmentCompleteEvent(
    String alignedFileUri,
    String sampleId
) {}
