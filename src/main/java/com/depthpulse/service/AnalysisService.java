package com.depthpulse.service;

import com.depthpulse.client.GexbotClient;
import com.depthpulse.client.OptionsDepthClient;
import com.depthpulse.dto.AnalysisPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service responsible for orchestrating the calls to both external APIs
 * and producing a simple combined analysis.
 */
@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final GexbotClient gexbotClient;
    private final OptionsDepthClient optionsDepthClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public AnalysisService(GexbotClient gexbotClient, OptionsDepthClient optionsDepthClient) {
        this.gexbotClient = gexbotClient;
        this.optionsDepthClient = optionsDepthClient;
    }

    /**
     * Launches both API calls in parallel and returns the raw responses
     * together with a minimal combined analysis.
     */
    public AnalysisPayload analyze(String consulta, String optionId) {
        log.info("Fetching data for consulta={} optionId={}", consulta, optionId);

        // Run both clients asynchronously.
        CompletableFuture<String> getboxFuture =
                CompletableFuture.supplyAsync(() -> gexbotClient.fetchRaw(consulta));
        CompletableFuture<String> optionFuture =
                CompletableFuture.supplyAsync(() -> optionsDepthClient.fetchRaw(optionId));

        // Join results.
        String getbox = getboxFuture.join();
        log.debug("GetBox response size={} for consulta={}",
                getbox != null ? getbox.length() : 0, consulta);

        String optionDepth = optionFuture.join();
        log.debug("OptionDepth response size={} for optionId={}",
                optionDepth != null ? optionDepth.length() : 0, optionId);

        String analysis = combineResults(getbox, optionDepth);
        log.info("Combining results for consulta={} optionId={}", consulta, optionId);
        return new AnalysisPayload(consulta, getbox, optionDepth, analysis);
    }

    private String combineResults(String getboxJson, String optionDepthJson) {
        /*
         * En un escenario real, aquí se invocaría un modelo de IA que:
         * - Analizaría profundamente los datos de ambas APIs, identificando patrones relevantes.
         * - Promediaría valores comparables y descartaría outliers que distorsionen las métricas.
         * - Evaluaría correlaciones y otras estadísticas para producir una señal de trading.
         * - Generaría una recomendación basada en las conclusiones anteriores.
         *
         * Para este ejemplo, simplemente extraemos un número de cada JSON y
         * calculamos su promedio para demostrar el flujo de trabajo.
         */
        try {
            double score = mapper.readTree(getboxJson).path("score").asDouble();
            double impliedVol = mapper.readTree(optionDepthJson).path("impliedVol").asDouble();
            double average = (score + impliedVol) / 2.0;
            return "Promedio simple: " + average;
        } catch (Exception e) {
            log.warn("Failed to combine results", e);
            return "Análisis no disponible";
        }
    }
}
