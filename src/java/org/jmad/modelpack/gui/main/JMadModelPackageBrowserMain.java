/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.main;

import org.jmad.modelpack.gui.panes.ModelPackagesPane;
import org.jmad.modelpack.gui.panes.ModelRepositoryPane;
import org.jmad.modpack.service.JMadModelPackageService;
import org.jmad.modpack.service.ModelPackageRepositoryManager;
import org.jmad.modpack.service.conf.JMadModelPackageServiceConfiguration;
import org.minifx.workbench.MiniFx;
import org.minifx.workbench.annotations.Name;
import org.minifx.workbench.annotations.View;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

@Configuration
@Import(value = JMadModelPackageServiceConfiguration.class)
public class JMadModelPackageBrowserMain {

    @View
    @Name("packages")
    @Order(0)
    @Bean
    public ModelPackagesPane packagesPane(JMadModelPackageService packageService) {
        return new ModelPackagesPane(packageService);
    }
    
    @View
    @Name("repositories")
    @Order(1)
    @Bean
    public ModelRepositoryPane repoListView(ModelPackageRepositoryManager manager) {
        return new ModelRepositoryPane(manager);
    }

    public static final void main(String... args) {
        MiniFx.launcher(JMadModelPackageBrowserMain.class).launch(args);
    }

}
