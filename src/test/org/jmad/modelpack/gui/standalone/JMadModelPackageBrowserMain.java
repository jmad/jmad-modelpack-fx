/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.standalone;

import cern.accsoft.steering.jmad.conf.JMadServiceConfiguration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.jmad.modelpack.gui.conf.JMadModelSelectionDialogFactory;
import org.jmad.modelpack.gui.conf.JMadModelSelectionDialogStandaloneConfiguration;
import org.jmad.modelpack.gui.domain.JMadModelSelection;
import org.jmad.modelpack.gui.domain.JMadModelSelectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@Configuration
@Import({JMadModelSelectionDialogStandaloneConfiguration.class, JMadServiceConfiguration.class})
public class JMadModelPackageBrowserMain extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(JMadModelPackageBrowserMain.class);
    private static final String MAIN_NODE_NAME = "main_fx_node";

    @Bean(MAIN_NODE_NAME)
    public Parent view(JMadModelSelectionDialogFactory factory) {
        Button buttonAll = new Button("Open Model Selection");
        buttonAll.setOnAction((evt) -> {
            Optional<JMadModelSelection> result = factory.showAndWaitModelSelection();
            if (result.isPresent()) {
                LOGGER.info("Selected model configuration: {}", result.get());
            } else {
                LOGGER.info("No model configuration selected.");
            }

        });
        Button buttonModelDefOnly = new Button("Open Model Selection: MODEL DEFINITION ONLY");
        buttonModelDefOnly.setOnAction((evt) -> {
            Optional<JMadModelSelection> result = factory.showAndWaitModelSelection(JMadModelSelectionType.MODEL_DEFINITION_ONLY);
            if (result.isPresent()) {
                LOGGER.info("Selected model configuration: {}", result.get());
            } else {
                LOGGER.info("No model configuration selected.");
            }

        });

        VBox pane = new VBox();
        pane.setPadding(new Insets(10));
        pane.setSpacing(10);
        pane.getChildren().add(buttonAll);
        pane.getChildren().add(buttonModelDefOnly);
        return pane;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(JMadModelPackageBrowserMain.class);

        Parent mainNode = ctx.getBean(MAIN_NODE_NAME, Parent.class);

        Scene scene = new Scene(mainNode);
        primaryStage.setScene(scene);
        primaryStage.show();
        Platform.setImplicitExit(true);
    }

    public static void main(String[] args) {
        Application.launch(JMadModelPackageBrowserMain.class);
    }
}
