/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import cern.accsoft.steering.jmad.domain.machine.RangeDefinition;
import cern.accsoft.steering.jmad.domain.machine.SequenceDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.OpticsDefinition;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jmad.modelpack.gui.domain.JMadModelSelectionType;
import org.jmad.modelpack.gui.domain.ModelPackSelectionState;
import org.jmad.modelpack.gui.util.GuiUtils;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static org.jmad.modelpack.gui.util.FxUtils.glueToAnchorPane;
import static org.jmad.modelpack.gui.util.FxUtils.onChange;
import static org.jmad.modelpack.gui.util.GuiUtils.DEFAULT_SPACING_INSETS;

public class JMadModelDefinitionSelectionControl extends AnchorPane {

    private static final double COMBOBOX_MIN_WIDTH = 150;
    private final ModelPackSelectionState state;

    public JMadModelDefinitionSelectionControl(ModelPackSelectionState state) {
        this.state = requireNonNull(state, "state must not be null");
        init();
    }

    private void init() {
        SectionPane modelDefinitionPane = createModelDefinitionPane(state);
        SectionPane opticsPane = createOpticsPane(state);

        state.loadingProperty().addListener(onChange(loading -> {
            modelDefinitionPane.loadingOverlayProperty().set(loading);
            opticsPane.loadingOverlayProperty().set(loading);
        }));

        VBox box = new VBox();
        box.setFillWidth(true);
        box.setSpacing(GuiUtils.DEFAULT_SPACING);
        VBox.setVgrow(modelDefinitionPane, Priority.ALWAYS);
        box.getChildren().addAll(modelDefinitionPane, opticsPane);

        glueToAnchorPane(box);
        getChildren().add(box);
    }

    private static SectionPane createModelDefinitionPane(ModelPackSelectionState state) {
        ListView<JMadModelDefinition> definitionView = new ListView<>(state.availableDefinitionsProperty());
        definitionView.getSelectionModel().selectedItemProperty().addListener(onChange(state.selectedModelDefinitionProperty()::set));
        state.selectedModelDefinitionProperty().addListener(onChange(definitionView.getSelectionModel()::select));

        SectionPane sectionPane = new SectionPane("Model Definitions");
        sectionPane.setContent(definitionView);
        return sectionPane;
    }

    private static SectionPane createOpticsPane(ModelPackSelectionState state) {
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

        StackPane sequenceControlContainer = new StackPane();
        StackPane rangeControlContainer = new StackPane();
        StackPane opticsDefinitionControlContainer = new StackPane();

        Consumer<JMadModelSelectionType> modelSelectionTypeUpdater = type -> {
            sequenceControlContainer.getChildren().clear();
            rangeControlContainer.getChildren().clear();
            opticsDefinitionControlContainer.getChildren().clear();

            if (type == JMadModelSelectionType.ALL) {
                sequenceControlContainer.getChildren().add(sequenceCombo);
                rangeControlContainer.getChildren().add(rangeCombo);
                opticsDefinitionControlContainer.getChildren().add(opticsDefinitions);
            } else if(type == JMadModelSelectionType.MODEL_DEFINITION_ONLY) {
                sequenceControlContainer.getChildren().add(new Label("ALL"));
                rangeControlContainer.getChildren().add(new Label("ALL"));
                opticsDefinitionControlContainer.getChildren().add(new Label("ALL"));
            }
        };

        state.modelSelectionTypeProperty().addListener(onChange(modelSelectionTypeUpdater));
        modelSelectionTypeUpdater.accept(state.modelSelectionTypeProperty().get());

        VBox box = new VBox();
        box.setPadding(DEFAULT_SPACING_INSETS);
        box.setFillWidth(true);
        VBox.setVgrow(opticsDefinitions, Priority.ALWAYS);
        box.getChildren().add(new Label("Sequence:"));
        box.getChildren().add(sequenceControlContainer);
        box.getChildren().add(new Label("Range:"));
        box.getChildren().add(rangeControlContainer);
        box.getChildren().add(new Label("Optics:"));
        box.getChildren().add(opticsDefinitionControlContainer);

        SectionPane sectionPane = new SectionPane();
        sectionPane.textProperty().bind(state.selectedModelDefinitionProperty().asString());
        sectionPane.setContent(box);
        return sectionPane;
    }

}
