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
import java.util.Map;

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
     * Temporary helper that produces synthetic data so the rest of the
     * application can be exercised without contacting the real service. The
     * value is derived from the {@code optionId} to keep it deterministic
     * between executions.
     *
     * @param optionId identifier of the option contract
     * @return simulated metric from the options-depth API
     */
    public double fetchDummySignal(String optionId) {
        // Use the hash code to obtain a pseudo value in the 0-10 range.
        return Math.abs(optionId.hashCode() % 100) / 10.0;
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
