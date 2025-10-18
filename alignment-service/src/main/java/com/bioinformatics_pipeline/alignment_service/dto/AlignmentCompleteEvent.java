package com.bioinformatics_pipeline.alignment_service.dto;

public record AlignmentCompleteEvent(
    String alignedFileUri,
    String sampleId
) {}
