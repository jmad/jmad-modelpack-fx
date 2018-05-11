/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import static java.util.Objects.requireNonNull;

import cern.accsoft.steering.jmad.domain.machine.RangeDefinition;
import cern.accsoft.steering.jmad.domain.machine.SequenceDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.OpticsDefinition;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class JMadModelDefinitionSelectionPane extends VBox {

    private final PackageSelectionModel selectionModel;

    public JMadModelDefinitionSelectionPane(PackageSelectionModel selectionModel) {
        this.selectionModel = requireNonNull(selectionModel, "selectionModel must not be null");

        init();
    }

    private void init() {
        ListView<JMadModelDefinition> definitionView = new ListView<>(selectionModel.availableDefinitionsProperty());
        definitionView.getSelectionModel().selectedItemProperty()
                .addListener((p, ov, nv) -> selectionModel.selectedModelDefinitionProperty().set(nv));
        selectionModel.selectedModelDefinitionProperty()
                .addListener((p, ov, nv) -> definitionView.getSelectionModel().select(nv));

        definitionView.setPrefHeight(200);
        TitledPane definitionPane = new TitledPane("Model definition", definitionView);
        definitionPane.setCollapsible(false);
        getChildren().add(definitionPane);

        TitledPane detailPane = new TitledPane();
        detailPane.setCollapsible(false);
        detailPane.textProperty().bind(selectionModel.selectedModelDefinitionProperty().asString());
        getChildren().add(detailPane);

        VBox content = new VBox();
        detailPane.setContent(content);

        content.getChildren().add(new Label("Sequence:"));
        ComboBox<SequenceDefinition> sequenceCombo = new ComboBox<>();
        content.getChildren().add(sequenceCombo);

        sequenceCombo.itemsProperty().bind(selectionModel.availableSequencesProperty());

        sequenceCombo.getSelectionModel().selectedItemProperty()
                .addListener((p, ov, nv) -> selectionModel.selectedSequenceProperty().set(nv));
        selectionModel.selectedSequenceProperty()
                .addListener((p, ov, nv) -> sequenceCombo.getSelectionModel().select(nv));

        content.getChildren().add(new Label("Range:"));
        ComboBox<RangeDefinition> rangeCombo = new ComboBox<>();
        content.getChildren().add(rangeCombo);
        rangeCombo.itemsProperty().bind(selectionModel.availableRangesProperty());
        selectionModel.selectedRangeProperty().addListener((p, ov, nv) -> rangeCombo.getSelectionModel().select(nv));
        rangeCombo.getSelectionModel().selectedItemProperty()
                .addListener((p, ov, nv) -> selectionModel.selectedRangeProperty().set(nv));

        content.getChildren().add(new Label("Optics:"));
        ListView<OpticsDefinition> opticsDefinitions = new ListView<>();
        content.getChildren().add(opticsDefinitions);

        opticsDefinitions.setPrefHeight(200);
        opticsDefinitions.setPrefWidth(300);
        opticsDefinitions.itemsProperty().bind(selectionModel.availableOpticsProperty());
        opticsDefinitions.getSelectionModel().selectedItemProperty()
                .addListener((p, ov, nv) -> selectionModel.selectedOpticsProperty().set(nv));
        selectionModel.selectedOpticsProperty()
                .addListener((p, ov, nv) -> opticsDefinitions.getSelectionModel().select(nv));
    }

}
