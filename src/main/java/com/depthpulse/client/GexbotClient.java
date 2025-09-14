package com.depthpulse.client;

import com.depthpulse.dto.TickersResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Map;

@Service
public class GexbotClient {

    private static final Logger LOG = LoggerFactory.getLogger(GexbotClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String baseUrl = "https://api.gexbot.com";

    public GexbotClient(String apiKey) {
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
        this.apiKey = apiKey;
    }

    /** Construye la cabecera con tu API key */
    private HttpEntity<Void> buildRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // ajusta el nombre del header si la documentación oficial usa x-api-key o similar
        headers.set("Authorization", "Bearer " + apiKey);
        return new HttpEntity<>(headers);
    }

    /** Devuelve el listado de símbolos soportados */
    public TickersResponse getTickers() throws Exception {
        String url = baseUrl + "/tickers";
        HttpEntity<Void> req = buildRequest();
        String json = restTemplate.exchange(url, HttpMethod.GET, req, String.class).getBody();
        return mapper.readValue(json, TickersResponse.class);
    }

    /** Consulta genérica para endpoints que devuelven JSON dinámico (gex chain, profile, etc.) */
    public JsonNode callEndpoint(String path, Map<String, String> queryParams) throws Exception {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + path);
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }
        HttpEntity<Void> req = buildRequest();
        String json = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, req, String.class).getBody();
        return mapper.readTree(json);
    }

    /**
     * Temporary helper that simulates a simple response from the GetBox API.
     * Provides deterministic data for development without contacting the
     * remote service.
     */
    public String fetchRaw(String consulta) {
        // A real implementation would issue an HTTP request using 'consulta'.
        return String.format("{\"consulta\":\"%s\",\"score\":42}", consulta);
    }
}

