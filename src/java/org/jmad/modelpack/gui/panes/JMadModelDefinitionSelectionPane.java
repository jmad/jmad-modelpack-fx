/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import static java.util.Objects.requireNonNull;
import static org.jmad.modelpack.gui.panes.FxUtils.onChange;
import static org.jmad.modelpack.gui.panes.FxUtils.setFontWeight;

import cern.accsoft.steering.jmad.domain.machine.RangeDefinition;
import cern.accsoft.steering.jmad.domain.machine.SequenceDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.OpticsDefinition;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class JMadModelDefinitionSelectionPane extends VBox {

    private static final double COMBOBOX_MIN_WIDTH = 150;
    private final PackageSelectionModel selectionModel;

    public JMadModelDefinitionSelectionPane(PackageSelectionModel selectionModel) {
        this.selectionModel = requireNonNull(selectionModel, "selectionModel must not be null");

        init();
    }

    private void init() {
        setSpacing(ModelPackGuiUtils.DEFAULT_SPACING);
        ListView<JMadModelDefinition> definitionView = new ListView<>(selectionModel.availableDefinitionsProperty());
        definitionView.getSelectionModel().selectedItemProperty()
                .addListener((p, ov, nv) -> selectionModel.selectedModelDefinitionProperty().set(nv));
        selectionModel.selectedModelDefinitionProperty()
                .addListener((p, ov, nv) -> definitionView.getSelectionModel().select(nv));

        definitionView.setPrefHeight(200);
        TitledPane definitionPane = new TitledPane("Model Definitions", definitionView);
        setFontWeight(definitionPane, FontWeight.BOLD);
        definitionPane.setCollapsible(false);
        getChildren().add(definitionPane);

        TitledPane detailPane = new TitledPane();
        setFontWeight(detailPane, FontWeight.BOLD);
        detailPane.setCollapsible(false);
        detailPane.textProperty().bind(selectionModel.selectedModelDefinitionProperty().asString());
        getChildren().add(detailPane);

        ComboBox<SequenceDefinition> sequenceCombo = new ComboBox<>();
        sequenceCombo.setMinWidth(COMBOBOX_MIN_WIDTH);
        sequenceCombo.itemsProperty().bind(selectionModel.availableSequencesProperty());
        sequenceCombo.getSelectionModel().selectedItemProperty()
                .addListener((p, ov, nv) -> selectionModel.selectedSequenceProperty().set(nv));
        selectionModel.selectedSequenceProperty()
                .addListener((p, ov, nv) -> sequenceCombo.getSelectionModel().select(nv));

        ComboBox<RangeDefinition> rangeCombo = new ComboBox<>();
        rangeCombo.setMinWidth(COMBOBOX_MIN_WIDTH);
        rangeCombo.itemsProperty().bind(selectionModel.availableRangesProperty());
        rangeCombo.getSelectionModel().selectedItemProperty()
                .addListener((p, ov, nv) -> selectionModel.selectedRangeProperty().set(nv));
        selectionModel.selectedRangeProperty().addListener((p, ov, nv) -> rangeCombo.getSelectionModel().select(nv));

        ListView<OpticsDefinition> opticsDefinitions = new ListView<>();

        opticsDefinitions.setPrefHeight(200);
        opticsDefinitions.setPrefWidth(300);
        opticsDefinitions.itemsProperty().bind(selectionModel.availableOpticsProperty());
        opticsDefinitions.getSelectionModel().selectedItemProperty()
                .addListener((p, ov, nv) -> selectionModel.selectedOpticsProperty().set(nv));

        VBox modelDetailsBox = new VBox();
        modelDetailsBox.getChildren().add(new Label("Sequence:"));
        modelDetailsBox.getChildren().add(sequenceCombo);
        modelDetailsBox.getChildren().add(new Label("Range:"));
        modelDetailsBox.getChildren().add(rangeCombo);
        modelDetailsBox.getChildren().add(new Label("Optics:"));
        modelDetailsBox.getChildren().add(opticsDefinitions);
        detailPane.setContent(modelDetailsBox);
        selectionModel.selectedOpticsProperty()
                .addListener((p, ov, nv) -> opticsDefinitions.getSelectionModel().select(nv));
    }


}
