/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import static java.util.Objects.requireNonNull;
import static org.jmad.modelpack.gui.util.FxUtils.glueToAnchorPane;
import static org.jmad.modelpack.gui.util.FxUtils.onChange;
import static org.jmad.modelpack.gui.util.FxUtils.setFontWeight;
import static org.jmad.modelpack.gui.util.GuiUtils.DEFAULT_SPACING_INSETS;

import cern.accsoft.steering.jmad.domain.machine.RangeDefinition;
import cern.accsoft.steering.jmad.domain.machine.SequenceDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.OpticsDefinition;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import org.jmad.modelpack.gui.util.GuiUtils;

public class JMadModelDefinitionSelectionControl extends AnchorPane {

    private static final double COMBOBOX_MIN_WIDTH = 150;
    private final ModelPackSelectionState state;

    public JMadModelDefinitionSelectionControl(ModelPackSelectionState state) {
        this.state = requireNonNull(state, "state must not be null");
        init();
    }

    private void init() {
        TitledPane modelDefinitionPane = createModelDefinitionPane(state);
        TitledPane opticsPane = createOpticsPane(state);

        VBox box = new VBox();
        box.setFillWidth(true);
        box.setSpacing(GuiUtils.DEFAULT_SPACING);
        VBox.setVgrow(modelDefinitionPane, Priority.ALWAYS);
        box.getChildren().addAll(modelDefinitionPane, opticsPane);

        glueToAnchorPane(box);
        getChildren().add(box);
    }

    private static TitledPane createModelDefinitionPane(ModelPackSelectionState state) {
        ListView<JMadModelDefinition> definitionView = new ListView<>(state.availableDefinitionsProperty());
        definitionView.getSelectionModel().selectedItemProperty().addListener(onChange(state.selectedModelDefinitionProperty()::set));
        state.selectedModelDefinitionProperty().addListener(onChange(definitionView.getSelectionModel()::select));

        TitledPane definitionPane = new TitledPane("Model Definitions", definitionView);
        definitionPane.setCollapsible(false);
        setFontWeight(definitionPane, FontWeight.BOLD);
        return definitionPane;
    }

    private static TitledPane createOpticsPane(ModelPackSelectionState state) {
        ComboBox<SequenceDefinition> sequenceCombo = new ComboBox<>();
        sequenceCombo.setMinWidth(COMBOBOX_MIN_WIDTH);
        sequenceCombo.itemsProperty().bind(state.availableSequencesProperty());
        sequenceCombo.getSelectionModel().selectedItemProperty().addListener(onChange(state.selectedSequenceProperty()::set));
        state.selectedSequenceProperty().addListener(onChange(sequenceCombo.getSelectionModel()::select));

        ComboBox<RangeDefinition> rangeCombo = new ComboBox<>();
        rangeCombo.setMinWidth(COMBOBOX_MIN_WIDTH);
        rangeCombo.itemsProperty().bind(state.availableRangesProperty());
        rangeCombo.getSelectionModel().selectedItemProperty().addListener(onChange(state.selectedRangeProperty()::set));
        state.selectedRangeProperty().addListener(onChange(rangeCombo.getSelectionModel()::select));

        ListView<OpticsDefinition> opticsDefinitions = new ListView<>();
        opticsDefinitions.itemsProperty().bind(state.availableOpticsProperty());
        opticsDefinitions.getSelectionModel().selectedItemProperty().addListener(onChange(state.selectedOpticsProperty()::set));
        state.selectedOpticsProperty().addListener(onChange(opticsDefinitions.getSelectionModel()::select));

        VBox box = new VBox();
        box.setPadding(DEFAULT_SPACING_INSETS);
        box.setFillWidth(true);
        VBox.setVgrow(opticsDefinitions, Priority.ALWAYS);
        box.getChildren().add(new Label("Sequence:"));
        box.getChildren().add(sequenceCombo);
        box.getChildren().add(new Label("Range:"));
        box.getChildren().add(rangeCombo);
        box.getChildren().add(new Label("Optics:"));
        box.getChildren().add(opticsDefinitions);

        TitledPane titlePane = new TitledPane();
        titlePane.setCollapsible(false);
        titlePane.textProperty().bind(state.selectedModelDefinitionProperty().asString());
        titlePane.setContent(box);
        setFontWeight(titlePane, FontWeight.BOLD);
        return titlePane;
    }

}
