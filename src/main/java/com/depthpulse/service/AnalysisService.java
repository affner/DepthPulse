package com.depthpulse.service;

import com.depthpulse.client.GetBoxClient;
import com.depthpulse.client.OptionsDepthClient;
import com.depthpulse.client.OptionsDepthClient.HeatmapPoint;
import com.depthpulse.dto.AnalysisPayload;
import com.depthpulse.dto.TickersResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service that orchestrates calls to both external clients and performs a
 * mock "AI" analysis over the combined results.
 */
@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final GetBoxClient getBoxClient;
    private final OptionsDepthClient optionsDepthClient;

    public AnalysisService(GetBoxClient getBoxClient, OptionsDepthClient optionsDepthClient) {
        this.getBoxClient = getBoxClient;
        this.optionsDepthClient = optionsDepthClient;
    }

    /**
     * Launch both API calls in parallel and perform a tiny analysis combining
     * their results.
     */
    public AnalysisPayload performAnalysis(String consulta, String optionId) {
        log.info("Launching analysis for consulta={} optionId={}", consulta, optionId);

        // Kick off both calls asynchronously. In a real scenario the parameters
        // would be forwarded to the clients. For this demo we rely on sample data.
        CompletableFuture<TickersResponse> tickersFuture =
                CompletableFuture.supplyAsync(getBoxClient::getSampleTickers);
        CompletableFuture<List<HeatmapPoint>> heatmapFuture =
                CompletableFuture.supplyAsync(optionsDepthClient::getSampleHeatmap);

        // Wait for both results to arrive.
        TickersResponse tickers = tickersFuture.join();
        List<HeatmapPoint> heatmap = heatmapFuture.join();

        // ------------------------------------------------------------------
        // Here is where a smart model would combine the data. The comments
        // illustrate possible steps that an AI/ML component could follow:
        // 1) Align both datasets in time and ticker to ensure consistency.
        // 2) Identify patterns in the heatmap (e.g., concentration of volume
        //    around certain strike prices) and compare with the list of
        //    available tickers from GetBox.
        // 3) Normalise values, detect and discard outliers, then compute
        //    statistical indicators.
        // 4) Feed the features into a predictive model to label the market as
        //    "bullish"/"bearish" or produce probability distributions.
        // ------------------------------------------------------------------
        // For demonstration purposes we only perform a trivial operation:
        double averageHeatmap = heatmap.stream()
                .mapToDouble(HeatmapPoint::getValue)
                .average()
                .orElse(0);
        String summary = String.format(
                "Promedio heatmap=%.2f sobre %d tickers", averageHeatmap,
                tickers.getStocks() != null ? tickers.getStocks().size() : 0);

        log.info("Analysis finished for consulta={} optionId={}", consulta, optionId);
        return new AnalysisPayload(tickers, heatmap, summary);
    }
}

