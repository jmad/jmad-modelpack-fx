/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.conf;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import org.jmad.modelpack.gui.dialogs.JMadModelSelectionDialog;
import org.jmad.modelpack.gui.domain.ModelPackSelectionState;
import org.jmad.modelpack.gui.panes.JMadModelDefinitionSelectionControl;
import org.jmad.modelpack.gui.panes.JMadModelPackagesSelectionControl;
import org.jmad.modelpack.gui.panes.JMadModelRepositorySelectionControl;
import org.jmad.modelpack.gui.util.GuiUtils;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.ModelPackageRepositoryManager;
import org.jmad.modelpack.service.conf.JMadModelPackageServiceConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = JMadModelPackageServiceConfiguration.class)
public class JMadModelSelectionDialogConfiguration {

    @Bean
    public JMadModelSelectionDialog jmadModelSelectionDialog(Region modelSelectionRegion, Region repositorySelectionControl, ModelPackSelectionState selectionState) {
        JMadModelSelectionDialog dialog = new JMadModelSelectionDialog(modelSelectionRegion, repositorySelectionControl, selectionState);
        dialog.setResizable(true);
        dialog.initModality(Modality.NONE);
        return dialog;
    }

    @Bean
    public JMadModelPackagesSelectionControl packagesSelectionControl(JMadModelPackageService packageService,
                                                            ModelPackSelectionState modelPackSelectionState) {
        return new JMadModelPackagesSelectionControl(packageService, modelPackSelectionState);
    }

    @Bean
    public JMadModelDefinitionSelectionControl definitionSelectionControl(ModelPackSelectionState modelPackSelectionState) {
        return new JMadModelDefinitionSelectionControl(modelPackSelectionState);
    }

    @Bean
    public Region modelSelectionRegion(JMadModelPackagesSelectionControl packagesSelectionControl, JMadModelDefinitionSelectionControl definitionSelectionControl) {
        HBox pane = new HBox(packagesSelectionControl, definitionSelectionControl);
        pane.setFillHeight(true);
        pane.setPadding(GuiUtils.DEFAULT_SPACING_INSETS);
        pane.setSpacing(GuiUtils.DEFAULT_SPACING);
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setFillHeight(true);
        HBox.setHgrow(packagesSelectionControl, Priority.ALWAYS);
        return pane;
    }

    @Bean
    public ModelPackSelectionState modelPackSelectionState(JMadModelPackageService packageService) {
        return new ModelPackSelectionState(packageService);
    }

    @Bean
    public JMadModelRepositorySelectionControl repositorySelectionControl(ModelPackageRepositoryManager manager) {
        return new JMadModelRepositorySelectionControl(manager);
    }


}
