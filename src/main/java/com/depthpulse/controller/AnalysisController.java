package com.depthpulse.controller;

import com.depthpulse.dto.AnalysisPayload;
import com.depthpulse.service.AnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalysisController {

    private final AnalysisService service;

    public AnalysisController(AnalysisService service) {
        this.service = service;
    }

    @GetMapping("/analysis")
    public AnalysisPayload getAnalysis(@RequestParam String consulta,
                                       @RequestParam String optionId) {
        return service.fetchBoth(consulta, optionId);
    }
}
