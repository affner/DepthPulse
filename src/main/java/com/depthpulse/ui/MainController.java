package com.depthpulse.ui;

import com.depthpulse.dto.AnalysisPayload;
import com.depthpulse.service.AnalysisService;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Controller for the main UI.
 */
@Component
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private TextField consultaField;
    @FXML
    private TextField optionIdField;
    @FXML
    private Button runButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea getboxArea;
    @FXML
    private TextArea optionDepthArea;
    @FXML
    private TextArea analysisArea;

    private final AnalysisService analysisService;

    public MainController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @FXML
    private void initialize() {
        progressIndicator.setVisible(false);
        statusLabel.setText("Listo");
        getboxArea.clear();
        optionDepthArea.clear();
        analysisArea.clear();
    }

    @FXML
    private void onRun() {
        String consulta = consultaField.getText().trim();
        String optionId = optionIdField.getText().trim();
        log.info("Run triggered with consulta={} optionId={}", consulta, optionId);
        if (consulta.isEmpty() || optionId.isEmpty()) {
            statusLabel.setText("Datos no válidos");
            return;
        }
        progressIndicator.setVisible(true);
        statusLabel.setText("Cargando...");
        getboxArea.clear();
        optionDepthArea.clear();
        analysisArea.clear();

        CompletableFuture
                .supplyAsync(() -> analysisService.analyze(consulta, optionId))
                .whenComplete((payload, ex) -> Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    if (ex != null) {
                        log.error("Analysis failed", ex);
                        statusLabel.setText("Error: " + ex.getCause().getMessage());
                    } else {
                        log.debug("Analysis completed for consulta={} optionId={}", consulta, optionId);
                        statusLabel.setText("Completado");
                        getboxArea.setText(payload.getboxJson());
                        optionDepthArea.setText(payload.optionDepthJson());
                        analysisArea.setText(payload.analysisResult());
                    }
                }));
    }
}
