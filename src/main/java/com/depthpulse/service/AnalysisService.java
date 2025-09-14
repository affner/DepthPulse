package com.depthpulse.service;

import com.depthpulse.client.GetBoxClient;
import com.depthpulse.client.OptionsDepthClient;
import com.depthpulse.dto.AnalysisPayload;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Coordinates the calls to both API clients and performs a very small
 * "analysis" combining their results. The heavy lifting is deliberately
 * simulated; extensive comments describe the reasoning an intelligent model
 * could apply when merging both data streams.
 */
@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final GetBoxClient getBoxClient;
    private final OptionsDepthClient optionsDepthClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public AnalysisService(GetBoxClient getBoxClient, OptionsDepthClient optionsDepthClient) {
        this.getBoxClient = getBoxClient;
        this.optionsDepthClient = optionsDepthClient;
    }

    /**
     * Launches both API requests in parallel and combines their outputs. The
     * method itself is asynchronous so callers can simply attach callbacks and
     * keep the JavaFX thread responsive.
     */
    public CompletableFuture<AnalysisPayload> fetchBoth(String consulta, String optionId) {
        log.info("Fetching data for consulta={} optionId={}", consulta, optionId);

        // Fire both requests concurrently. Each one is executed on a worker
        // thread provided by the common ForkJoinPool. In a production
        // environment an explicit Executor could be injected for more control.
        CompletableFuture<String> getBoxFuture = CompletableFuture.supplyAsync(() -> {
            log.debug("Calling GetBox service");
            return getBoxClient.fetchRaw(consulta);
        });

        CompletableFuture<String> optionDepthFuture = CompletableFuture.supplyAsync(() -> {
            log.debug("Calling OptionDepth service");
            return optionsDepthClient.fetchRaw(optionId);
        });

        // When both complete, merge them.
        return getBoxFuture.thenCombine(optionDepthFuture, (getBoxJson, optionDepthJson) -> {
            log.info("Both API calls finished, starting simulated analysis");

            double boxValue = 0d;
            double depthValue = 0d;
            try {
                JsonNode boxNode = mapper.readTree(getBoxJson);
                JsonNode depthNode = mapper.readTree(optionDepthJson);
                boxValue = boxNode.path("boxes").asDouble(0d);
                depthValue = depthNode.path("depth").asDouble(0d);
            } catch (Exception e) {
                log.warn("Failed to parse simulated responses", e);
            }

            // -----------------------------------------------------------------
            // Here is where an advanced AI model would analyse the data:
            // 1. Normalise both signals so they are comparable.
            // 2. Detect outliers or inconsistent measurements.
            // 3. Combine temporal patterns from the depth map with spatial
            //    information from the detected boxes.
            // 4. Finally generate a probability score or textual explanation.
            // For now we keep it deliberately simple and compute an average to
            // mimic a "confidence" metric. The verbose comments above act as a
            // placeholder for the reasoning future versions may implement.
            // -----------------------------------------------------------------
            double average = (boxValue + depthValue) / 2.0;
            String analysis = "Promedio simple de boxes y depth: " + average;

            log.debug("Combined average={} (box={} depth={})", average, boxValue, depthValue);
            return new AnalysisPayload(consulta, getBoxJson, optionDepthJson, analysis);
        });
    }
}

