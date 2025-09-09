package com.depthpulse.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class GetBoxApiClient {

    private static final Logger log = LoggerFactory.getLogger(GetBoxApiClient.class);

    private final WebClient client;

    public GetBoxApiClient(@Qualifier("getBoxWebClient") WebClient client) {
        this.client = client;
    }

    public String fetchRaw(String query) {
        log.info("Requesting GetBox API with query={}", query);
        try {
            String body = client.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("query", query).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.debug("GetBox API returned {} chars for query={}", body != null ? body.length() : 0, query);
            return body;
        } catch (WebClientResponseException e) {
            log.error("GetBox API error status={} query={}", e.getStatusCode(), query, e);
            throw new RuntimeException("GetBox API error: " + e.getStatusCode(), e);
        }
    }
}
