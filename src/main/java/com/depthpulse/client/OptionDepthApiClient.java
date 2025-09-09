package com.depthpulse.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class OptionDepthApiClient {

    private final WebClient client;

    public OptionDepthApiClient(@Qualifier("optionDepthWebClient") WebClient client) {
        this.client = client;
    }

    public String fetchRaw(String optionId) {
        try {
            return client.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("id", optionId).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("OptionDepth API error: " + e.getStatusCode(), e);
        }
    }
}
