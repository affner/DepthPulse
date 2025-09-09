package com.depthpulse.ui;

import com.depthpulse.dto.AnalysisPayload;
import com.depthpulse.service.AnalysisService;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

/**
 * Controller for the main UI.
 */
@Component
public class MainController {

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
    private TextArea outputArea;

    private final AnalysisService analysisService;

    public MainController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @FXML
    private void initialize() {
        progressIndicator.setVisible(false);
        statusLabel.setText("Listo");
    }

    @FXML
    private void onRun() {
        String consulta = consultaField.getText().trim();
        String optionId = optionIdField.getText().trim();
        if (consulta.isEmpty() || optionId.isEmpty()) {
            statusLabel.setText("Datos no válidos");
            return;
        }
        progressIndicator.setVisible(true);
        statusLabel.setText("Cargando...");
        outputArea.clear();

        CompletableFuture
                .supplyAsync(() -> analysisService.fetchBoth(consulta, optionId))
                .whenComplete((payload, ex) -> Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    if (ex != null) {
                        statusLabel.setText("Error: " + ex.getCause().getMessage());
                    } else {
                        statusLabel.setText("Completado");
                        outputArea.setText("GetBox: " + payload.getboxJson() + "\nOptionDepth: " + payload.optionDepthJson());
                    }
                }));
    }
}
