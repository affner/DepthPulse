package com.depthpulse.service;

import com.depthpulse.client.GetBoxClient;
import com.depthpulse.client.OptionsDepthClient;
import com.depthpulse.dto.AnalysisResult;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service that orchestrates calls to the two API clients and performs a
 * combined analysis of their outputs.
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
     * Launches both API calls in parallel and combines their results.
     *
     * @param ticker   symbol used for the GetBox query
     * @param optionId identifier for the options-depth query
     * @return aggregated analysis result
     */
    public AnalysisResult runAnalysis(String ticker, String optionId) {
        log.info("Starting analysis for ticker={} optionId={}", ticker, optionId);

        // Launch both requests asynchronously. The underlying client methods are
        // simple mock implementations at the moment, but the same structure will
        // allow real HTTP calls without blocking this thread.
        CompletableFuture<Double> getBoxFuture =
                CompletableFuture.supplyAsync(() -> getBoxClient.fetchDummySignal(ticker));
        CompletableFuture<Double> optionFuture =
                CompletableFuture.supplyAsync(() -> optionsDepthClient.fetchDummySignal(optionId));

        // Wait for both operations to complete.
        double getBoxValue = getBoxFuture.join();
        double optionValue = optionFuture.join();

        /*
         * Here is where a real AI model would ingest the two metrics and produce
         * a sophisticated insight. A realistic pipeline could:
         *   - Normalise the values according to historical volatility.
         *   - Detect anomalies or outliers using statistical tests.
         *   - Feed the cleaned signals into a predictive model (e.g. an LSTM
         *     network or gradient boosting machine) to forecast short term
         *     movements.
         *   - Quantify the confidence of the prediction and cross-check it with
         *     recent market regime classifiers.
         * For demonstration purposes we implement only a tiny fraction of this
         * reasoning by averaging the two inputs.
         */
        double average = (getBoxValue + optionValue) / 2.0;
        String conclusion = average > 5
                ? "Bullish bias detected"
                : "Bearish or neutral bias";

        log.info("Analysis complete for ticker={} optionId={}", ticker, optionId);
        return new AnalysisResult(getBoxValue, optionValue,
                String.format("avg=%.2f -> %s", average, conclusion));
    }
}
