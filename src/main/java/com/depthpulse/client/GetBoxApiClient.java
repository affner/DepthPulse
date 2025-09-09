package com.depthpulse.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class GetBoxApiClient {

    private final WebClient client;

    public GetBoxApiClient(@Qualifier("getBoxWebClient") WebClient client) {
        this.client = client;
    }

    public String fetchRaw(String query) {
        try {
            return client.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("query", query).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("GetBox API error: " + e.getStatusCode(), e);
        }
    }
}
