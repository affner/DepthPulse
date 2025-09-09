package com.depthpulse;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(
        exclude = { DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class })
public class DepthPulseApplication extends Application {

    @Override
    public void start(Stage stage) {          // JavaFX entry-point
        // TODO cargar tu escena FXML
        stage.setTitle("DepthPulse");
        stage.show();
    }

    public static void main(String[] args) {
        SpringApplication.run(DepthPulseApplication.class, args);
        launch(args);                         // arranca JavaFX
    }
}
