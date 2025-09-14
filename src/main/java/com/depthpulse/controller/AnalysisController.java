package com.depthpulse.controller;

import com.depthpulse.dto.AnalysisPayload;
import com.depthpulse.service.AnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalysisController {

    private static final Logger log = LoggerFactory.getLogger(AnalysisController.class);

    private final AnalysisService service;

    public AnalysisController(AnalysisService service) {
        this.service = service;
    }

    @GetMapping("/analysis")
    public AnalysisPayload getAnalysis(@RequestParam String consulta,
                                       @RequestParam String optionId) {
        log.info("Received analysis request consulta={} optionId={}", consulta, optionId);
        // The service performs work asynchronously, but for the REST endpoint we
        // simply block and wait for the result. The UI layer uses the same
        // method without blocking to keep the interface responsive.
        AnalysisPayload payload = service.fetchBoth(consulta, optionId).join();
        log.debug("Returning analysis for consulta={} optionId={}", consulta, optionId);
        return payload;
    }
}
