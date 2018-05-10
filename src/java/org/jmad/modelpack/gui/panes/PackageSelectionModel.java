/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import org.jmad.modelpack.domain.ModelPackageVariant;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PackageSelectionModel {
    
    private final ObjectProperty<ModelPackageVariant> selectedPackage = new SimpleObjectProperty<>();

    public ObjectProperty<ModelPackageVariant> selectedPackageProperty() {
        return selectedPackage;
    }

}
