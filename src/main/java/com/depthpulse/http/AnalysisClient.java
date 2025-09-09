package com.depthpulse.http;

import com.depthpulse.model.AnalysisResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP client layer responsible for contacting the backend.
 */
public class AnalysisClient {

    private final HttpClient client;

    public AnalysisClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Execute the analysis asynchronously against the given URL.
     */
    public CompletableFuture<AnalysisResponse> runAnalysis(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new AnalysisResponse(response.body()));
    }
}
