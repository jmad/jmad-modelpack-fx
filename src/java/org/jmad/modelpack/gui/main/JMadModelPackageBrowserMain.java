/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.main;

import static javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE;

import java.util.Optional;

import org.jmad.modelpack.gui.panes.JMadModelDefinitionSelectionPane;
import org.jmad.modelpack.gui.panes.ModelPackagesPane;
import org.jmad.modelpack.gui.panes.ModelRepositoryPane;
import org.jmad.modelpack.gui.panes.PackageSelectionModel;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.ModelPackageRepositoryManager;
import org.jmad.modelpack.service.conf.JMadModelPackageServiceConfiguration;
import org.minifx.workbench.MiniFx;
import org.minifx.workbench.annotations.View;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

@Configuration
@Import(value = JMadModelPackageServiceConfiguration.class)
public class JMadModelPackageBrowserMain {

    @Bean
    public Node packageBrowser(JMadModelPackageService packageService, PackageSelectionModel packageSelectionModel) {
        return new ModelPackagesPane(packageService, packageSelectionModel);
    }

    @Bean
    public Node fullSelectionPane(Node packageBrowser,
            PackageSelectionModel jmadModelDefinitionSelectionModel) {
        
        JMadModelDefinitionSelectionPane selectionPane = new JMadModelDefinitionSelectionPane(jmadModelDefinitionSelectionModel);
        HBox pane = new HBox(packageBrowser, selectionPane);
        pane.setFillHeight(true);
        pane.setAlignment(Pos.TOP_CENTER);
        return pane;
    }

    @Bean
    public PackageSelectionModel packageSelectionModel(JMadModelPackageService packageService) {
        return new PackageSelectionModel(packageService);
    }

    @Bean
    public ModelRepositoryPane repoListView(ModelPackageRepositoryManager manager) {
        return new ModelRepositoryPane(manager);
    }

    
    @Bean
    public Dialog<JMadModelDefinition> modelDefinitionSelectionDialog(Node fullSelectionPane, 
            ModelRepositoryPane repoListView) {
        Dialog<JMadModelDefinition> dialog = new Dialog<>();
        

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(UNAVAILABLE);
        tabPane.getTabs().add(new Tab("models", fullSelectionPane));
        tabPane.getTabs().add(new Tab("repos", repoListView));

        dialog.getDialogPane().setContent(tabPane);

        ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        dialog.setResultConverter(new Callback<ButtonType, JMadModelDefinition>() {
            @Override
            public JMadModelDefinition call(ButtonType b) {

                System.out.println(b);

                if (b == buttonTypeOk) {
                    System.out.println("Selected");
                    /* TODO! */
                    return null;
                } else {
                    System.out.println("canceled");
                    return null;
                }
            }
        });

        return dialog;
    }

    @View
    @Bean
    public BorderPane view(Dialog<JMadModelDefinition> modelDefinitionSelectionDialog) {
        BorderPane pane = new BorderPane();
        Button button = new Button("select model");
        button.setOnAction((evt) -> {
            modelDefinitionSelectionDialog.setWidth(900);
            modelDefinitionSelectionDialog.setHeight(700);
            modelDefinitionSelectionDialog.setResizable(true);
            Optional<JMadModelDefinition> result = modelDefinitionSelectionDialog.showAndWait();
        });
        pane.setCenter(button);
        return pane;
    }

    public static final void main(String... args) {
        MiniFx.launcher(JMadModelPackageBrowserMain.class).launch(args);
    }

}
