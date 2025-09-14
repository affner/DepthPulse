package com.depthpulse.client;

import com.depthpulse.dto.TickersResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Service
public class GetBoxClient {

    private static final Logger LOG = LoggerFactory.getLogger(GetBoxClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String baseUrl;

    // Inyección por propiedades; ya no intenta autowirear un String como bean
    public GetBoxClient(RestTemplateBuilder rtBuilder,
                        ObjectMapper mapper,
                        @Value("${getbox.api-key}") String apiKey,
                        @Value("${getbox.base-url:https://api.gexbot.com}") String baseUrl) {

        this.restTemplate = rtBuilder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(20))
                .build();
        this.mapper = mapper;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    /** Construye la cabecera con tu API key */
    private HttpEntity<Void> buildRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // Ajusta según la doc real: si es "x-api-key", usa esa cabecera; si es bearer, deja lo siguiente:
        headers.setBearerAuth(apiKey);
        // headers.set("x-api-key", apiKey);
        return new HttpEntity<>(headers);
    }

    /** Devuelve el listado de símbolos soportados */
    public TickersResponse getTickers() throws RestClientException {
        String url = baseUrl + "/tickers";
        HttpEntity<Void> req = buildRequest();

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, req, String.class);
        String body = resp.getBody();
        if (body == null) {
            throw new IllegalStateException("Respuesta vacía de /tickers");
        }
        try {
            return mapper.readValue(body, TickersResponse.class);
        } catch (Exception e) {
            LOG.error("Error parseando /tickers: {}", e.getMessage(), e);
            throw new IllegalStateException("No se pudo parsear la respuesta de /tickers", e);
        }
    }

    /**
     * Retorna datos de ejemplo sin realizar llamadas de red reales.
     * Esto nos permite demostrar el flujo completo de la aplicación
     * en entornos donde la API remota no está disponible.
     */
    public TickersResponse getSampleTickers() {
        TickersResponse resp = new TickersResponse();
        resp.setStocks(java.util.List.of("AAPL", "MSFT"));
        resp.setIndexes(java.util.List.of("SPX"));
        resp.setFutures(java.util.List.of("ES"));
        return resp;
    }

    /** Consulta genérica para endpoints que devuelven JSON dinámico (gex chain, profile, etc.) */
    public JsonNode callEndpoint(String path, Map<String, String> queryParams) throws RestClientException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + path);
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }
        HttpEntity<Void> req = buildRequest();

        ResponseEntity<String> resp = restTemplate.exchange(
                builder.toUriString(), HttpMethod.GET, req, new ParameterizedTypeReference<String>() {});
        String body = resp.getBody();
        if (body == null) {
            throw new IllegalStateException("Respuesta vacía de " + path);
        }
        try {
            return mapper.readTree(body);
        } catch (Exception e) {
            LOG.error("Error parseando {}: {}", path, e.getMessage(), e);
            throw new IllegalStateException("No se pudo parsear la respuesta de " + path, e);
        }
    }
}
