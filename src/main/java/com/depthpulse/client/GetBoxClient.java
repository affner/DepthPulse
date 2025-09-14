package com.depthpulse.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Minimal client that simulates an object-detection service. In a real
 * implementation this class would perform an HTTP call to an external API
 * returning bounding boxes for a given input. For the purposes of this project
 * we simply return a small JSON payload so the rest of the application can be
 * developed and tested without network dependencies.
 */
@Service
public class GetBoxClient {

    private static final Logger log = LoggerFactory.getLogger(GetBoxClient.class);

    /**
     * Fetches raw detection data for the provided query. The method is kept
     * synchronous; higher layers are responsible for executing it asynchronously
     * so the JavaFX UI thread is never blocked.
     *
     * @param query Any string identifying the object to detect.
     * @return Simulated JSON response.
     */
    public String fetchRaw(String query) {
        log.debug("Simulating GetBox API call for query={}", query);
        // In the real world, here we would invoke an HTTP endpoint and return
        // whatever JSON payload it produced. To keep the example self contained
        // we fabricate a deterministic response that includes a numeric score
        // representing, for instance, the number of detected boxes.
        return "{\"boxes\": 2, \"query\": \"" + query + "\"}";
    }
}

