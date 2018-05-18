/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.dialogs;

import static javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE;

import org.jmad.modelpack.gui.panes.ModelPackSelectionState;
import org.jmad.modelpack.gui.panes.ModelRepositoryPane;
import org.jmad.modelpack.gui.panes.SelectedModelConfiguration;

import cern.accsoft.steering.jmad.domain.machine.RangeDefinition;
import cern.accsoft.steering.jmad.model.JMadModelStartupConfiguration;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.OpticsDefinition;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class JMadModelSelectionDialog extends Dialog<SelectedModelConfiguration> {

    public JMadModelSelectionDialog(Node fullSelectionPane, ModelRepositoryPane repoListView,
            ModelPackSelectionState selectionModel) {

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(UNAVAILABLE);
        tabPane.getTabs().add(new Tab("Available models", fullSelectionPane));
        tabPane.getTabs().add(new Tab("Models repositories", repoListView));

        tabPane.setPrefWidth(1000);
        tabPane.setPrefHeight(700);

        getDialogPane().setContent(tabPane);

        ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        setTitle("JMad model selection");
        getDialogPane().setPadding(new Insets(0));
        tabPane.setPadding(new Insets(0));

        setResultConverter(b -> {
            if (b == buttonTypeOk) {
                JMadModelDefinition modelDefinition = selectionModel.selectedModelDefinitionProperty().get();
                if (modelDefinition == null) {
                    return null;
                }

                OpticsDefinition opticsDefinition = selectionModel.selectedOpticsProperty().get();
                RangeDefinition rangeDefinition = selectionModel.selectedRangeProperty().get();

                JMadModelStartupConfiguration startupConfiguration = new JMadModelStartupConfiguration();
                startupConfiguration.setInitialOpticsDefinition(opticsDefinition);
                startupConfiguration.setInitialRangeDefinition(rangeDefinition);

                return new SelectedModelConfiguration(modelDefinition, startupConfiguration);
            } else {
                return null;
            }
        });

    }

}
