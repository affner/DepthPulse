package com.depthpulse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * JavaFX entry point.
 */
@SpringBootApplication
public class DepthPulseApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(DepthPulseApplication.class);

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        log.info("Starting Spring context");
        context = new SpringApplicationBuilder(DepthPulseApplication.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        log.info("Launching JavaFX stage");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("DepthPulse");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        log.info("Stopping application");
        context.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
