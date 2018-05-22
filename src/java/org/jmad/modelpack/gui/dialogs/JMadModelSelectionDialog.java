/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.dialogs;

import cern.accsoft.steering.jmad.domain.machine.RangeDefinition;
import cern.accsoft.steering.jmad.model.JMadModelStartupConfiguration;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.OpticsDefinition;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import org.jmad.modelpack.gui.domain.JMadModelSelection;
import org.jmad.modelpack.gui.domain.ModelPackSelectionState;
import org.jmad.modelpack.gui.util.GuiUtils;

import static javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE;

public class JMadModelSelectionDialog extends Dialog<JMadModelSelection> {

    public JMadModelSelectionDialog(Region modelSelectionRegion, Region repositorySelectionRegion,
                                    ModelPackSelectionState selectionState) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(UNAVAILABLE);
        tabPane.getTabs().add(new Tab("Available models", modelSelectionRegion));
        tabPane.getTabs().add(new Tab("Models repositories", repositorySelectionRegion));

        tabPane.setPrefWidth(1000);
        tabPane.setPrefHeight(700);

        getDialogPane().setContent(tabPane);

        ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        setTitle("JMad Model Selection");
        getDialogPane().setPadding(GuiUtils.ZERO_INSETS);
        tabPane.setPadding(GuiUtils.ZERO_INSETS);

        setResultConverter(b -> {
            if (b != buttonTypeOk) {
                return null;
            }

            JMadModelDefinition modelDefinition = selectionState.selectedModelDefinitionProperty().get();

            if (modelDefinition == null) {
                return null;
            }

            OpticsDefinition opticsDefinition = selectionState.selectedOpticsProperty().get();
            RangeDefinition rangeDefinition = selectionState.selectedRangeProperty().get();

            JMadModelStartupConfiguration startupConfiguration = new JMadModelStartupConfiguration();
            startupConfiguration.setInitialOpticsDefinition(opticsDefinition);
            startupConfiguration.setInitialRangeDefinition(rangeDefinition);

            return new JMadModelSelection(modelDefinition, startupConfiguration);
        });
    }

}
