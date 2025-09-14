package com.depthpulse.client;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@Service
public class OptionsDepthClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String baseUrl = "https://api-od.cogentlabs.co.uk/options-depth-api/v1";

    public OptionsDepthClient() {
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    public List<HeatmapPoint> fetchHeatmap(LocalDate date,
                                           String ticker,
                                           String model,
                                           String type,
                                           String apiKey,
                                           Double minPrice,
                                           Double maxPrice) throws Exception {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/heatmap")
                .queryParam("date", date.toString())
                .queryParam("ticker", ticker)
                .queryParam("model", model)
                .queryParam("type", type)
                .queryParam("$key", apiKey);

        if (minPrice != null) {
            builder.queryParam("min_price", minPrice);
        }
        if (maxPrice != null) {
            builder.queryParam("max_price", maxPrice);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String responseBody = restTemplate
                .exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(headers), String.class)
                .getBody();

        // El endpoint devuelve un array de objetos
        return mapper.readValue(responseBody,
                mapper.getTypeFactory().constructCollectionType(List.class, HeatmapPoint.class));
    }

    /**
     * Retorna un pequeño conjunto de puntos para pruebas locales. No realiza
     * ninguna llamada de red, simplemente simula la estructura devuelta por la API.
     */
    public List<HeatmapPoint> getSampleHeatmap() {
        HeatmapPoint p1 = new HeatmapPoint();
        p1.setPrice(100);
        p1.setValue(15);
        p1.setEffectiveDatetime("2024-01-01T10:00:00Z");

        HeatmapPoint p2 = new HeatmapPoint();
        p2.setPrice(105);
        p2.setValue(5);
        p2.setEffectiveDatetime("2024-01-01T11:00:00Z");

        return List.of(p1, p2);
    }

    public static class HeatmapPoint {
        private double price;
        private double value;
        @JsonProperty("effectiveDatetime")
        private String effectiveDatetime;

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }

        public String getEffectiveDatetime() { return effectiveDatetime; }
        public void setEffectiveDatetime(String effectiveDatetime) { this.effectiveDatetime = effectiveDatetime; }

        @Override
        public String toString() {
            return "HeatmapPoint{" +
                    "price=" + price +
                    ", value=" + value +
                    ", effectiveDatetime='" + effectiveDatetime + '\'' +
                    '}';
        }
    }
}
