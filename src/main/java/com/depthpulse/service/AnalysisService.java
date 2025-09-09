package com.depthpulse.service;

import com.depthpulse.client.GetBoxApiClient;
import com.depthpulse.client.OptionDepthApiClient;
import com.depthpulse.dto.AnalysisPayload;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {

    private final GetBoxApiClient getBoxApiClient;
    private final OptionDepthApiClient optionDepthApiClient;

    public AnalysisService(GetBoxApiClient getBoxApiClient, OptionDepthApiClient optionDepthApiClient) {
        this.getBoxApiClient = getBoxApiClient;
        this.optionDepthApiClient = optionDepthApiClient;
    }

    public AnalysisPayload fetchBoth(String consulta, String optionId) {
        String getbox = getBoxApiClient.fetchRaw(consulta);
        String optionDepth = optionDepthApiClient.fetchRaw(optionId);
        return new AnalysisPayload(consulta, getbox, optionDepth);
    }
}
