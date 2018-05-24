/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import static org.jmad.modelpack.domain.VariantType.BRANCH;
import static org.jmad.modelpack.domain.VariantType.RELEASE;
import static org.jmad.modelpack.domain.VariantType.TAG;
import static org.jmad.modelpack.gui.util.GuiUtils.DEFAULT_SPACING;
import static org.jmad.modelpack.gui.util.GuiUtils.DEFAULT_SPACING_INSETS;

import java.util.function.Predicate;

import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.VariantType;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

public class VariantTypeFilterControl extends VBox {

    private final BooleanProperty showReleases = new SimpleBooleanProperty(true);
    private final BooleanProperty showTags = new SimpleBooleanProperty(false);
    private final BooleanProperty showBranches = new SimpleBooleanProperty(false);

    private final ObjectProperty<Predicate<ModelPackageVariant>> predicate = new SimpleObjectProperty<>(pv -> true);

    {
        ObjectBinding<Predicate<ModelPackageVariant>> objectBinding = Bindings.createObjectBinding(() -> pv -> {
            VariantType variant = pv.variant().type();
            if ((RELEASE == variant) && !showReleases.get()) {
                return false;
            }
            if ((TAG == variant) && !showTags.get()) {
                return false;
            }
            if ((BRANCH == variant) && !showBranches.get()) {
                return false;
            }
            return true;
        }, showTags, showReleases, showBranches);

        predicate.bind(objectBinding);
    }

    public VariantTypeFilterControl() {
        setSpacing(DEFAULT_SPACING);
        setPadding(DEFAULT_SPACING_INSETS);
        addCheckBox("releases", showReleases);
        addCheckBox("tags", showTags);
        addCheckBox("branches", showBranches);
    }

    public BooleanProperty showTagsProperty() {
        return showTags;
    }

    public BooleanProperty showReleasesProperty() {
        return showReleases;
    }

    public BooleanProperty showBranchesProperty() {
        return showBranches;
    }

    public ObjectProperty<Predicate<ModelPackageVariant>> variantFilterProperty() {
        return predicate;
    }

    private void addCheckBox(String text, BooleanProperty prop) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(prop.get());
        prop.bind(checkBox.selectedProperty());
        getChildren().add(checkBox);
    }

}
