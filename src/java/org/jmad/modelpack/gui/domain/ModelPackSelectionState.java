/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.domain;

import cern.accsoft.steering.jmad.domain.machine.RangeDefinition;
import cern.accsoft.steering.jmad.domain.machine.SequenceDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.OpticsDefinition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;

import static org.jmad.modelpack.service.JMadModelPackageService.Mode.OFFLINE;
import static org.jmad.modelpack.service.JMadModelPackageService.Mode.ONLINE;

import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.JMadModelPackageService.Mode;

import static freetimelabs.io.reactorfx.schedulers.FxSchedulers.fxThread;
import static javafx.collections.FXCollections.observableArrayList;

public class ModelPackSelectionState {

    private final ObjectProperty<ModelPackageVariant> selectedPackage = new SimpleObjectProperty<>();

    private final ListProperty<JMadModelDefinition> availableDefinitions = new SimpleListProperty<>(
            observableArrayList());
    private final ObjectProperty<JMadModelDefinition> selectedModelDefinition = new SimpleObjectProperty<>();

    private final ListProperty<OpticsDefinition> availableOptics = new SimpleListProperty<>(observableArrayList());
    private final ObjectProperty<OpticsDefinition> selectedOptics = new SimpleObjectProperty<>();

    private final ListProperty<SequenceDefinition> availableSequences = new SimpleListProperty<>(observableArrayList());
    private final ObjectProperty<SequenceDefinition> selectedSequence = new SimpleObjectProperty<>();

    private final ListProperty<RangeDefinition> availableRanges = new SimpleListProperty<>(observableArrayList());
    private final ObjectProperty<RangeDefinition> selectedRange = new SimpleObjectProperty<>();

    private final BooleanProperty onlineMode = new SimpleBooleanProperty(true);

    public ModelPackSelectionState(JMadModelPackageService modelPackageService) {
        selectedPackage.addListener((p, ov, nv) -> {
            if (nv != null) {
                modelPackageService.modelDefinitionsFrom(nv).collectList().publishOn(fxThread()).subscribe(defs -> {
                    availableDefinitions.setAll(defs);
                    if (!defs.isEmpty()) {
                        JMadModelDefinition selectedModelDef = defs.get(0);
                        update(selectedModelDef);
                    }
                });
            }
        });
        
        onlineMode.set(modelPackageService.mode() == ONLINE);
        onlineMode.addListener((p, ov, nv) -> modelPackageService.setMode(nv ? ONLINE : OFFLINE));
        
        selectedModelDefinition.addListener((p, ov, nv) -> update(nv));
    }

    private void update(JMadModelDefinition selectedModelDef) {
        selectedModelDefinitionProperty().set(selectedModelDef);

        if (selectedModelDef == null) {
            return;
        }

        availableOpticsProperty().setAll(selectedModelDef.getOpticsDefinitions());
        update(selectedModelDef.getDefaultOpticsDefinition());

        availableSequencesProperty().setAll(selectedModelDef.getSequenceDefinitions());
        update(selectedModelDef.getDefaultSequenceDefinition());
    }

    private void update(SequenceDefinition sequenceDefinition) {
        selectedSequenceProperty().set(sequenceDefinition);
        if (sequenceDefinition != null) {
            availableRangesProperty().setAll(sequenceDefinition.getRangeDefinitions());
            selectedRangeProperty().set(sequenceDefinition.getDefaultRangeDefinition());
        }
    }

    private void update(OpticsDefinition opticsDefinition) {
        selectedOpticsProperty().set(opticsDefinition);
    }

    public ListProperty<JMadModelDefinition> availableDefinitionsProperty() {
        return availableDefinitions;
    }

    public ObjectProperty<ModelPackageVariant> selectedPackageProperty() {
        return selectedPackage;
    }

    public ObjectProperty<JMadModelDefinition> selectedModelDefinitionProperty() {
        return selectedModelDefinition;
    }

    public ListProperty<OpticsDefinition> availableOpticsProperty() {
        return availableOptics;
    }

    public ObjectProperty<OpticsDefinition> selectedOpticsProperty() {
        return selectedOptics;
    }

    public ListProperty<SequenceDefinition> availableSequencesProperty() {
        return availableSequences;
    }

    public ObjectProperty<SequenceDefinition> selectedSequenceProperty() {
        return selectedSequence;
    }

    public ListProperty<RangeDefinition> availableRangesProperty() {
        return availableRanges;
    }

    public ObjectProperty<RangeDefinition> selectedRangeProperty() {
        return selectedRange;
    }
    
    public BooleanProperty onlineModeProperty() {
        return onlineMode;
    }

}
