/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.domain;

import static freetimelabs.io.reactorfx.schedulers.FxSchedulers.fxThread;
import static javafx.collections.FXCollections.observableArrayList;
import static org.jmad.modelpack.gui.util.FxUtils.onChange;
import static org.jmad.modelpack.service.JMadModelPackageService.Mode.OFFLINE;
import static org.jmad.modelpack.service.JMadModelPackageService.Mode.ONLINE;

import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.service.JMadModelPackageService;

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

    private final ObjectProperty<JMadModelSelectionType> modelSelectionType = new SimpleObjectProperty<>(JMadModelSelectionType.ALL);

    private final BooleanProperty onlineMode = new SimpleBooleanProperty(true);

    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    public ModelPackSelectionState(JMadModelPackageService modelPackageService) {
        selectedPackage.addListener(onChange(newSelectedPackage -> {
            if (newSelectedPackage == null) {
                return;
            }
            modelPackageService.modelDefinitionsFrom(newSelectedPackage).collectList()
                    .publishOn(fxThread())
                    .doOnSubscribe(s -> loading.set(true))
                    .doOnTerminate(() -> loading.set(false))
                    .subscribe(defs -> {
                        availableDefinitions.setAll(defs);
                        if (!defs.isEmpty()) {
                            JMadModelDefinition selectedModelDef = defs.get(0);
                            updateSelectedModelDefinition(selectedModelDef);
                        }
                    });
        }));

        onlineMode.set(modelPackageService.mode() == ONLINE);
        onlineMode.addListener(onChange(isOnline -> modelPackageService.setMode(isOnline ? ONLINE : OFFLINE)));
        
        selectedModelDefinition.addListener(onChange(this::updateSelectedModelDefinition));
        selectedSequence.addListener(onChange(this::updateSequenceDefinition));
    }

    private void updateSelectedModelDefinition(JMadModelDefinition selectedModelDef) {
        selectedModelDefinitionProperty().set(selectedModelDef);

        if (selectedModelDef == null) {
            return;
        }

        availableOpticsProperty().setAll(selectedModelDef.getOpticsDefinitions());
        updateOpticsDefinition(selectedModelDef.getDefaultOpticsDefinition());

        availableSequencesProperty().setAll(selectedModelDef.getSequenceDefinitions());
        updateSequenceDefinition(selectedModelDef.getDefaultSequenceDefinition());
    }

    private void updateSequenceDefinition(SequenceDefinition sequenceDefinition) {
        selectedSequenceProperty().set(sequenceDefinition);
        if (sequenceDefinition != null) {
            availableRangesProperty().setAll(sequenceDefinition.getRangeDefinitions());
            selectedRangeProperty().set(sequenceDefinition.getDefaultRangeDefinition());
        }
    }

    private void updateOpticsDefinition(OpticsDefinition opticsDefinition) {
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

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public ObjectProperty<JMadModelSelectionType> modelSelectionTypeProperty() {
        return modelSelectionType;
    }
}
