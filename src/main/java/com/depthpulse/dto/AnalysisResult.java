package com.depthpulse.dto;

/**
 * Simple DTO returned by the analysis service. It exposes the individual
 * signals coming from each remote API and a textual explanation of the
 * aggregated outcome.
 */
public record AnalysisResult(double getBoxSignal,
                             double optionDepthSignal,
                             String conclusion) {
}
