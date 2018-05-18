/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.standalone;

import java.util.Optional;

import org.jmad.modelpack.gui.conf.ModelSelectionDialogConfiguration;
import org.jmad.modelpack.gui.panes.SelectedModelConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

@Configuration
@Import(value = ModelSelectionDialogConfiguration.class)
public class JMadModelPackageBrowserMain extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(JMadModelPackageBrowserMain.class);
    private static final String MAIN_NODE_NAME = "main_fx_node";

    @Bean(MAIN_NODE_NAME)
    public BorderPane view(Dialog<SelectedModelConfiguration> modelDefinitionSelectionDialog) {
        BorderPane pane = new BorderPane();
        Button button = new Button("select model");
        button.setOnAction((evt) -> {
            Optional<SelectedModelConfiguration> result = modelDefinitionSelectionDialog.showAndWait();
            if (result.isPresent()) {
                LOGGER.info("Selected model configuration: {}", result.get());
            } else {
                LOGGER.info("No model configuration selected.");
            }

        });
        pane.setCenter(button);
        return pane;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(JMadModelPackageBrowserMain.class);

        Parent mainNode = ctx.getBean(MAIN_NODE_NAME, Parent.class);

        Scene scene = new Scene(mainNode);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(JMadModelPackageBrowserMain.class);
    }
}
