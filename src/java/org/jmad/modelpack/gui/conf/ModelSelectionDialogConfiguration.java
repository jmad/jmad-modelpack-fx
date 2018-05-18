/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.conf;

import org.jmad.modelpack.gui.dialogs.JMadModelSelectionDialog;
import org.jmad.modelpack.gui.panes.JMadModelDefinitionSelectionControl;
import org.jmad.modelpack.gui.panes.JMadModelPackagesSelectionPane;
import org.jmad.modelpack.gui.panes.ModelPackSelectionState;
import org.jmad.modelpack.gui.panes.ModelRepositoryPane;
import org.jmad.modelpack.gui.util.GuiUtils;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.ModelPackageRepositoryManager;
import org.jmad.modelpack.service.conf.JMadModelPackageServiceConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;

@Configuration
@Import(value = JMadModelPackageServiceConfiguration.class)
public class ModelSelectionDialogConfiguration {

    @Bean
    public JMadModelSelectionDialog jmadModelSelectionDialog(Node fullSelectionPane, ModelRepositoryPane repoListView,
            ModelPackSelectionState selectionModel) {
        JMadModelSelectionDialog dialog = new JMadModelSelectionDialog(fullSelectionPane, repoListView, selectionModel);

        dialog.setResizable(true);
        dialog.initModality(Modality.NONE);
        
        return dialog;
    }
    
    
    @Bean
    public JMadModelPackagesSelectionPane packageBrowser(JMadModelPackageService packageService,
            ModelPackSelectionState modelPackSelectionState) {
        return new JMadModelPackagesSelectionPane(packageService, modelPackSelectionState);
    }

    @Bean
    public Node fullSelectionPane(JMadModelPackagesSelectionPane packageBrowser,
            ModelPackSelectionState jmadModelDefinitionSelectionModel) {
        JMadModelDefinitionSelectionControl selectionPane = new JMadModelDefinitionSelectionControl(
                jmadModelDefinitionSelectionModel);
        HBox pane = new HBox(packageBrowser, selectionPane);
        pane.setFillHeight(true);
        pane.setPadding(new Insets(GuiUtils.DEFAULT_SPACING));
        pane.setSpacing(GuiUtils.DEFAULT_SPACING);
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setFillHeight(true);
        HBox.setHgrow(packageBrowser, Priority.ALWAYS);
        return pane;
    }

    @Bean
    public ModelPackSelectionState packageSelectionModel(JMadModelPackageService packageService) {
        return new ModelPackSelectionState(packageService);
    }

    @Bean
    public ModelRepositoryPane repoListView(ModelPackageRepositoryManager manager) {
        return new ModelRepositoryPane(manager);
    }


}
