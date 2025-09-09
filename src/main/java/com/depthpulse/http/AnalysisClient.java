package com.depthpulse.http;

import com.depthpulse.model.AnalysisResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Handles async calls to external APIs (OptionsDepth y Gexbot) and a local
 * analysis placeholder.
 */
public class AnalysisClient {

    private final HttpClient client;
    private final ObjectMapper mapper;

    public AnalysisClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.mapper = new ObjectMapper();
    }

    /**
     * Orchestrates fetching data from the two APIs and running a simulated
     * local analysis. All operations occur asynchronously.
     */
    public CompletableFuture<AnalysisResponse> runAnalysis(String optionsUrl, String gexbotUrl) {
        CompletableFuture<String> optionsFuture = fetchJson(optionsUrl)
                .thenApply(this::normalizeOptionsDepth);
        CompletableFuture<String> gexbotFuture = fetchJson(gexbotUrl)
                .thenApply(this::normalizeGexbot);

        return optionsFuture.thenCombine(gexbotFuture, (opt, gex) -> new String[]{opt, gex})
                .thenCompose(arr -> simulateLocalAnalysis(arr[0], arr[1]));
    }

    private CompletableFuture<JsonNode> fetchJson(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::readJson);
    }

    private JsonNode readJson(String body) {
        try {
            return mapper.readTree(body);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    private String normalizeOptionsDepth(JsonNode node) {
        JsonNode data = node.path("data");
        return data.isMissingNode() ? node.toString() : data.toString();
    }

    private String normalizeGexbot(JsonNode node) {
        JsonNode result = node.path("result");
        return result.isMissingNode() ? node.toString() : result.toString();
    }

    private CompletableFuture<AnalysisResponse> simulateLocalAnalysis(String options, String gex) {
        return CompletableFuture.supplyAsync(() -> {
            String summary = "Análisis simulado (placeholder)\n" +
                    "OptionsDepth: " + options + "\n" +
                    "Gexbot: " + gex;
            return new AnalysisResponse(summary);
        });
    }
}
