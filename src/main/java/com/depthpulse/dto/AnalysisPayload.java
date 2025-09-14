package com.depthpulse.dto;

import com.depthpulse.client.OptionsDepthClient.HeatmapPoint;
import java.util.List;

/**
 * Wrapper DTO that contains the raw results of both API clients plus
 * a simple textual summary produced by the analysis step.
 */
public record AnalysisPayload(
        TickersResponse tickers,
        List<HeatmapPoint> heatmap,
        String analysisSummary
) { }

