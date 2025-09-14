package com.depthpulse.service;

import com.depthpulse.client.GexbotClient;
import com.depthpulse.client.OptionsDepthClient;
import com.depthpulse.dto.AnalysisPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final GexbotClient gexbotClient;
    private final OptionsDepthClient optionsDepthClient;

    public AnalysisService(GexbotClient gexbotClient, OptionsDepthClient optionsDepthClient) {
        this.gexbotClient = gexbotClient;
        this.optionsDepthClient = optionsDepthClient;
    }

    public AnalysisPayload fetchBoth(String consulta, String optionId) {
        log.info("Fetching data for consulta={} optionId={}", consulta, optionId);
        String getbox = gexbotClient.fetchRaw(consulta);
        log.debug("GetBox response size={} for consulta={} ", getbox != null ? getbox.length() : 0, consulta);
        String optionDepth = optionsDepthClient.fetchRaw(optionId);
        log.debug("OptionDepth response size={} for optionId={}", optionDepth != null ? optionDepth.length() : 0, optionId);
        log.info("Combining results for consulta={} optionId={}", consulta, optionId);
        return new AnalysisPayload(consulta, getbox, optionDepth);
    }
}
