package com.depthpulse.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class OptionDepthApiClient {

    private static final Logger log = LoggerFactory.getLogger(OptionDepthApiClient.class);

    private final WebClient client;

    public OptionDepthApiClient(@Qualifier("optionDepthWebClient") WebClient client) {
        this.client = client;
    }

    public String fetchRaw(String optionId) {
        log.info("Requesting OptionDepth API with id={}", optionId);
        try {
            String body = client.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("id", optionId).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.debug("OptionDepth API returned {} chars for id={}", body != null ? body.length() : 0, optionId);
            return body;
        } catch (WebClientResponseException e) {
            log.error("OptionDepth API error status={} id={}", e.getStatusCode(), optionId, e);
            throw new RuntimeException("OptionDepth API error: " + e.getStatusCode(), e);
        }
    }
}
