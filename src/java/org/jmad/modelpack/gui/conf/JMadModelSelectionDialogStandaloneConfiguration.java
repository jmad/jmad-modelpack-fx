/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.conf;

import org.jmad.modelpack.service.conf.JMadModelPackageServiceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Ready to use configuration for the jmad-modelpack-gui project. If you need more fine grained management of the beans,
 * you can use the {@link JMadModelSelectionDialogConfiguration}.
 */
@Configuration
@Import({JMadModelSelectionDialogConfiguration.class, JMadModelPackageServiceConfiguration.class})
public class JMadModelSelectionDialogStandaloneConfiguration {
    /* meta-configuration */
}
