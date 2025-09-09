package com.depthpulse.service;

import com.depthpulse.client.GetBoxApiClient;
import com.depthpulse.client.OptionDepthApiClient;
import com.depthpulse.dto.AnalysisPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final GetBoxApiClient getBoxApiClient;
    private final OptionDepthApiClient optionDepthApiClient;

    public AnalysisService(GetBoxApiClient getBoxApiClient, OptionDepthApiClient optionDepthApiClient) {
        this.getBoxApiClient = getBoxApiClient;
        this.optionDepthApiClient = optionDepthApiClient;
    }

    public AnalysisPayload fetchBoth(String consulta, String optionId) {
        log.info("Fetching data for consulta={} optionId={}", consulta, optionId);
        String getbox = getBoxApiClient.fetchRaw(consulta);
        log.debug("GetBox response size={} for consulta={} ", getbox != null ? getbox.length() : 0, consulta);
        String optionDepth = optionDepthApiClient.fetchRaw(optionId);
        log.debug("OptionDepth response size={} for optionId={}", optionDepth != null ? optionDepth.length() : 0, optionId);
        log.info("Combining results for consulta={} optionId={}", consulta, optionId);
        return new AnalysisPayload(consulta, getbox, optionDepth);
    }
}
