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

public class JMadModelDefinitionSelectionPane extends AnchorPane {

    private static final double COMBOBOX_MIN_WIDTH = 150;
    private final ModelPackSelectionState selectionModel;

    public JMadModelDefinitionSelectionPane(ModelPackSelectionState selectionModel) {
        this.selectionModel = requireNonNull(selectionModel, "selectionModel must not be null");

        init();
    }

    private void init() {
        TitledPane modelDefinitionPane = createModelDefinitionPane(selectionModel);
        TitledPane modelDetailedPane = createModelDetailedPane(selectionModel);
        VBox box = new VBox();
        VBox.setVgrow(modelDefinitionPane, Priority.ALWAYS);
        box.setFillWidth(true);
        box.setSpacing(GuiUtils.DEFAULT_SPACING);
        box.getChildren().addAll(modelDefinitionPane, modelDetailedPane);

        glueToAnchorPane(box);
        getChildren().add(box);
    }

    private static TitledPane createModelDefinitionPane(ModelPackSelectionState selectionModel) {
        ListView<JMadModelDefinition> definitionView = new ListView<>(selectionModel.availableDefinitionsProperty());
        definitionView.getSelectionModel().selectedItemProperty()
                .addListener((p, ov, nv) -> selectionModel.selectedModelDefinitionProperty().set(nv));
        selectionModel.selectedModelDefinitionProperty()
                .addListener((p, ov, nv) -> definitionView.getSelectionModel().select(nv));

//        definitionView.setMinHeight(200);
        TitledPane definitionPane = new TitledPane("Model Definitions", definitionView);
        setFontWeight(definitionPane, FontWeight.BOLD);
        definitionPane.setCollapsible(false);
        return definitionPane;
    }

    private static TitledPane createModelDetailedPane(ModelPackSelectionState pkgSelectionModel) {
        ComboBox<SequenceDefinition> sequenceCombo = new ComboBox<>();
        sequenceCombo.setMinWidth(COMBOBOX_MIN_WIDTH);
        sequenceCombo.itemsProperty().bind(pkgSelectionModel.availableSequencesProperty());
        sequenceCombo.getSelectionModel().selectedItemProperty()
                .addListener(onChange(pkgSelectionModel.selectedSequenceProperty()::set));
        pkgSelectionModel.selectedSequenceProperty().addListener(onChange(sequenceCombo.getSelectionModel()::select));

        ComboBox<RangeDefinition> rangeCombo = new ComboBox<>();
        rangeCombo.setMinWidth(COMBOBOX_MIN_WIDTH);
        rangeCombo.itemsProperty().bind(pkgSelectionModel.availableRangesProperty());
        rangeCombo.getSelectionModel().selectedItemProperty()
                .addListener(onChange(pkgSelectionModel.selectedRangeProperty()::set));
        pkgSelectionModel.selectedRangeProperty().addListener(onChange(rangeCombo.getSelectionModel()::select));

        ListView<OpticsDefinition> opticsDefinitions = new ListView<>();
//        opticsDefinitions.setPrefWidth(300);
        opticsDefinitions.itemsProperty().bind(pkgSelectionModel.availableOpticsProperty());
        opticsDefinitions.getSelectionModel().selectedItemProperty()
                .addListener(onChange(pkgSelectionModel.selectedOpticsProperty()::set));
        pkgSelectionModel.selectedOpticsProperty().addListener(onChange(opticsDefinitions.getSelectionModel()::select));

        VBox modelDetailsBox = new VBox();
        VBox.setVgrow(opticsDefinitions, Priority.ALWAYS);
        modelDetailsBox.setPadding(DEFAULT_SPACING_INSETS);
        modelDetailsBox.getChildren().add(new Label("Sequence:"));
        modelDetailsBox.getChildren().add(sequenceCombo);
        modelDetailsBox.getChildren().add(new Label("Range:"));
        modelDetailsBox.getChildren().add(rangeCombo);
        modelDetailsBox.getChildren().add(new Label("Optics:"));
        modelDetailsBox.getChildren().add(opticsDefinitions);
        modelDetailsBox.setFillWidth(true);

        TitledPane detailPane = new TitledPane();
        setFontWeight(detailPane, FontWeight.BOLD);
        detailPane.setCollapsible(false);
        detailPane.textProperty().bind(pkgSelectionModel.selectedModelDefinitionProperty().asString());
        detailPane.setContent(modelDetailsBox);
        return detailPane;
    }

}
