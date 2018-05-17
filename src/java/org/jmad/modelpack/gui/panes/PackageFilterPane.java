/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import static org.jmad.modelpack.gui.util.GuiUtils.DEFAULT_SPACING;

public class PackageFilterPane extends VBox {

    public PackageFilterPane(PackageFilterModel filterModel) {
        setPrefWidth(120);
        setSpacing(DEFAULT_SPACING);
        setPadding(new Insets(DEFAULT_SPACING));
        addCheckBox("releases", filterModel.showReleasesProperty());
        addCheckBox("tags", filterModel.showTagsProperty());
        addCheckBox("branches", filterModel.showBranchesProperty());
    }

    private void addCheckBox(String text, BooleanProperty prop) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(prop.get());
        prop.bind(checkBox.selectedProperty());
        getChildren().add(checkBox);
    }

}
