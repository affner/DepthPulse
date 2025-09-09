package com.depthpulse.ui;

import com.depthpulse.http.AnalysisClient;
import com.depthpulse.model.AnalysisResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.concurrent.CompletableFuture;

/**
 * Controller for the main UI.
 */
public class MainController {

    @FXML
    private TextField optionsUrlField;
    @FXML
    private TextField gexbotUrlField;
    @FXML
    private Button runButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea outputArea;

    private final AnalysisClient client = new AnalysisClient();

    @FXML
    private void initialize() {
        progressIndicator.setVisible(false);
        statusLabel.setText("Listo");
    }

    @FXML
    private void onRun() {
        String optionsUrl = optionsUrlField.getText().trim();
        String gexbotUrl = gexbotUrlField.getText().trim();
        if (optionsUrl.isEmpty() || gexbotUrl.isEmpty()) {
            statusLabel.setText("URL no válida");
            return;
        }
        progressIndicator.setVisible(true);
        statusLabel.setText("Cargando...");
        outputArea.clear();

        CompletableFuture<AnalysisResponse> future = client.runAnalysis(optionsUrl, gexbotUrl);
        future.whenComplete((resp, ex) -> {
            Platform.runLater(() -> {
                progressIndicator.setVisible(false);
                if (ex != null) {
                    statusLabel.setText("Error: " + ex.getCause().getMessage());
                    outputArea.setText("");
                } else {
                    statusLabel.setText("Completado");
                    outputArea.setText(resp.body());
                }
            });
        });
    }
}
