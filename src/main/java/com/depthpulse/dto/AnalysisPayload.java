package com.depthpulse.dto;

/**
 * Simple container for the raw responses of both APIs and the synthesized
 * analysis result.
 */
public record AnalysisPayload(String consulta,
                              String getboxJson,
                              String optionDepthJson,
                              String analysisResult) { }
