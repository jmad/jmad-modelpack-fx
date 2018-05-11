/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import cern.accsoft.steering.jmad.model.JMadModelStartupConfiguration;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;

public class SelectedModelConfiguration {

    private final JMadModelDefinition modelDefinition;
    private final JMadModelStartupConfiguration startupConfiguration;

    public SelectedModelConfiguration(JMadModelDefinition modelDefinition,
            JMadModelStartupConfiguration startupConfiguration) {
        this.modelDefinition = requireNonNull(modelDefinition, "modelDefinition must not be null");
        this.startupConfiguration = requireNonNull(startupConfiguration, "startupConfiguration must not be null");
    }

    public JMadModelDefinition modelDefinition() {
        return modelDefinition;
    }

    public JMadModelStartupConfiguration startupConfiguration() {
        return startupConfiguration;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((modelDefinition == null) ? 0 : modelDefinition.hashCode());
        result = prime * result + ((startupConfiguration == null) ? 0 : startupConfiguration.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SelectedModelConfiguration other = (SelectedModelConfiguration) obj;
        if (modelDefinition == null) {
            if (other.modelDefinition != null) {
                return false;
            }
        } else if (!modelDefinition.equals(other.modelDefinition)) {
            return false;
        }
        if (startupConfiguration == null) {
            if (other.startupConfiguration != null) {
                return false;
            }
        } else if (!startupConfiguration.equals(other.startupConfiguration)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SelectedModelConfiguration [modelDefinition=" + modelDefinition + ", startupConfiguration="
                + startupConfiguration + "]";
    }

}
